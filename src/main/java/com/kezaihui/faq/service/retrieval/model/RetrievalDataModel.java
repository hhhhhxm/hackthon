package com.kezaihui.faq.service.retrieval.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author: lerry_li
 * @CreateDate: 2022/03/31
 * @Description
 */
@Data
public class RetrievalDataModel {
    //docId
    private Integer id;
    //问答对的qa_id
    private String standardQuestion;
    //标准问
    private String textValue;
    //标准答
    private Integer creatorId;
    //标准答
    private String creatorName;
    //标准答
    private String type;
    //标准答
    private Boolean inUse;
    //创建时间
    private LocalDateTime createdAt;
    //更新时间
    private LocalDateTime updatedAt;
    //相关度得分
    private Double relevanceScore;
    //相似度得分
    private Double similarityScore;
    //置信度
    private Double confidence;
}
