package com.kezaihui.faq.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通用返回值
 *
 * @author wanghaibing
 * @date 2018-11-08
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultData<T> implements Result {

    public final static int SUCCESS_CODE = 200;

    public final static int ERR_CODE = 500;
    public final static String SUCCESS_MESSAGE = "操作成功";

    public final static ResultData SUCCESS = success().build();

    private Integer code;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer totalCount;

    public static <M> ResultDataBuilder<M> success() {
        return ResultData.<M>builder().code(SUCCESS_CODE).message(SUCCESS_MESSAGE);
    }

    public static <M> ResultDataBuilder<M> error() {
        return ResultData.<M>builder().code(ERR_CODE);
    }

    public static <M> ResultDataBuilder<M> error(String messageStr) {
        return ResultData.<M>builder().code(ERR_CODE).message(messageStr);
    }
}
