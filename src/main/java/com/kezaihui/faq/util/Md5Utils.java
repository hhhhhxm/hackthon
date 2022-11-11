package com.kezaihui.faq.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * md5工具
 *
 * @author huangxingming
 */
@Component
@Slf4j
public class Md5Utils {

    public String getFileMD5(byte[] data) {

        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.update(data, 0, data.length);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }

    public String getFileMD5Method2(byte[] data) {
        String str = "";
        InputStream in = new ByteArrayInputStream(data);
        try {
            str = DigestUtils.md5Hex(in);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.error("获取md5关闭输入流失败", e);
                }
            }
        }
        return str;
    }

}
