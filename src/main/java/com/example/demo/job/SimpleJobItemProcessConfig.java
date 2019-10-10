package com.example.demo.job;

import com.example.demo.model.Simple2Model;
import com.example.demo.model.SimpleModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.sql.DataSource;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SimpleJobItemProcessConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource; // DataSource DI

    private static final int chunkSize = 10;

    @Bean
    public Job jdbcBatchItemProcesssJob() {
        return jobBuilderFactory.get("jdbcBatchItemProcessJob")
                .start(jdbcBatchItemProcessStep())
                .build();
    }

    @Bean
    @JobScope
    public Step jdbcBatchItemProcessStep() {
        return stepBuilderFactory.get("jdbcBatchItemProcessStep")
                .<SimpleModel, Simple2Model>chunk(chunkSize)
                .reader(jdbcBatchItemProcessReader())
                .processor(itemProcessor())
                .writer(jdbcBatchItemProcessWriter())
                .build();
    }

    @Bean
    public JdbcCursorItemReader<SimpleModel> jdbcBatchItemProcessReader() {
        return new JdbcCursorItemReaderBuilder<SimpleModel>()
                .fetchSize(chunkSize)
                .dataSource(dataSource)
                .rowMapper(new BeanPropertyRowMapper<>(SimpleModel.class))
                .sql("SELECT id, name, age FROM simple where age")
                .name("jdbcBatchItemWriter")
                .build();
    }

    @Bean
    public ItemProcessor<SimpleModel, Simple2Model> itemProcessor() {
        return simpleModel -> {
            log.info("itemProcessor {}",simpleModel.getId());
            Simple2Model simple2Model = new Simple2Model();
            simple2Model.setAge(simpleModel.getAge());
            simple2Model.setAddr(simpleModel.getAge()+" addr");
            return simple2Model;
        };
    }

    /**
     * reader에서 넘어온 데이터를 하나씩 출력하는 writer
     */
    @Bean // beanMapped()을 사용할때는 필수
    public JdbcBatchItemWriter<Simple2Model> jdbcBatchItemProcessWriter() {
        return new JdbcBatchItemWriterBuilder<Simple2Model>()
                .dataSource(dataSource)
                .sql("INSERT INTO simple2 (name, age, addr) VALUES (:name, :age, :addr)")
                .beanMapped() //Pojo 기반(DTO)으로 insert sql의 values 바인딩
                //.columnMapped() -> Key, value(MAP) 으로 sql의 values 바인딩
                .build();
    }
}
