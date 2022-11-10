package com.kezaihui.faq.vo;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionVo {

    /**
     * standardQuestion
     */
    private String standardQuestion;
    /**
     * textValue
     */
    private String textValue;
    /**
     * type
     */
    private String type;

    private Integer  creatorId;

    private String creatorName;

    private Boolean inUse;
}
