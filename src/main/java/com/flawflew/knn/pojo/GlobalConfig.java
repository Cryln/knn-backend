package com.flawflew.knn.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GlobalConfig {

    public static Integer KNN_CONFIG = 100;

    @TableId(type = IdType.AUTO)
    Integer configId;
    Integer configType;
    String content;
    Integer invalid;
}
