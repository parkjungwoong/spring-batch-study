package com.example.demo.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SimpleNextJobConfig {
    private final JobBuilderFactory jobBuilderFactory; // Job 오브젝트 생성 빌더
    private final StepBuilderFactory stepBuilderFactory; // Step 오브젝트 생성 빌더

    @Bean
    public Job nextJob() {
        return jobBuilderFactory.get("nextJob")
                .start(nextStep1())
                .next(nextStep2())//순차적으로 step을 연결할 때 사용
                .build();
    }

    @Bean
    public Step nextStep1() {
        return stepBuilderFactory.get("nextJob - nextStep1")
                .tasklet( (contribution, chunkContext) -> {
                    log.info("nextJob - nextStep1");
                    return RepeatStatus.FINISHED;
                }).build();
    }

    @Bean
    public Step nextStep2() {
        return stepBuilderFactory.get("nextJob - nextStep2")
                .tasklet( (contribution, chunkContext) -> {
                    log.info("nextJob - nextStep2");
                    return RepeatStatus.FINISHED;
                }).build();
    }

}
