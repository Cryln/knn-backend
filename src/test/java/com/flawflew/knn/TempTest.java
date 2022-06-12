package com.flawflew.knn;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.flawflew.knn.controller.HelloController;
import com.flawflew.knn.mapper.RelationMapper;
import com.flawflew.knn.mapper.UserMapper;
import com.flawflew.knn.pojo.Relation;
import com.flawflew.knn.pojo.User;
import com.flawflew.knn.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.*;
import java.util.List;


@SpringBootTest
public class TempTest {
    @Resource
    private UserMapper userMapper;

    @Resource
    private RelationMapper relationMapper;

    @Autowired
    private UserService userService;

    @Test
    public void testSelect() {
        System.out.println(("----- selectAll method test ------"));
        List<User> userList = userMapper.selectList(null);

//        Assertions.assertEquals(5, userList.size());
        userList.forEach(System.out::println);
    }

    @Test
    public void testInsert(){
        relationMapper.insert(new Relation(2,3));
        relationMapper.insert(new Relation(3,2));
        relationMapper.insert(new Relation(1,3));
    }

    @Test
    public void testSearch(){
        userService.invokePython(2,new UserService.KNNConfig(10000,5));
    }

    @Test void batchNewUser(){
        User user = userMapper.selectById(1);
        System.out.println(user);
        user.setPassword("123");
        for (int i = 1; i < 100; i++) {
            user.setId(null);
            user.setUsername("testUser"+i);
            user.setAccount("testUser"+i);
            userMapper.insert(user);
        }
        System.out.println("end");
    }

    @Test void batchRelation(){
        String jsonStr = "";
        try {
            File jsonFile = new File("src/main/resources/static/relation.json");
            FileReader fileReader = new FileReader(jsonFile);

            Reader reader = new InputStreamReader(new FileInputStream(jsonFile), "utf-8");
            int ch = 0;
            StringBuffer sb = new StringBuffer();
            while ((ch = reader.read()) != -1) {
                sb.append((char) ch);
            }

            fileReader.close();
            reader.close();
            jsonStr = sb.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }
        Double[][] parse = JSON.parseObject(jsonStr, Double[][].class);
        for (int i = 9; i < 108; i++) {
            for (int j = i+1; j < 108; j++) {
                if(parse[i-9][j-9].equals((double) 0)){
                    continue;
                }
                userService.makeRelationById(i,j,(int)Math.ceil(parse[i-9][j-9]*100));
            }
        }
    }
    @Test
    public void testRemove(){
        QueryWrapper<Relation> wrapper = new QueryWrapper<>();
        wrapper.ge("relation_id",23);
        relationMapper.delete(wrapper);
    }
}