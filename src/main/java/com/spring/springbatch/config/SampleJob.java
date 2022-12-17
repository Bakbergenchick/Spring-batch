package com.spring.springbatch.config;

import com.spring.springbatch.listener.FirstJobListener;
import com.spring.springbatch.listener.FirstStepListener;
import com.spring.springbatch.model.*;
import com.spring.springbatch.processor.FirstItemProcessor;
import com.spring.springbatch.reader.FirstItemReader;
import com.spring.springbatch.service.FirstTasklet;
import com.spring.springbatch.service.SecondTasklet;
import com.spring.springbatch.service.StudentService;
import com.spring.springbatch.writer.FirstItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.adapter.ItemReaderAdapter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.xml.StaxEventItemReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.File;

@Configuration
@SuppressWarnings("ALL")
public class SampleJob {

    @Autowired
    private FirstTasklet firstTasklet;
    @Autowired
    private SecondTasklet secondTasklet;
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private FirstJobListener firstJobListener;
    @Autowired
    private FirstStepListener firstStepListener;
    @Autowired
    private FirstItemReader firstItemReader;
    @Autowired
    private FirstItemProcessor firstItemProcessor;
    @Autowired
    private FirstItemWriter firstItemWriter;

    @Autowired
    private StudentService studentService;

    @Autowired
    @Qualifier("universityDataSource")
    private DataSource dataSource;

    public Step firstStep() {
        return this.stepBuilderFactory.get("firstStep")
                .tasklet(firstTasklet)
                .listener(firstStepListener)// or .chunk()
                .build();
    }

    public Step secondStep() {
        return this.stepBuilderFactory.get("secondStep")
                .tasklet(secondTasklet) // or .chunk()
                .build();
    }

    @Bean
    public Job firstJob() {
        return this.jobBuilderFactory.get("firstJob")
                .incrementer(new RunIdIncrementer())
                .start(firstStep())
                .next(secondStep())
                .listener(firstJobListener)
                .build();
    }

    @Bean
    public Job secondJob() {
        return this.jobBuilderFactory.get("secondJob")
                .incrementer(new RunIdIncrementer())
                .start(firstChunkStep()) // chunk-oriented step
                .next(secondStep()) // tasklet step
                .build();
    }

    public Step firstChunkStep() {
        return stepBuilderFactory.get("firstChunk")
                .<StudentResponse, StudentResponse>chunk(2)
                .reader(responseItemReaderAdapter())
//                .processor(firstItemProcessor)
                .writer(firstItemWriter)
                .build();
    }

    // CSV Item Reader
    public FlatFileItemReader<StudentCsv> csvFlatFileItemReader(){
        FlatFileItemReader<StudentCsv> csvFlatFileItemReader =
                new FlatFileItemReader<StudentCsv>();

        csvFlatFileItemReader.setResource(
                new FileSystemResource(
                        new File("C:\\Users\\batym\\OneDrive\\Рабочий стол\\spring-batch\\inputFiles\\students.csv")
                ));
        // 1 method
        csvFlatFileItemReader.setLineMapper(new DefaultLineMapper<StudentCsv>(){
            {
                setLineTokenizer(new DelimitedLineTokenizer(","){
                    {
                        setNames("ID","First Name","Last Name","Email");
                    }
                });
                setFieldSetMapper(new BeanWrapperFieldSetMapper<StudentCsv>(){
                    {
                        setTargetType(StudentCsv.class);
                    }
                });
            }
        });
        // 2 method
//        DefaultLineMapper<StudentCsv> csvDefaultLineMapper =
//                new DefaultLineMapper<>();
//        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
//
//        delimitedLineTokenizer.setNames("ID","First Name","Last Name","Email");
//
//        csvDefaultLineMapper.setLineTokenizer(delimitedLineTokenizer);
//
//        BeanWrapperFieldSetMapper<StudentCsv> fieldSetMapper =
//                new BeanWrapperFieldSetMapper<>();
//
//        fieldSetMapper.setTargetType(StudentCsv.class);
//
//        csvDefaultLineMapper.setFieldSetMapper(fieldSetMapper);
//
//        csvFlatFileItemReader.setLineMapper(csvDefaultLineMapper);
        ////////////
        csvFlatFileItemReader.setLinesToSkip(1);

        return csvFlatFileItemReader;
    }

    // JSON Item Reader
    public JsonItemReader<StudentJson> jsonItemReader(){
        JsonItemReader<StudentJson> studentCsvJsonItemReader =
                new JsonItemReader<>();
        studentCsvJsonItemReader.setResource(new FileSystemResource(
                new File("C:\\Users\\batym\\OneDrive\\Рабочий стол\\spring-batch\\inputFiles\\students.json")
        ));

        studentCsvJsonItemReader.setJsonObjectReader(
                new JacksonJsonObjectReader<>(StudentJson.class)
        );

        studentCsvJsonItemReader.setMaxItemCount(5);

        return studentCsvJsonItemReader;
    }

    // XML Item Reader
    public StaxEventItemReader<StudentXML> xmlStaxEventItemReader(){
        StaxEventItemReader<StudentXML> studentXMLStaxEventItemReader =
                new StaxEventItemReader<>();
        studentXMLStaxEventItemReader.setResource(new FileSystemResource(
                new File("C:\\Users\\batym\\OneDrive\\Рабочий стол\\spring-batch\\inputFiles\\students.xml")
        ));
        studentXMLStaxEventItemReader.setFragmentRootElementName("student");
        studentXMLStaxEventItemReader.setUnmarshaller(new Jaxb2Marshaller(){
            {
                setClassesToBeBound(StudentXML.class);
            }
        });

        return studentXMLStaxEventItemReader;
    }

    // JDBC Item Reader
    public JdbcCursorItemReader<StudentJDBC> jdbcCursorItemReader(){
        JdbcCursorItemReader<StudentJDBC> studentJDBCJdbcCursorItemReader =
                new JdbcCursorItemReader<>();

        studentJDBCJdbcCursorItemReader.setDataSource(dataSource);
        studentJDBCJdbcCursorItemReader.setSql(
                "select id, first_name as firstName, last_name as lastName, email " +
                "from students");
        studentJDBCJdbcCursorItemReader.setRowMapper(new BeanPropertyRowMapper<StudentJDBC>(){
            {
                setMappedClass(StudentJDBC.class);
            }
        });
        return studentJDBCJdbcCursorItemReader;
    }

    // REST API Item Reader
    public ItemReaderAdapter<StudentResponse> responseItemReaderAdapter(){
        ItemReaderAdapter<StudentResponse> studentResponseItemReaderAdapter =
                new ItemReaderAdapter<>();

        studentResponseItemReaderAdapter.setTargetObject(studentService);
        studentResponseItemReaderAdapter.setTargetMethod("getStudent");
        return studentResponseItemReaderAdapter;
    }


}
