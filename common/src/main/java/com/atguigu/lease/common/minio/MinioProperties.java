package com.atguigu.lease.common.minio;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data//加了@Data即可得到get和set方法
@ConfigurationProperties(prefix = "minio")//prefix即前缀，指要绑定的这些参数值的前缀
public class MinioProperties {

    private String endpoint;

    private String accessKey;

    private String secretKey;

    private String bucketName;

    private String publicUrl;
}
