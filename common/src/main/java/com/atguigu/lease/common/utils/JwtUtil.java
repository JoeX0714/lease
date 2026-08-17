package com.atguigu.lease.common.utils;

import com.atguigu.lease.common.exception.LeaseException;
import com.atguigu.lease.common.result.ResultCodeEnum;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtUtil {

    private static SecretKey secretKey = Keys.hmacShaKeyFor("M0PKKI6pYGVWWfDZw90a0lTpGYX1d4AQ".getBytes());

    public static String createToken(Long userId,String username){
        //Payload（负载）
        String jwt = Jwts
                .builder()
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))//3600000即一个小时
                .setSubject("LOGIN_USER")
                .claim("userId", userId)//自定义字段
                .claim("username", username)
                .signWith(secretKey, SignatureAlgorithm.HS256)//Signature（签名）SignatureAlgorithm即签名算法，secretKey即密钥
                .compact();
        return jwt;
    }

    //校验token
    public static Claims parseToken(String token){

        if(token == null){
            throw new LeaseException(ResultCodeEnum.ADMIN_LOGIN_AUTH);
        }

        try{
            Jws<Claims> claimsJws = Jwts
                    .parserBuilder()//解析器的builder
                    .setSigningKey(secretKey)//设置密钥
                    .build()//得到解析器
                    .parseClaimsJws(token);//该方法会将jwt的Payload（负载）解析出来

            return claimsJws.getBody();
        }catch(ExpiredJwtException e){
            throw new LeaseException(ResultCodeEnum.TOKEN_EXPIRED);
        }catch(JwtException e){
            throw new LeaseException(ResultCodeEnum.TOKEN_INVALID);
        }
    }

    public static void main(String[] args) {
        System.out.println(createToken(8L,"18828038044"));
    }
}
