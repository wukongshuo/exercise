package com.xxw.base.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Json工具类，封装fastJson
 */
@Slf4j
@UtilityClass
public class JsonUtil {

    /**
     * 所有的属性都会输出
     *
     * @param object
     * @return
     */
    public String objectToJson(Object object) {
        return JSONObject.toJSONString(object, SerializerFeature.WriteMapNullValue);
    }

    public <T> List<T> jsonToList(String json, Class<T> clazz) {
        return JSONArray.parseArray(json, clazz);
    }

    public <T> T jsonToObject(String json, Class<T> clazz) {
        return JSONObject.parseObject(json, clazz);
    }
}
