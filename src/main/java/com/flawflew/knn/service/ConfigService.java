package com.flawflew.knn.service;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.flawflew.knn.mapper.ConfigMapper;
import com.flawflew.knn.pojo.GlobalConfig;
import com.flawflew.knn.pojo.Result;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class ConfigService {

    @Resource
    private ConfigMapper configMapper;

    public GlobalConfig queryConfig(Integer configType){
        List<GlobalConfig> configs = configMapper.selectByMap(Map.of("config_type", configType, "invalid", 0));
        if(configs.size()==0){
            return null;
        }
        else{
            return configs.get(0);
        }
    }

    public boolean saveConfig(GlobalConfig config){
        Integer configType = config.getConfigType();
        UpdateWrapper<GlobalConfig> wrapper = new UpdateWrapper<>();
        wrapper.eq("invalid",0).eq("config_type",configType);
        wrapper.set("invalid",1);
        configMapper.update(null,wrapper);

        return configMapper.insert(config)==1;
    }
}
