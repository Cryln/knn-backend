package com.flawflew.knn.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result {
    public static int COMMON_ERROR = -1;
    public static int NONEXISTENT = -2;
    public static int NOT_MATCH = -3;

    public static int COMMON_SUCCESS = 200;

    public static Result SUCCESS = new Result(COMMON_SUCCESS,"成功",null);
    public static Result FAILED = new Result(COMMON_ERROR,"失败",null);


    private int code;
    private String msg;
    private Object data;

    public static Result success(String msg,Object data){
        return new Result(200,msg,data);
    }
    public static Result failed(int code, String msg){
        return new Result(code,msg,null);
    }
}
