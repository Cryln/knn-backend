package com.flawflew.knn.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.flawflew.knn.mapper.RelationMapper;
import com.flawflew.knn.mapper.UserMapper;
import com.flawflew.knn.pojo.Relation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private RelationMapper relationMapper;


    public List<Relation> getAllRelation(Integer id){
        return relationMapper.selectByMap(Map.of("user_id", id));
    }

    public List<Relation> getNLayerAllRelation(Integer id,Integer n){
        Set<Relation> ans = new HashSet<>();
        Set<Integer> nextLayer = Set.of(id);
        Set<Integer> visited = new HashSet<>();
        for (int i = 0; i < n; i++) {
            if(nextLayer.isEmpty()){
                break;
            }else{
                Set<Relation> next = new HashSet<>();
                for (Integer curId : nextLayer) {
                    List<Relation> temp = getAllRelation(curId);
                    visited.add(curId);
                    next.addAll(temp);
                }
                nextLayer = next.stream().map(Relation::getFriendId).collect(Collectors.toSet());
                nextLayer.removeAll(visited);
                ans.addAll(next);
            }
        }
        return new ArrayList<>(ans);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class KNNConfig{
        Integer sampleSize;
        Integer k;
    }

    public Map<String, Object> invokePython(Integer src, KNNConfig config){
        List<Relation> relations = getNLayerAllRelation(src, 3);
        List<Integer> friends = getAllRelation(src).stream().map(Relation::getFriendId).collect(Collectors.toList());

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("arg2",config);
        jsonObject.put("arg3",relations);
        jsonObject.put("arg4",friends);

//        String arg2 = JSON.toJSONString(config).replaceAll("\"","\\\\\"");
//        String arg3 = JSON.toJSONString(relations).replaceAll("\"","\\\\\"");
//        String friendsList = JSON.toJSONString(friends);
        String json = jsonObject.toJSONString();
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("src/main/resources/static/args.json"));
            out.write(json);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String invokeResult = null;
        Process proc;
        try {
            String[] cmd = new String[]{"python","src/main/python/knn.py",src.toString()};
            proc = Runtime.getRuntime().exec(cmd);
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                System.out.println("py*************");
                System.out.println("py:"+line);
                System.out.println("py*************");
                invokeResult = line;
            }
            in.close();
            proc.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Map<String,Object> res = new HashMap<>();
//        System.out.println("py:"+invokeResult);
        res.put("recommend",JSON.parseArray(invokeResult));
        res.put("graph",relations);
        return res;
    }

    public void makeRelationById(Integer userId,Integer friendId){
        relationMapper.insert(new Relation(userId,friendId));
        relationMapper.insert(new Relation(friendId,userId));
    }
    public void makeRelationById(Integer userId,Integer friendId,Integer val){
        if(val==0){
            val=1;
        }
        relationMapper.insert(new Relation(null,userId,friendId,val));
        relationMapper.insert(new Relation(null,friendId,userId,val));
    }

    public void brokeRelationById(Integer userId,Integer friendId){
        relationMapper.deleteByMap(Map.of("user_id",userId,"friend_id",friendId));
        relationMapper.deleteByMap(Map.of("user_id",friendId,"friend_id",userId));
    }

    public static void main(String[] args)  {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("src/main/resources/static/xx.json"));
            out.write("xx");
            out.close();
            System.out.println("文件创建成功！");
        } catch (IOException e) {
        }
    }

}

