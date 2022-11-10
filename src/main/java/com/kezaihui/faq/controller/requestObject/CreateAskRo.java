package com.kezaihui.faq.controller.requestObject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateAskRo {


    private List<String> base64List;

    @NotBlank
    private String question;

    private Integer creatorId;

    private String creatorName;
}
