package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.common.minio.MinioProperties;
import com.atguigu.lease.web.admin.service.FileService;
import io.minio.*;
import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private MinioProperties properties;

    @Override
    public String upload(MultipartFile file) throws ServerException, InsufficientDataException,
            ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException,
            InvalidResponseException, XmlParserException, InternalException {
        //判断桶是否存在
        boolean bucketExists = minioClient.bucketExists(
                BucketExistsArgs
                        .builder()
                        .bucket(properties.getBucketName())
                        .build());
        if (!bucketExists) {
            //创建桶
            minioClient.makeBucket(
                    MakeBucketArgs
                            .builder()
                            .bucket(properties.getBucketName())
                            .build());

            //设置权限
            minioClient.setBucketPolicy(
                    SetBucketPolicyArgs
                            .builder()
                            .bucket(properties.getBucketName())
                            .config(createBucketPolicyConfig(properties.getBucketName()))
                            .build());
        }
        //上传图片
        String filename = new SimpleDateFormat("yyyyMMdd").format(
                new Date()) + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();
        //getOriginalFilename返回用户上传文件的原始用户名，getName返回前端的表单字段名
        minioClient.putObject(PutObjectArgs
                .builder()
                .bucket(properties.getBucketName())
                .stream(file.getInputStream(), file.getSize(), -1)
                .object(filename)
                .contentType(file.getContentType())//设置了contentType，浏览器打开url就不是自动下载，而是预览图片
                .build());

        String publicUrl = properties.getPublicUrl();
        if (publicUrl == null || publicUrl.isEmpty()) {
            publicUrl = properties.getEndpoint();
        }
        String url = publicUrl + "/" + properties.getBucketName() + "/" + filename;

        return url;
    }

    private String createBucketPolicyConfig(String bucketName) {
        //Action、Effect、Principal、Resource四个字段连起来就是允许所有人读取bucket下的所有资源
        return """
                {
                  "Statement" : [ {
                    "Action" : "s3:GetObject",
                    "Effect" : "Allow",
                    "Principal" : "*",
                    "Resource" : "arn:aws:s3:::%s/*"
                  } ],
                  "Version" : "2012-10-17"
                }
                """.formatted(bucketName);
    }
}
