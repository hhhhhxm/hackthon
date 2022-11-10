package com.kezaihui.faq.util;


import info.debatty.java.stringsimilarity.Jaccard;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 00026
 * @date 2021-04-10 11:05
 * @descript
 */
public class Similaritys {
    public static double apply(String s1, String s2) {
        //先去除字符串所有空格转义字符根据字符串长度判定
        String s1NoBS = s1.replace(" ", "").replace("　", "").replace("\t", "").replace("\r", "").replace("\n", "").replace("\f", "").replace("\b", "");
        String s2NoBS = s2.replace(" ", "").replace("　", "").replace("\t", "").replace("\r", "").replace("\n", "").replace("\f", "").replace("\b", "");
        if (s1NoBS.length() < 7 && s2NoBS.length() < 7) {
            return getSimi(s1, s2, false);
        } else
            return getSimi(s1, s2, true);
    }

    private static String getNumsPuncs(String src) {
        //匹配字符串中所有数字和标点空格
        //    val pattern = Pattern.compile("[\\d\\pP“”‘’\\p{Punct}]+")
        Pattern pattern = Pattern.compile("[\\d\\pP“”‘’\\p{Punct}]+");
        Matcher matcher = pattern.matcher(src);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            sb.append(matcher.group(0));
        }
        if (sb.length() >= 1) {
            return sb.toString();
        } else
            return "";
    }

    private static double getSimi(String s1, String s2, Boolean isNumCount) {
        ArrayList<String> spaces = new ArrayList<>();
        spaces.add(" ");
        spaces.add("　");
        spaces.add("\u00A0");
        double sim = 0.0;
        //两者一样 返回1.0
        if (s1 != null && s2 != null && s1.replaceAll("\u00A0+", " ").replaceAll("　+", " ").replaceAll(" +", " ").replaceAll("\t", "").replaceAll("\r", "").replaceAll("\n", "").replaceAll("\f", "").replaceAll("\b", "").equalsIgnoreCase(s2.replaceAll("\u00A0+", " ").replaceAll("　+", " ").replaceAll(" +", " ").replaceAll("\t", "").replaceAll("\r", "").replaceAll("\n", "").replaceAll("\f", "").replaceAll("\b", "")))
            return 1.0;
        else if (Objects.equals(null, s1) || Objects.equals(null, s2)) {
            return 0.0;
        } else {
            //替换掉所有标点符号 .replace("\t","").replace("\r","").replace("\n","").replace("\f","").replace("\b","")
            //20190717去掉replaceAll("[\\d\\pP“”‘’\\p{Punct}]+","").replaceAll(" ","").replace("\u00A0","").replace("　","").
            String srcS1 = RegParse.parseItWithBlank(s1).replaceAll("\u00A0+", " ").replaceAll("　+", " ").replaceAll(" +", " ").replaceAll("\t", "").replaceAll("\r", "").replaceAll("\n", "").replaceAll("\f", "").replaceAll("\b", "");
            String srcS2 = RegParse.parseItWithBlank(s2).replaceAll("\u00A0+", " ").replaceAll("　+", " ").replaceAll(" +", " ").replaceAll("\t", "").replaceAll("\r", "").replaceAll("\n", "").replaceAll("\f", "").replaceAll("\b", "");
            //获取两字符串中所有数字和标点
            String s1PN = getNumsPuncs(s1);
            String s2PN = getNumsPuncs(s2);
            List<String> s1Num = extractNums(s1.replace("\u00A0+", " ").replace("　+", " ").replaceAll(" +", " ").replaceAll("\t", "").replaceAll("\r", "").replaceAll("\n", "").replaceAll("\f", "").replaceAll("\b", ""));
            List<String> s2Num = extractNums(s2.replace("\u00A0+", " ").replace("　+", " ").replaceAll(" +", " ").replaceAll("\t", "").replaceAll("\r", "").replaceAll("\n", "").replaceAll("\f", "").replaceAll("\b", ""));
            //去掉空格标点后元素忽略大小写相同，阿拉伯数字集合长度一样(两者数字集合都不为0)并且数字不计入匹配率的情况
            if (srcS1.equalsIgnoreCase(srcS2) && !isNumCount) {
                return 0.99;
            } else {
                //数字个数，标点，其余内容都不一样
                //将s1转换为小写并去除空格的集合并赋给longer
                //        val s1List=srcS1.toLowerCase
                List<String> s1List = FindSubs.turnIntoList(new FindSubs().getAllElements(srcS1.toLowerCase()));
                s1List.removeAll(spaces);
                List<String> longer = s1List;
                //将s2转换为小写并去除空格的集合并赋给shorter
                List<String> s2List = FindSubs.turnIntoList(new FindSubs().getAllElements(srcS2.toLowerCase()));
                s2List.removeAll(spaces);
                List<String> shorter = s2List;
                //若s1长度小于s2 longer shorter互换
                if (s1List.size() < s2List.size()) { // longer should always have greater length
                    longer = s2List;
                    shorter = s1List;
                }
                int longerLength = longer.size();
                //两句话中不一样的数字
                //        val diffNums=EditDistance.editDistance(s1Num.toString().substring(7,s1Num.toString().length-1), s2Num.toString().substring(7,s2Num.toString().length-1))
                int diffNums = ListEditDistance.editDistance2(s1Num, s2Num);
                double weight = (1.0 / Double.valueOf(longerLength)) / 30.0;
                //如果数字标点完全一样
                if (s1PN.equals(s2PN)) {
                    if (longerLength == 0) {
                        return 1.0; /* both strings are zero length */
                    } else {
                        //相似度
                        sim = (longerLength - ListEditDistance.editDistance2(longer, shorter)) / Double.valueOf(longerLength);
                        if (sim >= 0.6 && sim < 0.91)
                            sim = sim + 0.03;
                        else if (sim < 0.5)
                            sim = sim + 0.05;
                        else
                            sim = sim;
                        return sim - diffNums * weight;

                    }
                } else {
                    if (longerLength == 0) {
                        return 0.99;/* both strings are zero length */
                    } else {
                        //相似度
                        sim = (longerLength - ListEditDistance.editDistance2(longer, shorter)) / Double.valueOf(longerLength);
                        if (sim >= 0.6 && sim < 0.91)
                            sim = sim + 0.03;
                        else if (sim < 0.5)
                            sim = sim + 0.05;
                        else
                            sim = sim;
                        if (sim > 0.02)
                            return (sim - 0.02) - diffNums * weight;
                        else
                            return 0.0;
                    }
                }
            }
        }
    }

    /**
     * 将src中的数字按组拆分放入集合中
     *
     * @param src
     * @return
     */
    public static List<String> extractNums(String src) {
        List<String> result = new ArrayList<>();
        //只能匹配小数
        //    val regxStr="-?\\d+(\\.\\d+)?"
        //匹配小数或者分数
        //    val regxStr="(-?\\d+(\\.\\d+)?/-?\\d+(\\.\\d+)?|-?\\d+(\\.\\d+)?)"
        //匹配如-1.2/-5.279852、1.58415.157.1581……1,781,688,581……，481.474等情况数字模型 20181212
        String regxStr = "(-?\\d+(\\,\\d+)*(\\.\\d+)*/-?\\d+(\\,\\d+)*(\\.\\d+)*|-?\\d+(\\,\\d+)*(\\.\\d+)*)";
        Pattern pt = Pattern.compile(regxStr);
        Matcher m = pt.matcher(src);
        while (m.find()) {
            result.add(m.group());
        }
        return result;
    }

    private static double editDiff(String s1, String s2) {
        Jaccard jaccard = new Jaccard(4);
        return jaccard.similarity(s1, s2);
    }
}
