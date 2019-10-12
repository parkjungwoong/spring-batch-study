package com.example.demo.job;

import com.example.demo.model.SimpleModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import javax.sql.DataSource;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class SimpleJobDbCursorConfig {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final DataSource dataSource; // DataSource DI

    private static final int chunkSize = 10;

    @Bean
    public Job jdbcCursorItemReaderJob() {
        return jobBuilderFactory.get("jdbcCursorItemReaderJob")
                .start(jdbcCursorItemReaderStep())
                .build();
    }

    @Bean
    public Step jdbcCursorItemReaderStep() {
        return stepBuilderFactory.get("jdbcCursorItemReaderStep")
                .<SimpleModel, SimpleModel>chunk(chunkSize) //<Reader에서 반환할 타입, Writer에 파라미터로 넘오올 타입>, 트랜젝션 처리 양
                .reader(jdbcCursorItemReader())
                .writer(jdbcCursorItemWriter())
                .build();
    }

    /**
     * Cursor 방식의 reader 사용시 하나의 커넥션을 갖고 batch가 종료 될때까지 수행되기 때문에 socketTimeout 값을 배치 수행 완료 시간보다 크게 설정해야된다.
     * 배치 수행시간이 오래 걸린 경우 page 방식의 reader를 사용해야된다.
     */
    @Bean
    public JdbcCursorItemReader<SimpleModel> jdbcCursorItemReader() {
        return new JdbcCursorItemReaderBuilder<SimpleModel>()
                .fetchSize(chunkSize)//한번에 조회할 양
                .dataSource(dataSource)//데이터 소스
                .rowMapper(new BeanPropertyRowMapper<>(SimpleModel.class))//조회 결과 매핑
                .sql("SELECT id, name, age FROM simple")
                .name("jdbcCursorItemReader")//Bean이름이 아닌 ExecutionContext에서 지정되어질 이름
                .build();
    }

    private ItemWriter<SimpleModel> jdbcCursorItemWriter() {
        return list -> {
            for (SimpleModel SimpleModel: list) {
                log.info("Current SimpleModel={}", SimpleModel);
            }
        };
    }
}
