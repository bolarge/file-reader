package com.dot.filereader;

import com.dot.filereader.service.FileReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootApplication
public class FileReaderApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(FileReaderApplication.class, args);
	}

	@Autowired
	FileReader fileReader;

	@Override
	public void run(String[] args){

		//fileReader.openFile();
		fileReader.processFile();

		String startTime = "2022-01-01 00:00:11.763";
		String endTime = "2022-01-01 05:39:55.085";
		int limit = 20;

		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
		LocalDateTime startLocalDateTime = LocalDateTime.parse(startTime, dateTimeFormatter);
		LocalDateTime endLocalDateTime = LocalDateTime.parse(endTime, dateTimeFormatter);

		//fileReader.checkRequestRate("192.168.234.82",startLocalDateTime, endLocalDateTime);

		//Use Case 1
		//fileReader.CheckRequestRateCount("192.168.234.82", startLocalDateTime, endLocalDateTime, limit);

		//Use Case 2
		fileReader.checkRequestRateCount(startLocalDateTime, "Hourly", limit);
	}
}
