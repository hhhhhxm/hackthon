package com.kezaihui.faq.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.kezaihui.faq.emum.DataType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Author: lerry_li
 * @CreateDate: 2022/03/31
 * @Description 对应数据表：faq_pair
 */
@Data
@TableName("faq_pair")
public class FaqPair {
    @TableId(value = "id",type = IdType.AUTO)
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
