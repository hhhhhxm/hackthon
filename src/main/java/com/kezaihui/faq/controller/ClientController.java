package com.kezaihui.faq.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kezaihui.faq.controller.requestObject.CreateAskRo;
import com.kezaihui.faq.response.ResultData;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/client")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
@Slf4j
public class ClientController {
    private static final int HTTP_SUCCESS_CODE = 0;

    private static final String ROB_URL = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=78339ff3-8da5-4af6-98b7-1132abef81cf";


    @GetMapping("/client/create_ask")
    public ResultData<?> createAsk(@RequestBody CreateAskRo askRo) {
        if (!CollectionUtils.isEmpty(askRo.getBase64List())) {
            askRo.getBase64List().forEach(base64 -> {
                sendPicToWX(ROB_URL, base64);
            });
        }
        sendTextToWX(ROB_URL, "问题：" + askRo.getQuestion());

        return ResultData.SUCCESS;
    }

    private void sendPicToWX(String url, String base64) {

        JSONObject queryObj = new JSONObject();
        queryObj.put("msgtype", "image");
        JSONObject contentObj = new JSONObject();
        contentObj.put("base64", base64);
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
