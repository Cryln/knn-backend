package com.flawflew.knn.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.flawflew.knn.bo.UserBo;
import lombok.Data;

import java.util.Date;

@Data
public class User {
    @TableId(type= IdType.AUTO)
    private Integer id;
    private String account;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String card;
    private Date birth;
    private String sex;

    public UserBo to(){
        UserBo userBo = new UserBo();
        userBo.setId(this.id);
        userBo.setName(this.username);
        userBo.setPass(this.password);
        userBo.setEmail(this.email);
        userBo.setPhone(this.phone);
        userBo.setCard(this.card);
        userBo.setSex(this.sex);
        userBo.setAccount(this.account);
        userBo.setBirth(this.birth);
        return userBo;
    }
}
/**
 * {
 *   name: 'jmj',
 *   account: 'tt2345',
 *   pass: '1234',
 *   checkPass: '1234',
 *   email: 'superj0529@gmail.com',
 *   phone: '13100000000',
 *   card: '513021199905296092',
 *   birth: '2016-06-29T16:00:00.000Z',
 *   sex: 'man'
 * }
 */
