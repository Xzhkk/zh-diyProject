package com.xzh.bean;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName(value ="cm_dict")
public class CmDict {
    private String id;
    private String classCode;
    private String classDict;
    private String value;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;}
