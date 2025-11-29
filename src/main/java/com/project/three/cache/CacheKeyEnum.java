package com.project.three.cache;

public enum CacheKeyEnum {
    USER_BY_ID("user:id:%s", "根据用户ID缓存用户信息"),
    USER_BY_USERNAME("user:username:%s", "根据用户名缓存用户信息");

    private final String keyPattern;
    private final String desc;

    CacheKeyEnum(String keyPattern, String desc) {
        this.keyPattern = keyPattern;
        this.desc = desc;
    }

    // 生成带参数的缓存Key
    public String getKey(Object... args) {
        return String.format(keyPattern, args);
    }
}