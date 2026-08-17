package com.atguigu.lease.web.app.service.impl;

import com.aliyun.dypnsapi20170525.Client;
import com.aliyun.dypnsapi20170525.models.SendSmsVerifyCodeRequest;
import com.aliyun.dypnsapi20170525.models.SendSmsVerifyCodeResponse;
import com.atguigu.lease.web.app.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SmsServiceImpl implements SmsService {

    @Autowired
    private Client client;

    @Override
    public String sendCode(String phone) {
        SendSmsVerifyCodeRequest request = new SendSmsVerifyCodeRequest();
        request.setSignName("恒创联众");
        request.setTemplateCode("100001");
        request.setPhoneNumber(phone);
        request.setTemplateParam("{\"code\":\"##code##\",\"min\":\"5\"}");
        request.setReturnVerifyCode(true);

        try {
            SendSmsVerifyCodeResponse response = client.sendSmsVerifyCode(request);
            String responseCode = response.getBody().getCode();
            if (!"OK".equals(responseCode)) {
                throw new RuntimeException("短信发送失败: " + responseCode + " - " + response.getBody().getMessage());
            }else{
                return response.getBody().getModel().getVerifyCode();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
/*client.sendSmsVerifyCode(request) 会返回一个 SendSmsVerifyCodeResponse，
它就是这次 HTTP 调用完整响应的封装，SDK 把阿里云返回的 JSON
反序列化成了这个对象。结构分三层：

SendSmsVerifyCodeResponse
├─ headers    (Map<String,String>)    HTTP 响应头
├─ statusCode(Integer)               HTTP 状态码，正常是 200
└─ body       (SendSmsVerifyCodeResponseBody)  ← 业务数据都在这里
   ├─ code            (String)   业务返回码，"OK" 表示成功
   ├─ message         (String)   提示信息
   ├─ success         (Boolean)  是否成功
   ├─ requestId       (String)   请求 ID，排查问题用
   ├─ model           (Model)    业务数据：
   │  ├─ bizId        发送业务 ID（后续校验验证码要用）
   │  ├─ verifyCode   验证码（仅当请求设了 returnVerifyCode=true 才返回）
   │  └─ outId / requestId
   └─ accessDeniedDetail (String)

两点关键提醒：

1. statusCode 是 200 不等于发送成功——业务失败时 HTTP 也返回 200，只是 body.code
   不是 "OK"。所以代码里判断的是 response.getBody().getCode()（SmsServiceImpl.
   java:23-25），而不是看 HTTP 状态码。

2. model.verifyCode 默认是空的——因为你没给请求设置 setReturnVerifyCode(true)，
   验证码由阿里云侧存着。想自己拿验证码（比如存 Redis 做登录校验），
   就要开启该参数并从 response.getBody().getModel().getVerifyCode() 取。*/
