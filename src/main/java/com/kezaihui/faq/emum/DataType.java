package com.kezaihui.faq.emum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter

public enum DataType {
    TEXT("纯文本显示"),
    HREF("跳转超链接");

    private String desc;
}
