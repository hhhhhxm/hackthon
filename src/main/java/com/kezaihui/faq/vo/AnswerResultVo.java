package com.kezaihui.faq.vo;

import lombok.*;

import java.util.List;

@Builder
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnswerResultVo {

    private List<AnswerVo> answerList;

}
