package com.xzh.bean;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("cm_service_log")
public class CmServiceLog {
    @TableId
    private String id;
    private String url;
    private LocalDateTime createTime;
    private String result;

}
