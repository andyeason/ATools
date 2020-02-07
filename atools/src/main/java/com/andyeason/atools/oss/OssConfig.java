package com.andyeason.atools.oss;

/**
 * 阿里云OSS配置文件
 *
 * @author Andy.
 */

public interface OssConfig {

    String END_POINT = "oss-cn-hangzhou.aliyuncs.com";
    String ACCESS_ID = " **";
    String ACCESS_KEY = "**";
    String BUCKET = "**";
    String PUBLIC_OSS_URL = "http://" + BUCKET + "." + END_POINT + "/";
    boolean ReturnAllPath = true;//是否返回完整路径
    //其他可选配置
    String OSS_CONFIG = "?x-oss-process=image/resize,w_750";
    String CIRCLE = "?x-oss-process=image/circle,r_4096/format,png";
}
