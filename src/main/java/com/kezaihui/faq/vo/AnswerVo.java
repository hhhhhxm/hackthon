package com.kezaihui.faq.vo;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerVo {

    /**
     * 数据类型
     */
    private String dataType;

    /**
     * 文本类型
     */
    private String textValue;
    /**
     * 简短的答案
     */
    private String standardQuestion;
}
