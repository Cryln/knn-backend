package com.flawflew.knn.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.flawflew.knn.bo.UserBo;
import com.flawflew.knn.mapper.RelationMapper;
import com.flawflew.knn.mapper.UserMapper;
import com.flawflew.knn.pojo.GlobalConfig;
import com.flawflew.knn.pojo.Relation;
import com.flawflew.knn.pojo.Result;
import com.flawflew.knn.pojo.User;
import com.flawflew.knn.service.ConfigService;
import com.flawflew.knn.service.UserService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@RequestMapping("/api")
@Slf4j
public class UserController {

    @Resource
    private UserMapper userMapper;
    @Resource
    private RelationMapper relationMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private ConfigService configService;

    @PostMapping("/test")
    String test1(@RequestBody MyData data){
        System.out.println(data);
        return "hello";
    }

    @Data
    static class MyData{
        String name;
        String pass;
    }

    @PutMapping("/user")
    Result addUser(@RequestBody UserBo user){
        User userDo = user.to();
        userMapper.insert(userDo);
        return Result.success("注册成功",userDo.to());
    }

    @PostMapping("/user/login")
    Result userLogin(@RequestBody UserBo user){
        log.info(user.toString());
        List<User> users = userMapper.selectByMap(Map.of("username", user.getName()));

        if (users.size()<1) {
            log.info("用户不存在");
            return Result.failed(Result.NONEXISTENT,"用户不存在");
        }
        User user1 = users.get(0);
        if(user1.getPassword().equals(user.getPass())){
            return Result.success("登录成功",user1.to());
        }
        else {
            return Result.failed(Result.NOT_MATCH,"密码错误");
        }
    }

    @GetMapping("/user")
    Result queryUser(@RequestParam("id") Integer id){
        User user = userMapper.selectById(id);

        return Result.success("查询成功",user.to());
    }

    @PatchMapping("/user")
    Result updateInfo(@RequestBody Map<String,String>map){
        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        wrapper.eq("id",map.get("id"));
        map.forEach((k,v)->{
            if(k.equals("birth")) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date date = simpleDateFormat.parse(v);
                    wrapper.set(k,date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            else{
                wrapper.set(k,v);
            }
        });
        userMapper.update(null,wrapper);
        return Result.SUCCESS;
    }

    @PostMapping("/friend/add")
    Result addFriend(@RequestBody Map<String,String> map){
        Integer userId = Integer.parseInt(map.get("userId"));
        String friendAccount = map.get("account");

        List<User> friends = userMapper.selectByMap(Map.of("account", friendAccount));
        if(friends.size()==0){
            return Result.failed(Result.NONEXISTENT,"未查找到相关用户");
        }

        User friend = friends.get(0);
        userService.makeRelationById(userId,friend.getId());
        return Result.SUCCESS;
    }
    @GetMapping("/v2/friend/add")
    Result addFriendById(@RequestParam("userId")Integer userId,@RequestParam("friendId")Integer friendId){
        userService.makeRelationById(userId,friendId);
        return Result.SUCCESS;
    }

    @GetMapping("/friends")
    Result queryFriends(@RequestParam("id")Integer id){
        List<Relation> relations = userService.getAllRelation(id);
        List<Integer> ids = relations.stream().mapToInt(Relation::getFriendId).boxed().collect(Collectors.toList());
        List<User> friends = userMapper.selectBatchIds(ids);
        return Result.success("查询成功",friends.stream().map(User::to).collect(Collectors.toList()));
    }

    @PostMapping("/friends/interact")
    Result interact(@RequestBody Relation relation){

        UpdateWrapper<Relation> wrapper = new UpdateWrapper<>();
        wrapper.eq("user_id",relation.getUserId()).eq("friend_id",relation.getFriendId());
        wrapper.setSql("intimacy = intimacy + "+relation.getIntimacy());
        relationMapper.update(null,wrapper);
        return Result.SUCCESS;

    }

    @GetMapping("/friends/recommend")
    Result recommend(@RequestParam("userId") Integer id){
        GlobalConfig config = configService.queryConfig(GlobalConfig.KNN_CONFIG);
        Map<String,Object> map = JSON.parseObject(config.getContent());

        Integer sampleSize = (Integer) map.get("sampleSize");
        Integer k = (Integer) map.get("k");

        Map<String, Object> res = userService.invokePython(id, new UserService.KNNConfig(sampleSize, k));
        return Result.success("推荐如下",res);
    }

    @DeleteMapping("/friend")
    Result deleteFriend(@RequestParam("userId")Integer userId,@RequestParam("friendId")Integer friendId){
        userService.brokeRelationById(userId,friendId);
        return Result.SUCCESS;
    }

    public static void main(String[] args) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = simpleDateFormat.parse("2016-06-01T00:00:00.000Z");
            System.out.println(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
