package com.kezaihui.faq.controller.viewObject;

import com.kezaihui.faq.emum.DataType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FaqPairDetailVo {
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
}
