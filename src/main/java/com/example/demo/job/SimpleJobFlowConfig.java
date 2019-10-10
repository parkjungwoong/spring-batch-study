package com.example.demo.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
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
public class SimpleJobFlowConfig {
    private final JobBuilderFactory jobBuilderFactory; // Job 오브젝트 생성 빌더
    private final StepBuilderFactory stepBuilderFactory; // Step 오브젝트 생성 빌더

    @Bean
    public Job flowJob() {
        /*
        step1 실패 시나리오: step1 -> step3
        step1 성공 시나리오: step1 -> step2 -> step3
         */
        return jobBuilderFactory.get("flowJob")
                .start(flowStep1())
                    .on(ExitStatus.FAILED.getExitCode()) // FAILED 일 경우
                    .to(flowStep3()) // step3으로 이동한다.
                    .on("*") // step3의 결과 관계 없이
                    .end() // step3으로 이동하면 Flow가 종료한다.
                .from(flowStep1()) // step1로부터
                    .on("*") // FAILED 외에 모든 경우
                    .to(flowStep2()) // step2로 이동한다.
                    .next(flowStep3()) // step2가 정상 종료되면 step3으로 이동한다.
                    .on("*") // step3의 결과 관계 없이
                    .end() // step3으로 이동하면 Flow가 종료한다.
                .end() // Job 종료
                .build();
        /**
         * from() => 해당 step이 on 의 패턴데 맞으면 to 실행
         */
    }

    @Bean
    public Step flowStep1() {
        return stepBuilderFactory.get("flowStep1")
                .tasklet((contribution, chunkContext) -> {
                    log.info(">>>>> This is stepNextConditionalJob Step1");

                    /**
                     ExitStatus를 FAILED로 지정한다.
                     해당 status를 보고 flow가 진행된다.
                     **/
                    //contribution.setExitStatus(ExitStatus.FAILED);
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step flowStep2() {
        return stepBuilderFactory.get("flowStep2")
                .tasklet((contribution, chunkContext) -> {
                    log.info(">>>>> This is stepNextConditionalJob Step2");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }

    @Bean
    public Step flowStep3() {
        return stepBuilderFactory.get("flowStep3")
                .tasklet((contribution, chunkContext) -> {
                    log.info(">>>>> This is stepNextConditionalJob Step3");
                    return RepeatStatus.FINISHED;
                })
                .build();
    }
}
