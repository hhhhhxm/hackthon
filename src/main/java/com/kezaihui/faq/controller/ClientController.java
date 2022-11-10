package com.kezaihui.faq.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kezaihui.faq.controller.requestObject.CreateAskRo;
import com.kezaihui.faq.response.ResultData;
import com.kezaihui.faq.util.Img2Base64Util;
import com.kezaihui.faq.vo.PicVo;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/client")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
@Slf4j
public class ClientController {
    private static final int HTTP_SUCCESS_CODE = 0;

    private static final String ROB_URL = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=78339ff3-8da5-4af6-98b7-1132abef81cf";

    @Autowired
    private Img2Base64Util img2Base64Util;

    @PostMapping("/create_ask")
    public ResultData<?> createAsk(@RequestBody CreateAskRo askRo) {

        sendTextToWX(ROB_URL, "销售反馈了一个问题,请及时处理,具体问题如下:" + askRo.getQuestion());
        sendTextToWX(ROB_URL, askRo.getQuestion());
        if (!CollectionUtils.isEmpty(askRo.getBase64List())) {
            askRo.getBase64List().forEach(base64 -> {
                base64 = base64.replace("data:image/png;base64,", "");
                InputStream inputStream = null;
                inputStream = img2Base64Util.generateImage(base64);
                //生成base64和md5
                PicVo imgStr = img2Base64Util.getImgStr(inputStream);
                sendPicToWX(ROB_URL, imgStr);
            });
        }
        return ResultData.SUCCESS;
    }

    private void sendPicToWX(String url, PicVo imgStr) {

        JSONObject queryObj = new JSONObject();
        queryObj.put("msgtype", "image");
        JSONObject contentObj = new JSONObject();
        contentObj.put("md5", imgStr.getMd5());
        contentObj.put("base64", imgStr.getBase64());
        queryObj.put("image", contentObj);
        okhttp3.RequestBody body = okhttp3.RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"), JSON.toJSONString(queryObj));
        try {
            String httpResult = postRequestWithTimeout(url, body);
            JSONObject responseObj = JSON.parseObject(httpResult);
            int httpCode = responseObj.getInteger("errcode");
            if (httpCode == HTTP_SUCCESS_CODE) {
                return;
            }
            log.info("通过机器人给群里发图片响应报文:{}", httpResult);
        } catch (IOException e) {
            log.error(e.toString());
        }
    }

    /**
     * 微信群里发文本
     *
     * @param url
     * @param text
     */
    public void sendTextToWX(String url, String text) {

        JSONObject queryObj = new JSONObject();
        queryObj.put("msgtype", "text");
        JSONObject contentObj = new JSONObject();
        contentObj.put("content", text);
        queryObj.put("text", contentObj);
        okhttp3.RequestBody body = okhttp3.RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"), JSON.toJSONString(queryObj));
        try {
            String httpResult = postRequestWithTimeout(url, body);
            JSONObject responseObj = JSONObject.parseObject(httpResult);
            int httpCode = responseObj.getInteger("errcode");
            if (httpCode == HTTP_SUCCESS_CODE) {
                return;
            }
            log.info("通过机器人给群里发图片响应报文:{}", httpResult);
        } catch (IOException e) {
            log.error(e.toString());
        }
    }


    public String postRequestWithTimeout(String url, okhttp3.RequestBody requestBody) throws IOException {
        OkHttpClient okHttpClient = (new OkHttpClient.Builder()).callTimeout(15L, TimeUnit.SECONDS).build();
        Request request = (new Request.Builder()).url(url).post(requestBody).build();
        return Objects.requireNonNull(okHttpClient.newCall(request).execute().body()).string();
    }

}
