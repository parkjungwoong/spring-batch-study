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
public class SimpleJobConfig {
    private final JobBuilderFactory jobBuilderFactory; // Job 오브젝트 생성 빌더
    private final StepBuilderFactory stepBuilderFactory; // Step 오브젝트 생성 빌더

    /**
     * Job 생성, job은 1개 이상의 step을 갖을 수 있다.
     */
    @Bean
    public Job simpleJob() {
        return jobBuilderFactory.get("simpleJob")
                .start(step1()) //step 추가
                .build();
    }

    /**
     * step은 job에 속한 관계이다.
     * step은 tasklet, itemReader, itemWriter, itemProcess를 갖을 수 있다.
     */
    @Bean
    public Step step1() {
        return stepBuilderFactory.get("simpleStep")
                .tasklet( (contribution, chunkContext) -> {
                    log.info("nextStep1 start");
                    return RepeatStatus.FINISHED;
                }).build();
    }

}
