package com.xzh.result;

public class BaseResponse {
    private Integer code;
    private Object data;
    private String msg;

    public BaseResponse (Integer code,Object data,String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    public static BaseResponse success(Object data) {
        return new BaseResponse(200,data,"操作成功");
    }

    public static BaseResponse success() {
        return new BaseResponse(200,null,"操作成功");
    }

    public static BaseResponse error(String msg) {
        return new BaseResponse(500,null,msg);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
