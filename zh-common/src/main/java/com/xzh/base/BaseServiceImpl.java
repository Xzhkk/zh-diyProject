package com.xzh.base;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xzh.utils.Util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.Function;

import static cn.hutool.poi.excel.sax.AttributeName.t;

public class BaseServiceImpl<M extends BaseMapper<T>,T> extends ServiceImpl<M,T> implements BaseService<T>{

    private final Class<T> clazz;

    {
        //获得带有乏型的父亲
        Type type = this.getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            clazz = Util.cast(parameterizedType.getActualTypeArguments()[1]);
        } else {
            clazz = Util.cast(type);
        }
    }


    @Override
    public T findOneByField(SFunction<T, ?> function, Object val) {
        if (val == null) {
            return null;
        }
        return this.getOne(Wrappers.lambdaQuery(clazz()).eq(function,val).last(" limit 1"));
    }

    @Override
    public Class<T> clazz() {
        return this.clazz;
    }
}
