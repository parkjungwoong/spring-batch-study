package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.h2.tools.Server;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.SQLException;

@Slf4j
@EnableBatchProcessing //배치기능 활성화, SpringBatch 사용을 위해서 필수로 선언되어야함
@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
		try {
			//h2 서버 생성
			log.info("start h2 tcp server");
			//h2 db의 외부 접속을 위해 tcp 서버 생성
			Server.createTcpServer("-tcpPort","9123","-tcpAllowOthers").start();
		} catch (SQLException e) {
			log.error("H2 createServer ERROR",e);
		}
	}

}
