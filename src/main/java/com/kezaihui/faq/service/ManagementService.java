package com.kezaihui.faq.service;

import com.kezaihui.faq.entity.FaqPair;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kezaihui.faq.entity.FaqPair;
import com.kezaihui.faq.vo.QuestionListVo;
import com.kezaihui.faq.vo.QuestionVo;

import java.io.IOException;

/**
 * @Author: lerry_li
 * @CreateDate: 2022/03/31
 * @Description
 */
public interface ManagementService {
    /**
     * 全量同步，从mysql中同步一张表的所有数据到es对应的索引中
     *
     * @param tableIndexName 表名/索引名
     * @return 成功操作的数据总数
     */
    int totalSynchronize(String tableIndexName) throws IOException;

    /**
     * 更新多轮问答树到redis
     *
     * @return 成功更新的数量
     */
    int updateMultiTree();

    Page<FaqPair> page(QuestionListVo questionListVo);

    void update(QuestionVo questionVo, Integer qaId);

    /**
     * 管理端新增问题
     *
     * @param faqPair
     */
    void addFaqPair(FaqPair faqPair);

    /**
     * 管理端删除问题
     *
     * @param id
     */
    void deleteFaqPair(Integer id);
}
