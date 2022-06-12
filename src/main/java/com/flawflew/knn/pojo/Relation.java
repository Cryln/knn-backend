package com.flawflew.knn.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Relation {

    @TableId(type = IdType.AUTO)
    private Integer relationId;
    private Integer userId;
    private Integer friendId;
    private Integer intimacy;

    public Relation(Integer user, Integer friend){
        userId = user;
        friendId = friend;
    }
}
