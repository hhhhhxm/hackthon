package com.kezaihui.faq.response;

/**
 * 通用返回
 *
 * @author wanghaibing
 * @date 2018-11-08
 */
public interface Result {

    /**
     * 获取code
     *
     * @return
     */
    Integer getCode();

    /**
     * 获取消息
     *
     * @return
     */
    String getMessage();

}
