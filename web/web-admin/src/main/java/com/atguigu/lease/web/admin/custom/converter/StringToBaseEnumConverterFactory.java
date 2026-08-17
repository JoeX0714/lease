package com.atguigu.lease.web.admin.custom.converter;

import com.atguigu.lease.model.enums.BaseEnum;
import com.atguigu.lease.model.enums.ItemType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class StringToBaseEnumConverterFactory implements ConverterFactory<String, BaseEnum> {
    @Override
    public <T extends BaseEnum> Converter<String, T> getConverter(Class<T> targetType) {
        /*该方法是一个泛型方法，其基本格式是：访问修饰符 <类型参数> 返回值类型 方法名(形参)，对于该方法
        * public                       访问修饰符
        <T extends BaseEnum>         声明一个类型变量 T，并限制 T 必须属于 BaseEnum ，java必须先声明T，然后才能使用T
        Converter<String, T>         返回值类型
        getConverter                 方法名
        Class<T> targetType          方法形参*/
        return new Converter<String,T>(){
            @Nullable
            @Override
            public T convert(String code) {
                T[] enumConstants = targetType.getEnumConstants();//getEnumConstants方法获取全部枚举对象
                for(T enumConstant:enumConstants){
                    if(enumConstant.getCode().equals(Integer.valueOf(code))){
                        return enumConstant;
                    }
                }
                throw new IllegalArgumentException("code:"+code+"非法");
            }
        };
    }
}
