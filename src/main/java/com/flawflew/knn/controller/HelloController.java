package com.flawflew.knn.controller;

import com.flawflew.knn.mapper.UserMapper;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class HelloController {

    @Resource
    private UserMapper userMapper;

    @PostMapping("/hello")
    String hello(@RequestParam("name") @Nullable String name){
        return "Hello, "+name;
    }

}
