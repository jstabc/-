package com.sky.utils;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.ObjectMetadata;
import com.sky.constant.MessageConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import java.io.ByteArrayInputStream;
import java.util.UUID;

@Data
@AllArgsConstructor
@Slf4j
public class AliOssUtil {

    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;



    /**
     * 文件上传
     *
     * @param bytes
     * @param originalFileName
     * @return
     */
    public String upload(byte[] bytes, String originalFileName) {
        //截取原始文件名的后缀
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        //构造新文件名称
        String objectName = UUID.randomUUID().toString() + extension;
        //创建OSSClient实例
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            ossClient.putObject(bucketName, objectName, new ByteArrayInputStream(bytes));
        } catch (OSSException e) {
            log.error(MessageConstant.UPLOAD_FAILED);
        } catch (ClientException e) {
            log.info(MessageConstant.UPLOAD_FAILED, e);
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }

        StringBuilder stringBuider = new StringBuilder("https://");
        stringBuider.append(bucketName)
                .append(".")
                .append(endpoint)
                .append("/")
                .append(objectName);
        log.info("文件上传到：{}", stringBuider.toString());
        return stringBuider.toString();

    }

}
