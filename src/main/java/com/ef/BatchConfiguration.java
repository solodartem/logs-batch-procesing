package com.ef;


import com.ef.batch.processor.AccessLogItemProcessor;
import com.ef.batch.listener.JobCompletionNotificationListener;
import com.ef.model.AccessLogObject;
import com.ef.model.AccessLogString;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    @Autowired
    private StepBuilderFactory steps;

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job jobAnalyzeLogs(JobCompletionNotificationListener listener, Tasklet cleanDBDataTasklet, Step stepImportLogs) {
        return jobBuilderFactory.get("jobAnalyzeLogs")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(steps.get("cleanDB").tasklet(cleanDBDataTasklet).build())
                .next(stepImportLogs)
                .build();
    }

    @Bean
    public Step stepImportLogs(JdbcBatchItemWriter<AccessLogObject> dbWriter) {
        return stepBuilderFactory.get("stepImportLogs")
                .<AccessLogString, AccessLogObject>chunk(100)
                .reader(reader())
                .processor(processor())
                .writer(dbWriter)
                .build();
    }

    @Bean
    public FlatFileItemReader<AccessLogString> reader() {
        return new FlatFileItemReaderBuilder<AccessLogString>()
                .name("personItemReader")
                .resource(new ClassPathResource("access.log"))
                .delimited()
                .delimiter("|")
                .names(new String[]{"date", "IP", "URL", "HTTPStatus", "client"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<AccessLogString>() {{
                    setTargetType(AccessLogString.class);
                }})
                .build();
    }

    @Bean
    public AccessLogItemProcessor processor() {
        return new AccessLogItemProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<AccessLogObject> dbWriter(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<AccessLogObject>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO access_logs (date, IP, URL, HTTPStatus, client) VALUES (:date, :IP, :URL, :HTTPStatus, :client)")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public Tasklet cleanDBDataTasklet(DataSource dataSource) {
        return (contribution, chunkContext) -> {
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            jdbcTemplate.execute("TRUNCATE TABLE access_logs");
            jdbcTemplate.execute("TRUNCATE TABLE blocked_ips");
            return RepeatStatus.FINISHED;
        };
    }
}
