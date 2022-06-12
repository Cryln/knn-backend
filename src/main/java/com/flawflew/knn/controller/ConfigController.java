package com.flawflew.knn.controller;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.flawflew.knn.mapper.ConfigMapper;
import com.flawflew.knn.pojo.GlobalConfig;
import com.flawflew.knn.pojo.Result;
import com.flawflew.knn.service.ConfigService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin
public class ConfigController {

    @Resource
    private ConfigService configService;

    @GetMapping("/config")
    Result getConfig(@RequestParam("configType")Integer configType){
        GlobalConfig config = configService.queryConfig(configType);
        if(config==null){
            return Result.failed(Result.NONEXISTENT,"不存在");
        }else {
            return Result.success("查询成功",config);
        }
    }

    @PutMapping("/config")
    Result saveConfig(@RequestBody Map<String,Object> map){
        Integer configType = (Integer)map.get("configType");
        Object content = map.get("content");
        GlobalConfig config = new GlobalConfig(null, configType, JSON.toJSONString(content), 0);


        return configService.saveConfig(config)?Result.SUCCESS:Result.FAILED;
    }
}
