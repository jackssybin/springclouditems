package com.jackssy.springclouds.servicea;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.web.bind.annotation.*;

import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
@EnableEurekaClient
@SpringBootApplication
@RestController
public class ServiceaApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceaApplication.class, args);
    }


    @Value("${server.port}")
    String port;

    @GetMapping("/test")
    public String homeget(@RequestParam(value = "name", defaultValue = "jackssy") String name) {
        return "hi get " + name + " ,i am from port:" + port;
    }

    @PostMapping("/test")
    public String homepost(@RequestParam(value = "name", defaultValue = "jackssy") String name) {
        return "hi post " + name + " ,i am from port:" + port;
    }

    @RequestMapping("/test/aa")
    public String aa(@RequestParam(value = "name", defaultValue = "jackssy") String name) {
        return "aa" + name + " ,i am from port:" + port;
    }

    @RequestMapping("/test/bb")
    public String bb(@RequestParam(value = "name", defaultValue = "jackssy") String name) {
        return "bb" + name + " ,i am from port:" + port;
    }
}
