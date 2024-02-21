package com.mango.maker.meta;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;

import java.io.BufferedReader;
import java.net.URL;

/**
 * JSON转成JAVA对象
 *
 */
public class MetaMessage {
    private static volatile Meta meta ;

    /**
     * 利用设计模式饿汉模式
     * @return
     */
    public static Meta getMeta() {
        if (meta == null) {
            synchronized (MetaMessage.class) {
                if (meta == null) {
                    //meta = new Meta();
                    meta=initMeta();
                }
            }
        }
        return meta;
    }

    /**
     * Json转成java对象
     * @return
     */
    private  static Meta initMeta(){
        String utf8Reader = ResourceUtil.readUtf8Str("meta.json");
        Meta meta = JSONUtil.toBean(utf8Reader, Meta.class);
        return meta;
    }

}
