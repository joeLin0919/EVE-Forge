package org.eveforge;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableAsync
@EnableScheduling
@EnableCaching
@MapperScan("org.eveforge.mapper")
@SpringBootApplication
public class EveForgeApplication {

    public static void main(String[] args) {
        SpringApplication.run(EveForgeApplication.class, args);
    }

}