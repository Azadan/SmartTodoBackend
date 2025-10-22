package com.smarttodo.smarttodo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SmartTodoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartTodoApplication.class, args);
    }

}
