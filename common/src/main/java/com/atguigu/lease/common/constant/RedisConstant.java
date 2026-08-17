package com.atguigu.lease.common.constant;

public class RedisConstant {
    public static final String ADMIN_LOGIN_PREFIX = "admin:login:";
    public static final Integer ADMIN_LOGIN_CAPTCHA_TTL_SEC = 60;
    public static final String APP_LOGIN_PREFIX = "app:login:";
    public static final Integer APP_LOGIN_CODE_RESEND_TIME_SEC = 60;
    public static final Integer APP_LOGIN_CODE_TTL_SEC = 60 * 5;
    public static final String APP_ROOM_PREFIX = "app:room:";
    public static final String AI_CHAT_MEMORY_PREFIX = "ai:chat:memory:";
    public static final String AI_CONVERSATION_CONTEXT_PREFIX = "ai:conversation:context:";
    public static final Integer AI_CONTEXT_TTL_SEC = 60 * 60 * 24 * 7;
}
