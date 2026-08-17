package com.atguigu.lease.web.app.service;

import com.aliyun.dypnsapi20170525.models.SendSmsVerifyCodeResponse;

public interface SmsService {

    String sendCode(String phone);
}
