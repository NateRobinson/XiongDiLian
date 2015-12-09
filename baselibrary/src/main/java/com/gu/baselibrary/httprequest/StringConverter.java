package com.gu.baselibrary.httprequest;

/**
 * Created by Nate on 2015/10/21. 解决gson解析null时错误问题
 */

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class StringConverter implements JsonSerializer<String>,
        JsonDeserializer<String> {
    public JsonElement serialize(String src, Type typeOfSrc,
                                 JsonSerializationContext context) {
        if (src == null) {
            return new JsonPrimitive("");
        } else {
            return new JsonPrimitive(src.toString());
        }
    }

    public String deserialize(JsonElement json, Type typeOfT,
                              JsonDeserializationContext context)
            throws JsonParseException {
        return json.getAsJsonPrimitive().getAsString();
    }
}