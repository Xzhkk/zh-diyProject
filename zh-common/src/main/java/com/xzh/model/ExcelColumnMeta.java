package com.xzh.model;

import lombok.Data;

@Data
public class ExcelColumnMeta implements Comparable<ExcelColumnMeta> {
    /** 表头名称 */
    private String title;
    /** 对应数据Map或DTO中的属性名 */
    private String field;
    /** 列宽 */
    private Integer width;
    /** 排序号 (升序) */
    private Integer sort;

    /** 新增：数据格式化 (如: yyyy-MM-dd HH:mm:ss 或 0.00) */
    private String dataFormat;

    /** 新增：字典类型 (如: sys_user_sex, order_status) */
    private String classCode;

    @Override
    public int compareTo(ExcelColumnMeta o) {
        return this.sort.compareTo(o.getSort());
    }
}
