package com.flawflew.knn.bo;

import com.flawflew.knn.pojo.User;
import lombok.Data;

import java.util.Date;

@Data
public class UserBo {
    private Integer id;
    private String name;
    private String account;
    private String pass;
    private String email;
    private String phone;
    private String card;
    private Date birth;
    private String sex;

    public User to(){
        User user = new User();
        user.setId(this.id);
        user.setUsername(this.name);
        user.setPassword(this.pass);
        user.setEmail(this.email);
        user.setPhone(this.phone);
        user.setCard(this.card);
        user.setBirth(this.birth);
        user.setSex(this.sex);
        user.setAccount(this.account);
        return user;
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
