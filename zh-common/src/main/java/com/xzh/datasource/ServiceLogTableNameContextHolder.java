package com.xzh.datasource;

public class ServiceLogTableNameContextHolder {
    private static final ThreadLocal<String> TABLE_NAME_HOLDER = new ThreadLocal<>();
    public static void setTableName(String name) { TABLE_NAME_HOLDER.set(name); }
    public static String getTableName(String baseName) { return TABLE_NAME_HOLDER.get(); }
    public static void clear() { TABLE_NAME_HOLDER.remove(); }
}
