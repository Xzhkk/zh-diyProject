package com.xzh.bean;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 
 * @TableName cm_user
 */
@TableName(value ="cm_user")
public class CmUser {
    /**
     * 
     */
    @TableId
    private String id;

    /**
     * 
     */
    private String userName;

    /**
     * 
     */
    private String passWord;

    /**
     * 
     */
    private String nickName;

    /**
     * 
     */
    private String nickUrl;

    /**
     * 
     */
    public String getId() {
        return id;
    }

    /**
     * 
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 
     */
    public String getUserName() {
        return userName;
    }

    /**
     * 
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * 
     */
    public String getPassWord() {
        return passWord;
    }

    /**
     * 
     */
    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    /**
     * 
     */
    public String getNickName() {
        return nickName;
    }

    /**
     * 
     */
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    /**
     * 
     */
    public String getNickUrl() {
        return nickUrl;
    }

    /**
     * 
     */
    public void setNickUrl(String nickUrl) {
        this.nickUrl = nickUrl;
    }



    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", userName=").append(userName);
        sb.append(", passWord=").append(passWord);
        sb.append(", nickName=").append(nickName);
        sb.append(", nickUrl=").append(nickUrl);
        sb.append("]");
        return sb.toString();
    }
}