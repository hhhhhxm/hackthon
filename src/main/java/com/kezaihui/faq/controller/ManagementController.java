package com.kezaihui.faq.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kezaihui.faq.config.RetrievalConfig;
import com.kezaihui.faq.entity.FaqPair;
import com.kezaihui.faq.response.CommonReturnType;
import com.kezaihui.faq.response.ResultData;
import com.kezaihui.faq.service.ManagementService;
import com.kezaihui.faq.vo.QuestionListVo;
import com.kezaihui.faq.vo.QuestionVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Objects;
import java.util.List;

/**
 * @Author: lerry_li
 * @CreateDate: 2022/03/31
 * @Description
 */
@RestController
@RequestMapping("/management")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")   //处理跨域请求
@Slf4j
public class ManagementController {
    private final static String ContentType = "application/x-www-form-urlencoded";

    @Autowired
    private ManagementService managementService;

    @Autowired
    private RetrievalConfig retrievalConfig;

    /**
     * 全量同步，将mysql中的faq_pair表全部同步到redis中
     */
    @RequestMapping(value = "/total_synchronize", method = RequestMethod.GET)
    public CommonReturnType totalSynchronize() throws IOException {

        String tableIndexName = "faq_pair";
        //检查表/索引名是否有效
        if (!retrievalConfig.getIndex().getFaqPair().equals(tableIndexName)) {
            log.error("{}不在可以同步的表/索引中", tableIndexName);
            return CommonReturnType.failed(String.format("%s不在可以同步的表/索引中", tableIndexName));
        }

        //统计耗时
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        int account = managementService.totalSynchronize(tableIndexName);
        stopWatch.stop();

        if (account == 0) {
            return CommonReturnType.failed(String.format("mysql表%s中0条数据被同步", tableIndexName));
        }

        return CommonReturnType.success(String.format("成功同步mysql表%s中%d条数据到es索引%s，耗时%dms", tableIndexName, account, tableIndexName, stopWatch.getTotalTimeMillis()));
    }


    @RequestMapping(value = "/update_multi_turn_qa_tree", method = RequestMethod.GET)
    public CommonReturnType updateMultiRoundQATree() {
        int account = managementService.updateMultiTree();
        if (account == 0) {
            return CommonReturnType.failed(null);
        }
        return CommonReturnType.success(String.format("成功更新%d个多轮问答树到redis", account));
    }

    @GetMapping("/list")
    public ResultData<List<FaqPair>> list(QuestionListVo questionListVo) {
        Page<FaqPair> faqPairPage = managementService.page(questionListVo);
        return ResultData.<List<FaqPair>>success().data(faqPairPage.getRecords()).totalCount((int) faqPairPage.getTotal()).build();
    }


    @PutMapping("/{qaId}")
    public ResultData update(@RequestBody QuestionVo questionVo, @PathVariable Integer qaId) {
        managementService.update(questionVo, qaId);
        return ResultData.SUCCESS;
    }

    /**
     * 新增问题
     *
     * @return
     */
    @PostMapping("")
    public ResultData addFaqPair(@RequestBody FaqPair faqPair) throws IOException {
        if (Objects.isNull(faqPair)){
            throw new RuntimeException("参数异常");
        }
        managementService.addFaqPair(faqPair);
        //同步到搜索引擎
        try {
            totalSynchronize();
        }catch (Exception e){
            e.printStackTrace();
        }
        return ResultData.SUCCESS;
    }

    @DeleteMapping("/{id}")
    public ResultData deleteFaqPair(@PathVariable Integer id){
        managementService.deleteFaqPair(id);
        //同步到搜索引擎
        try {
            totalSynchronize();
        }catch (Exception e){
            e.printStackTrace();
        }
        return ResultData.SUCCESS;
    }

    @GetMapping("/top_list")
    public ResultData<List<FaqPair>> topList() {
        List<FaqPair> faqPairs = managementService.topList();
        return ResultData.<List<FaqPair>>success()
                .data(faqPairs).build();
    }

    @PutMapping("/add/{qa_id}")
    public ResultData addCount(@PathVariable("qa_id") Integer qaId) {
        managementService.addCount(qaId);
        return ResultData.SUCCESS;
    }

}
