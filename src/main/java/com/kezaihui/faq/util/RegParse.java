package com.kezaihui.faq.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 00026
 * @date 2021-03-04 10:55
 * @descript 正则解析工具
 */
public class RegParse {
    public static String apply(String param) {
        return parseIt(param);
    }

    public static String parseIt(String param) {
        String s = param.replaceAll("[\\pP‘’“”\\p{Punct}￥¨ˉ∩∪∈∵∴∞∝≈≠＝≤≥＜＞≮≯∷⊥∥∠⌒⊙≌∽√∏∑∨∧±＋－×÷／∫∮]", "")
                .replaceAll("[\\ud83c\\udc00-\\ud83c\\udfff]", "").replaceAll("[\\ud83d\\udc00-\\ud83d\\udfff]", "")
                .replaceAll("[\\u2600-\\u27ff]", "").replaceAll(" +", "").replaceAll("　+", "").replaceAll("\\u00A0+", "");
        return s;
    }

    public static String parseItWithBlank(String param) {
        String s = param.replaceAll("[\\pP‘’“”\\p{Punct}￥¨ˉ∩∪∈∵∴∞∝≈≠＝≤≥＜＞≮≯∷⊥∥∠⌒⊙≌∽√∏∑∨∧±＋－×÷／∫∮]", "")
                .replaceAll("[\\ud83c\\udc00-\\ud83c\\udfff]", "").replaceAll("[\\ud83d\\udc00-\\ud83d\\udfff]", "")
                .replaceAll("[\\u2600-\\u27ff]", "");
        return s;
    }

    public static String parseItWithBlankSpace(String param) {
        String s = param.replaceAll("[\\pP‘’“”\\p{Punct}￥¨ˉ∩∪∈∵∴∞∝≈≠＝≤≥＜＞≮≯∷⊥∥∠⌒⊙≌∽√∏∑∨∧±＋－×÷／∫∮]", " ")
                .replaceAll("[\\ud83c\\udc00-\\ud83c\\udfff]", "").replaceAll("[\\ud83d\\udc00-\\ud83d\\udfff]", " ")
                .replaceAll("[\\u2600-\\u27ff]", " ");
        return s;
    }

    public static String getEscape(String param) {
        String rex = "[\t\f\b\r\n]";
        Pattern p = Pattern.compile(rex);
        Matcher m = p.matcher(param);
        StringBuffer result = new StringBuffer();
        while (m.find()) {
            result.append(m.group());
        }
        return result.toString();
    }
}
