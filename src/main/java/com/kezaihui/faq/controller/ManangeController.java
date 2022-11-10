package com.kezaihui.faq.controller;

import com.kezaihui.faq.controller.viewObject.FaqPairDetailVo;
import com.kezaihui.faq.dao.FaqPairDao;
import com.kezaihui.faq.entity.FaqPair;
import com.kezaihui.faq.response.ResultData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;

@RestController
@RequestMapping("/management")
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*")
@Slf4j
public class ManangeController {

    @Autowired
    private FaqPairDao faqPairDao;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private ManagementController managementController;


    @GetMapping("/manange/{qaId}")
    public ResultData<FaqPairDetailVo> getDetail(@PathVariable("qaId") Integer qaId) {
        FaqPair detail = faqPairDao.getDetail(qaId);
        if (Objects.isNull(detail)) {
            return ResultData.<FaqPairDetailVo>error("沒有找到").build();
        }
        return ResultData.<FaqPairDetailVo>success()
                .data(FaqPairDetailVo.builder()
                        .standardQuestion(detail.getStandardQuestion())
                        .textValue(detail.getTextValue())
                        .type(detail.getType())
                        .inUse(detail.getInUse())
                        .creatorId(detail.getCreatorId())
                        .creatorName(detail.getCreatorName())
                        .askCount(detail.getAskCount())
                        .build())
                .build();
    }

    @PutMapping("/open/{qaId}")
    public ResultData<FaqPairDetailVo> openDetail(@PathVariable("qaId") Integer qaId) {
        return transactionTemplate.execute(status -> changeInUse(qaId, true));
    }

    private ResultData<FaqPairDetailVo> changeInUse(Integer qaId, boolean inUse) {
        FaqPair detail = faqPairDao.getDetail(qaId);
        if (Objects.isNull(detail)) {
            return ResultData.<FaqPairDetailVo>error("沒有找到").build();
        }
        detail.setInUse(inUse);
        detail.setUpdatedAt(LocalDateTime.now());
        faqPairDao.updateById(detail);
        try {
            managementController.totalSynchronize();
        } catch (IOException e) {
            return ResultData.<FaqPairDetailVo>success().build();
        }
        return ResultData.<FaqPairDetailVo>success().build();
    }

    @PutMapping("/manange/close/{qaId}")
    public ResultData<FaqPairDetailVo> closeDetail(@PathVariable("qaId") Integer qaId) {
        return transactionTemplate.execute(status -> changeInUse(qaId, false));
    }
}


