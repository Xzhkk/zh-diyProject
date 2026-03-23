package com.xzh.config;

import com.xzh.constants.DataSourceConstants;

import java.util.ArrayDeque;
import java.util.Deque;

public class DynamicDataSourceContextHolder {
    private static final ThreadLocal<Deque<String>> CONTEXT_HOLDER = ThreadLocal.withInitial(ArrayDeque::new);

    public static void push(String dataSourceKey) {
        CONTEXT_HOLDER.get().push(dataSourceKey);

    }
    public static String peek() {
        return CONTEXT_HOLDER.get().isEmpty() ? DataSourceConstants.MASTER : CONTEXT_HOLDER.get().peek();
    }

    public static void poll() {
        Deque<String> deque = CONTEXT_HOLDER.get();
        deque.poll();
        if (deque.isEmpty()) CONTEXT_HOLDER.remove();
    }
}
