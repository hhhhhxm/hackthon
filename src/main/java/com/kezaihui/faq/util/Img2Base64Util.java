package com.kezaihui.faq.util;

import com.kezaihui.faq.vo.PicVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * 将图片转换为Base64<br>
 * 将base64编码字符串解码成img图片
 *
 * @创建时间 2022-02-26
 */
@Component
@Slf4j
public class Img2Base64Util {

    @Autowired
    private Md5Utils md5Utils;


    /**
     * 将图片转换成Base64编码
     *
     * @param inputStream 输入流
     * @return
     */
    public PicVo getImgStr(InputStream inputStream) {
        PicVo picVo = PicVo.builder().build();
        //将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        //最多不能超过2mb
        //读取图片字节数组
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] data = null;
        try {
            byte[] buffer = new byte[4096];
            int n = 0;
            while (-1 != (n = inputStream.read(buffer))) {
                output.write(buffer, 0, n);
            }
            data = output.toByteArray();
        } catch (Exception e) {
            log.error("生成base64失败", e);
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    log.error("生成base64关闭输出流失败", e);
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("生成base64关闭输入流失败", e);
                }
            }
        }
        String base64 = new String(Base64.encodeBase64(data));
        //获取md5
        String fileMD5 = md5Utils.getFileMD5Method2(data);
        picVo.setBase64(base64);
        picVo.setMd5(fileMD5);
        return picVo;
    }

    /**
     * 对字节数组字符串进行Base64解码并生成图片
     *
     * @param imgStr 图片数据的base64
     * @return
     */
    public InputStream generateImage(String imgStr) {
        if (imgStr == null) {
            return null;
        }
        InputStream inputStream = null;
        //Base64解码
        byte[] b = Base64.decodeBase64(imgStr);
        for (int i = 0; i < b.length; ++i) {
            if (b[i] < 0) {
                //调整异常数据
                b[i] += 256;
            }
        }
        //生成图片的输入流 方便后续使用
        inputStream = new ByteArrayInputStream(b);
        return inputStream;
    }
}