package com.flawflew.knn;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.flawflew.knn.mapper")
public class KnnApplication {

    public static void main(String[] args) {
        SpringApplication.run(KnnApplication.class, args);
    }

}
