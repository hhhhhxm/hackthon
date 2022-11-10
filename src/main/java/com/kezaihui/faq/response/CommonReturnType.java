package com.kezaihui.faq.response;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: lerry_li
 * @CreateDate: 2022/03/31
 * @Description 通用返回类型，返回给前端
 */
@Data
public class CommonReturnType implements Serializable {
    //状态码
    private Integer code;
    //状态码说明
    private String msg;
    //返回数据内容
    private Object data;

    //通用创建方法，指定状态码
    public static CommonReturnType create(Object data, CodeMsg codeMsg) {
        return create(data, codeMsg.getCode(), codeMsg.getMsg());
    }

    //通用创建方法，操作成功
    public static CommonReturnType success(Object data) {
        return create(data, CodeMsg.SUCCESS.getCode(), CodeMsg.SUCCESS.getMsg());
    }

    //通用创建方法，操作失败
    public static CommonReturnType failed(Object data) {
        return create(data, CodeMsg.FAILED.getCode(), CodeMsg.FAILED.getMsg());
    }

    public static CommonReturnType create(Object data, Integer code, String msg) {
        CommonReturnType commonReturnType = new CommonReturnType();
        commonReturnType.setCode(code);
        commonReturnType.setMsg(msg);
        commonReturnType.setData(data);
        return commonReturnType;
    }
}
