package com.wzw.ziweishopcity.ziweishopcitythirdparty.controller;

import com.aliyun.oss.OSS;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.wzw.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class OSSController {
    @Autowired
    OSS ossClient;

    @RequestMapping("/oss/policy")
    public R policy() {

        //  https://ziweishopcity.oss-cn-beijing.aliyuncs.com/%E7%A7%98%E7%B1%8D.txt
        String accessId = "LTAI5tRjFgDMhfM2PGx5HEYG"; // 请填写您的AccessKeyId。
        String accessKey = "A6iFLleumPvNs7yeOY6rrwDmImWYji"; // 请填写您的AccessKeySecret。
        String endpoint = "oss-cn-beijing.aliyuncs.com"; // 请填写您的 endpoint。
        String bucket = "ziweishopcity"; // 请填写您的 bucketname 。
        String host = "https://" + bucket + "." + endpoint; // host的格式为 bucketname.endpoint
        // callbackUrl为上传回调服务器的URL，请将下面的IP和Port配置为您自己的真实信息。
        // String callbackUrl = "http://88.88.88.88:8888";

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String dir = simpleDateFormat.format(date) + "/"; // 用户上传文件时指定的前缀。这个会在远程oss中产生文件夹

        try {
            long expireTime = 30;
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
            // PostObject请求最大可支持的文件大小为5 GB，即CONTENT_LENGTH_RANGE为5*1024*1024*1024。
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

            String postPolicy = ossClient.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes("utf-8");
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = ossClient.calculatePostSignature(postPolicy);

            Map<String, Object> respMap = new LinkedHashMap<String, Object>();
            respMap.put("accessid", accessId);
            respMap.put("policy", encodedPolicy);
            respMap.put("signature", postSignature);
            respMap.put("dir", dir);
            respMap.put("host", host);
            respMap.put("expire", String.valueOf(expireEndTime / 1000));
            // respMap.put("expire", formatISO8601Date(expiration));
            ossClient.shutdown();
            R r = new R();
            R data = r.put("data", respMap);


            return data;


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}

