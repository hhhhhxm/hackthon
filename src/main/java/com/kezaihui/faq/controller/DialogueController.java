package com.kezaihui.faq.controller;

import com.kezaihui.faq.config.DialogueConfig;
import com.kezaihui.faq.controller.viewObject.DialogueResultVO;
import com.kezaihui.faq.dao.FaqPairDao;
import com.kezaihui.faq.dataObject.DialogueStatus;
import com.kezaihui.faq.entity.FaqPair;
import com.kezaihui.faq.response.CommonReturnType;
import com.kezaihui.faq.response.ResultData;
import com.kezaihui.faq.service.DialogueService;
import com.kezaihui.faq.vo.AnswerResultVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * @Author: lerry_li
 * @CreateDate: 2021/01/17
 * @Description
 */
@RestController
@RequestMapping("/dialogue")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")   //处理跨域请求
@Slf4j
public class DialogueController {

    @Autowired
    private DialogueService dialogueService;



    @RequestMapping(value = "/ask", method = RequestMethod.GET)
    @ResponseBody
    public ResultData ask(
            @RequestParam(name = "question") String question) throws IOException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        //调用service回答
        AnswerResultVo result = dialogueService.answer2(question);
        stopWatch.stop();
        log.info("当前用户提问\"{}\"，处理耗时{}ms", question, stopWatch.getTotalTimeMillis());
        return ResultData.<AnswerResultVo>success()
                .data(result)
                .build();
    }


}
