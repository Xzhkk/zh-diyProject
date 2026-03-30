package com.xzh.bean;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName(value ="cm_dynamic_excel_config")
public class CmDynamicExcelConfig {
    private String id;
    private String exportCode;
    private String fileName;
    private String sheetName;
    private String columnConfig;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
