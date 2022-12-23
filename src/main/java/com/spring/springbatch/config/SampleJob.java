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
import org.springframework.batch.item.adapter.ItemWriterAdapter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.ItemPreparedStatementSetter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileFooterCallback;
import org.springframework.batch.item.file.FlatFileHeaderCallback;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldExtractor;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.xml.StaxEventItemReader;

import org.springframework.batch.item.xml.StaxEventItemWriter;
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
import java.io.IOException;
import java.io.Writer;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

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
                .<StudentCsv, StudentCsv>chunk(2)
//                .reader(responseItemReaderAdapter())
                .reader(csvFlatFileItemReader())
//                .processor(firstItemProcessor)
//                .writer(firstItemWriter)
//                .writer(csvFlatFileItemWriter())
                .writer(responseItemWriterAdapter())
                .build();

    }

    //-------------------ITEM READERS------------------//

    // CSV Item Reader
    public FlatFileItemReader<StudentCsv> csvFlatFileItemReader() {
        FlatFileItemReader<StudentCsv> csvFlatFileItemReader =
                new FlatFileItemReader<StudentCsv>();

        csvFlatFileItemReader.setResource(
                new FileSystemResource(
                        new File("C:\\Users\\batym\\OneDrive\\Рабочий стол\\spring-batch\\inputFiles\\students.csv")
                ));
        // 1 method
        csvFlatFileItemReader.setLineMapper(new DefaultLineMapper<StudentCsv>() {
            {
                setLineTokenizer(new DelimitedLineTokenizer("|") {
                    {
                        setNames("ID", "First Name", "Last Name", "Email");
                    }
                });
                setFieldSetMapper(new BeanWrapperFieldSetMapper<StudentCsv>() {
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
    public JsonItemReader<StudentJson> jsonItemReader() {
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
    public StaxEventItemReader<StudentXML> xmlStaxEventItemReader() {
        StaxEventItemReader<StudentXML> studentXMLStaxEventItemReader =
                new StaxEventItemReader<>();
        studentXMLStaxEventItemReader.setResource(new FileSystemResource(
                new File("C:\\Users\\batym\\OneDrive\\Рабочий стол\\spring-batch\\inputFiles\\students.xml")
        ));
        studentXMLStaxEventItemReader.setFragmentRootElementName("student");
        studentXMLStaxEventItemReader.setUnmarshaller(new Jaxb2Marshaller() {
            {
                setClassesToBeBound(StudentXML.class);
            }
        });

        return studentXMLStaxEventItemReader;
    }

    // JDBC Item Reader
    public JdbcCursorItemReader<StudentJDBC> jdbcCursorItemReader() {
        JdbcCursorItemReader<StudentJDBC> studentJDBCJdbcCursorItemReader =
                new JdbcCursorItemReader<>();

        studentJDBCJdbcCursorItemReader.setDataSource(dataSource);
        studentJDBCJdbcCursorItemReader.setSql(
                "select id, first_name as firstName, last_name as lastName, email " +
                        "from students");
        studentJDBCJdbcCursorItemReader.setRowMapper(new BeanPropertyRowMapper<StudentJDBC>() {
            {
                setMappedClass(StudentJDBC.class);
            }
        });
        return studentJDBCJdbcCursorItemReader;
    }

    // REST API Item Reader
    public ItemReaderAdapter<StudentResponse> responseItemReaderAdapter() {
        ItemReaderAdapter<StudentResponse> studentResponseItemReaderAdapter =
                new ItemReaderAdapter<>();

        studentResponseItemReaderAdapter.setTargetObject(studentService);
        studentResponseItemReaderAdapter.setTargetMethod("getStudent");
        return studentResponseItemReaderAdapter;
    }

    //------------------ITEM WRITERS---------------/

    // CSV Item Writer
    public FlatFileItemWriter<StudentJDBC> csvFlatFileItemWriter() {
        FlatFileItemWriter<StudentJDBC> flatFileItemWriter =
                new FlatFileItemWriter<>();
        flatFileItemWriter.setResource(new FileSystemResource(
                new File("C:\\Users\\batym\\OneDrive\\Рабочий стол\\spring-batch\\outputFiles\\student.csv")
        ));
        flatFileItemWriter.setHeaderCallback(new FlatFileHeaderCallback() {
            @Override
            public void writeHeader(Writer writer) throws IOException {
                writer.write("Id, First Name, Last Name, Email");
            }
        });

        flatFileItemWriter.setLineAggregator(new DelimitedLineAggregator<>() {
            {
                setFieldExtractor(new BeanWrapperFieldExtractor<>() {
                    {
                        setNames(new String[]{"id", "firstName", "lastName", "email"});
                    }
                });
            }
        });

        flatFileItemWriter.setFooterCallback(new FlatFileFooterCallback() {
            @Override
            public void writeFooter(Writer writer) throws IOException {
                writer.write("Created at " + new Date());
            }
        });

        return flatFileItemWriter;
    }

    // JSON Item Writer
    public JsonFileItemWriter<StudentJson> jsonFileItemWriter() {
        FileSystemResource fileSystemResource =
                new FileSystemResource(new File("C:\\Users\\batym\\OneDrive\\Рабочий стол\\spring-batch\\outputFiles\\student.json"));

        JsonFileItemWriter<StudentJson> jsonFileItemWriter =
                new JsonFileItemWriter<>(fileSystemResource, new JacksonJsonObjectMarshaller<>());

        return jsonFileItemWriter;
    }

    // XML Item Writer
    public StaxEventItemWriter<StudentXML> xmlStaxEventItemWriter() {
        StaxEventItemWriter<StudentXML> studentXMLStaxEventItemWriter =
                new StaxEventItemWriter<>();
        studentXMLStaxEventItemWriter.setResource(new FileSystemResource(
                new File("C:\\Users\\batym\\OneDrive\\Рабочий стол\\spring-batch\\outputFiles\\students.xml")
        ));
        studentXMLStaxEventItemWriter.setRootTagName("group");
        studentXMLStaxEventItemWriter.setMarshaller(new Jaxb2Marshaller() {
            {
                setClassesToBeBound(StudentXML.class);
            }
        });

        return studentXMLStaxEventItemWriter;
    }

    // JDBC Item Writer(s)
    @Bean
    public JdbcBatchItemWriter<StudentJDBC> jdbcBatchItemWriter() {
        JdbcBatchItemWriter<StudentJDBC> studentJDBCBatchItemWriter =
                new JdbcBatchItemWriter<>();
        studentJDBCBatchItemWriter.setDataSource(dataSource);
        studentJDBCBatchItemWriter.setSql("insert into students" +
                "(id, first_name, last_name, email) " +
                "values (:id, :firstName, :lastName, :email)");
        studentJDBCBatchItemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());

        return studentJDBCBatchItemWriter;
    }

    @Bean
    public JdbcBatchItemWriter<StudentJDBC> jdbcBatchItemWriter2() {
        JdbcBatchItemWriter<StudentJDBC> studentJDBCBatchItemWriter =
                new JdbcBatchItemWriter<>();
        studentJDBCBatchItemWriter.setDataSource(dataSource);
        studentJDBCBatchItemWriter.setSql("insert into students" +
                "(id, first_name, last_name, email) " +
                "values (?, ?, ?, ?)");
        studentJDBCBatchItemWriter.setItemPreparedStatementSetter(
                new ItemPreparedStatementSetter<StudentJDBC>() {
                    @Override
                    public void setValues(StudentJDBC studentJDBC, PreparedStatement ps) throws SQLException {
                        ps.setLong(1, studentJDBC.getId());
                        ps.setString(2, studentJDBC.getFirstName());
                        ps.setString(3, studentJDBC.getLastName());
                        ps.setString(4, studentJDBC.getEmail());
                    }
                }
        );

        return studentJDBCBatchItemWriter;
    }

    // POST API Item Writer
    public ItemWriterAdapter<StudentCsv> responseItemWriterAdapter() {
        ItemWriterAdapter<StudentCsv> studentResponseItemWriterAdapter =
                new ItemWriterAdapter<>();

        studentResponseItemWriterAdapter.setTargetObject(studentService);
        studentResponseItemWriterAdapter.setTargetMethod("restCallToCreateStudent");
        return studentResponseItemWriterAdapter;
    }
}
