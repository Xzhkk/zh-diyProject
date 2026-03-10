package com.xzh.base;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.function.Function;

public interface BaseService<T> extends IService<T> {
    T findOneByField(SFunction<T,?> function, Object fieldValue);
    Class<T> clazz();

}
