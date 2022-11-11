package com.kezaihui.faq.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huangxingming
 * @date 2022/02/26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PicVo {
    /**
     * md5码
     */
    private String md5;
    /**
     * base64码
     */
    private String base64;
}
