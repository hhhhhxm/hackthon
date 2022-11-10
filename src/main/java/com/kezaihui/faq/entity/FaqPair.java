package com.kezaihui.faq.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kezaihui.faq.emum.DataType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author: lerry_li
 * @CreateDate: 2022/03/31
 * @Description 对应数据表：faq_pair
 */
@ApiModel("FAQ问答对：标准问-标准答")
@Data
@TableName("faq_pair")
public class FaqPair {
    private Integer id;
    //标准问
    private String standardQuestion;
    //标准答
    private String textValue;
    //创建人id
    private Integer creatorId;
    //创建人姓名
    private String creatorName;
    //答案类型
    private DataType type;
    //是否使用
    private Boolean inUse;
    //创建日期
    private LocalDateTime createdAt;
    //更新日期
    private LocalDateTime updatedAt;
}
