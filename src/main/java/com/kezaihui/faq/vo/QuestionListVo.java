package com.kezaihui.faq.vo;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionListVo {
    private String standardQuestion;
    private Integer creatorId;
    @Builder.Default
    private Integer page = 1;
    @Builder.Default
    private Integer size = 10;
}
