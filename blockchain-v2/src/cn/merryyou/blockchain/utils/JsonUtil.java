package cn.merryyou.blockchain.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created on 2018/10/20.
 *
 * @author DragonW 
 * @since 1.0
 */
public class JsonUtil {
    public static String toJson(Object object){
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        Gson gson = gsonBuilder.create();
        return gson.toJson(object);
    }
}
