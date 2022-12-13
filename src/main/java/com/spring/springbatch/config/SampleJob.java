package com.spring.springbatch.config;

import com.spring.springbatch.listener.FirstJobListener;
import com.spring.springbatch.listener.FirstStepListener;
import com.spring.springbatch.processor.FirstItemProcessor;
import com.spring.springbatch.reader.FirstItemReader;
import com.spring.springbatch.service.FirstTasklet;
import com.spring.springbatch.service.SecondTasklet;
import com.spring.springbatch.writer.FirstItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
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

//        @Bean
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
                .start(firstChunkStep())
                .build();
    }

    public Step firstChunkStep() {
        return stepBuilderFactory.get("firstChunk")
                .<Integer, Long>chunk(2)
                .reader(firstItemReader)
                .processor(firstItemProcessor)
                .writer(firstItemWriter)
                .build();
    }



}
