package com.kezaihui.faq.vo;

import com.kezaihui.faq.emum.DataType;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnswerVo {

    /**
     * 数据类型
     */
    private DataType dataType;

    /**
     * 文本类型
     */
    private String textValue;
}
