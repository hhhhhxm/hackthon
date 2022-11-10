package com.kezaihui.faq.util;


import info.debatty.java.stringsimilarity.NGram;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author 00026
 * @date 2021-04-10 10:42
 * @descript
 */
public class Similarity {
    static int PENALTY = 2;
    static String[] ignorables = null;
    static boolean loaded = false;

    public static double apply(String s1, String s2) {
        //获取二串中的转义/b/f/n/r/t
        String s1Escape = RegParse.getEscape(s1);
        String s2Escape = RegParse.getEscape(s2);
        //没有数字特殊标点的字符串
        String s1NoNumsNoPunc = RegParse.parseItWithBlank(s1.replaceAll("\t+", " ").replaceAll("\r+", " ").replaceAll("\n+", " ").replaceAll("\f+", " ").replaceAll("\b+", " ").replaceAll("　+", " ").replaceAll("\\u00A0+", " ").replaceAll(" +", " ").trim().replaceAll("(-?\\d+(\\,\\d+)*(\\.\\d+)*/-?\\d+(\\,\\d+)*(\\.\\d+)*|-?\\d+(\\,\\d+)*(\\.\\d+)*)", "")).trim();
        String s2NoNumsNoPunc = RegParse.parseItWithBlank(s2.replaceAll("\t+", " ").replaceAll("\r+", " ").replaceAll("\n+", " ").replaceAll("\f+", " ").replaceAll("\b+", " ").replaceAll("　+", " ").replaceAll("\\u00A0+", " ").replaceAll(" +", " ").trim().replaceAll("(-?\\d+(\\,\\d+)*(\\.\\d+)*/-?\\d+(\\,\\d+)*(\\.\\d+)*|-?\\d+(\\,\\d+)*(\\.\\d+)*)", "")).trim();
        double result = 0.0;
        //无转义字符串但是有数字和常见标点字符串
        String s1NormalB = s1.replaceAll("\t+", " ").replaceAll("\r+", " ").replaceAll("\n+", " ").replaceAll("\f+", " ").replaceAll("\b+", " ").toLowerCase().replaceAll("　+", " ").replaceAll("\\u00A0+", " ").replaceAll(" +", " ").trim();
        String s2NormalB = s2.replaceAll("\t+", " ").replaceAll("\r+", " ").replaceAll("\n+", " ").replaceAll("\f+", " ").replaceAll("\b+", " ").toLowerCase().replaceAll("　+", " ").replaceAll("\\u00A0+", " ").replaceAll(" +", " ").trim();
        //无特殊符号但是有数字
        String x = RegParse.parseItWithBlank(s1NormalB).trim();
        String y = RegParse.parseItWithBlank(s2NormalB).trim();
        List<String> cx = FindSubs.trimBlank(FindSubs.turnIntoList(new FindSubs().getAllElements(RegParse.parseItWithBlankSpace(s1NormalB))));
        List<String> cy = FindSubs.trimBlank(FindSubs.turnIntoList(new FindSubs().getAllElements(RegParse.parseItWithBlankSpace(s2NormalB))));
        //无常见数字但是有特殊符号
        //    val xNoNums = s1NormalB.replaceAll("(-?\\d+(\\,\\d+)*(\\.\\d+)*/-?\\d+(\\,\\d+)*(\\.\\d+)*|-?\\d+(\\,\\d+)*(\\.\\d+)*)","")
        //    val yNoNums = s2NormalB.replaceAll("(-?\\d+(\\,\\d+)*(\\.\\d+)*/-?\\d+(\\,\\d+)*(\\.\\d+)*|-?\\d+(\\,\\d+)*(\\.\\d+)*)","")
        String xNoNums = s1NormalB.replaceAll("\\d", "");
        String yNoNums = s2NormalB.replaceAll("\\d", "");
        //去除连续空格含有数字标点的集合
        List<String> c1 = FindSubs.trimBlank(FindSubs.turnIntoList(new FindSubs().getAllElements(s1NormalB)));
        List<String> c2 = FindSubs.trimBlank(FindSubs.turnIntoList(new FindSubs().getAllElements(s2NormalB)));
        //    var pureS1 = x.replaceAll("(-?\\d+(\\,\\d+)*(\\.\\d+)*/-?\\d+(\\,\\d+)*(\\.\\d+)*|-?\\d+(\\,\\d+)*(\\.\\d+)*)","").trim
        //    var pureS2 = y.replaceAll("(-?\\d+(\\,\\d+)*(\\.\\d+)*/-?\\d+(\\,\\d+)*(\\.\\d+)*|-?\\d+(\\,\\d+)*(\\.\\d+)*)","").trim
        String pureS1 = x.replaceAll("\\d", "").trim();
        String pureS2 = y.replaceAll("\\d", "").trim();
        //不含数字标点的字符串集合
        List<String> c1Pure = FindSubs.turnIntoList(new FindSubs().getAllElements(pureS1));
        List<String> c2Pure = FindSubs.turnIntoList(new FindSubs().getAllElements(pureS2));
        List<String> c1CN = c1Pure.stream().filter(a -> a.matches("^[\\u4e00-\\u9fa5]*")).collect(Collectors.toList());
        List<String> c2CN = c2Pure.stream().filter(b -> b.matches("^[\\u4e00-\\u9fa5]*")).collect(Collectors.toList());
        //    val longerVecPure = if (c1Pure.size > c2Pure.size) c1Pure.size else c2Pure.size
        //    val longerVecWeight = 1.0/longerVecPure
        //    val listEditDistance = ListEditDistance.editDistance2(FindSubs.turnIntoList(new FindSubs().getAllElements(pureS1)),FindSubs.turnIntoList(new FindSubs().getAllElements(pureS2)))
        List<String> xNums = Similaritys.extractNums(s1NormalB);
        List<String> yNums = Similaritys.extractNums(s2NormalB);
        int numDiff = ListEditDistance.editDistance2(xNums, yNums);
        int longerLen = (c1.size() > c2.size()) ? c1.size() : c2.size();
        double weight = (1.0 / Double.valueOf(longerLen)) / 3.0;
        result = similarity(xNoNums, yNoNums) / Double.valueOf(100);
        //数字越小匹配率越大，editDiff的同理
        double ngramSimi = 1.0 - new NGram(2).distance(xNoNums, yNoNums);
        //中文计算时，与trados差异太大

        double additionalScore = 2.0 * result / 15.0;
        double matchpart = 0.0;
        if (pureS1.length() == 0 && pureS2.length() == 0) {
            //原文是数字+符号的情况
            double pucSimi = 1.0 - new NGram(1).distance(xNoNums, yNoNums);
            if (cx.size() > 2 || cy.size() > 2) {
                int longLenth = (cx.size() > cy.size()) ? cx.size() : cy.size();
                double numRe = 1.0 - ListEditDistance.editDistance2(cx, cy) / (longLenth * 1.0);
                if (numRe < 0.7)
                    numRe += 0.05;
                return numRe - (1.0 - pucSimi) * weight / 4.0;
            } else {
                double numRe = 0.0;
                if (Objects.equals(x, y)) {
                    numRe = 1.0;
                    return numRe - (1.0 - pucSimi) * weight / 4.0;
                } else {
                    ArrayList<String> crossList = new ArrayList<>();
                    crossList.addAll(cx);
                    crossList.retainAll(cy);
                    switch (crossList.size()) {
                        case 1:
                            numRe = 0.69;
                            break;
                        case 2:
                            numRe = 0.99;//位置相反,数字全一样
                            break;
                        default:
                            numRe = 0.0;
                    }
                    return numRe - (1.0 - pucSimi) * weight / 4.0;
                }
            }
        } else {
            if (c1CN.size() / Double.valueOf(c1Pure.size()) >= 0.5 || c2CN.size() / Double.valueOf(c2Pure.size()) >= 0.5) {
                //汉字占比在分词集合中超过50%，比trados差距大且均比trados小
                /**
                 * 如果c1CN和C1Pure长度相等 （C2CN同理），纯汉字就不补充匹配率，否则补充5%匹配率
                 */
                double backNG = ngramSimi;
                double backRe = result;

                if (result < 0.4)
                    additionalScore += 0.08;
                else if (result >= 0.4 && result < 0.6)
                    additionalScore += 0.04;
                else if (result >= 0.6 && result < 0.8)
                    additionalScore -= 0.04;
                else if (result >= 0.8 && result < 0.89)
                    additionalScore = 3 / Double.valueOf(10) * additionalScore + 0.01;
                else
                    additionalScore = 0.0;
                matchpart = (result + additionalScore >= 1.0) ? result : result + additionalScore;
                if (result == 1.0 && !Objects.equals(x, y))
                    matchpart -= 0.01;

                if (ngramSimi < 0.55)
                    ngramSimi += 0.09;
                else if (ngramSimi >= 0.55 && ngramSimi < 0.7)
                    ngramSimi += 0.08;
                else if (ngramSimi >= 0.7 && ngramSimi < 0.8)
                    ngramSimi += 0.07;
                else {
                    //        if (ngramSimi <0.88)
                    //          ngramSimi +=0.05
                    //        else{
                    if (ngramSimi + 0.033 < 0.99)
                        ngramSimi += 0.033;
                    //        }

                }
                if (ngramSimi > 0.99 && ngramSimi < 1.0)
                    ngramSimi = 0.99;
                if (Objects.equals(x, y) && !Objects.equals(s1NormalB, s2NormalB))
                    ngramSimi -= 0.01;
                if (ngramSimi == 1.0 && !Objects.equals(x, y))
                    ngramSimi -= 0.01;
                int diffWordDiffNum = ListEditDistance.editDistance2(FindSubs.trimBlank(FindSubs.turnIntoList(new FindSubs().getAllElements(s1NoNumsNoPunc))), FindSubs.trimBlank(FindSubs.turnIntoList(new FindSubs().getAllElements(s2NoNumsNoPunc))));
                //不含数字常见普通符号字符串分词集合长度均大于7
                if (c1.size() > 7 && c2.size() > 7) {
                    double finalResult = matchpart * 0.45 + ngramSimi * 0.55;
                    //纯汉字句子
                    if (c1CN.size() == c1Pure.size() && c2CN.size() == c2Pure.size()) {
                        if (finalResult < 0.88)
                            finalResult += 0.035;
                        else if (finalResult >= 0.88 && finalResult < 0.95)
                            finalResult += 0.013;
                        else
                            finalResult = finalResult;
                    } else {
                        //包含英文的混合
                        if (finalResult < 0.8)
                            finalResult += (0.06 - 0.02 * diffWordDiffNum / 3.0);
                        else if (finalResult >= 0.8 && finalResult <= 0.88)
                            finalResult += (0.04 - 0.02 * diffWordDiffNum / 3.0);
                        else {
                            if (finalResult < 0.95)
                                finalResult += (0.01 - 0.02 * diffWordDiffNum / 3.0);
                        }
                    }

                    if (numDiff <= 0) {
                        finalResult = finalResult;
                    } else {
                        if (pureS1.length() > 5 || pureS2.length() > 5)
                            finalResult = finalResult - numDiff * weight;
                        else
                            finalResult = finalResult - 0.01 * numDiff / 2;
                    }
                    if (finalResult < 0)
                        finalResult = 0.0;
                    return finalResult;

                } else {
                    //        result = similarity(pureS1,pureS2)/100.toDouble
                    //        backRe = result
                    //去掉特殊标点和数字的纯字字符串
                    if (pureS1.replaceAll(" +", "").matches("^[\\u4e00-\\u9fa5]*"))
                        pureS1 = pureS1.replaceAll(" +", "");
                    if (pureS2.replaceAll(" +", "").matches("^[\\u4e00-\\u9fa5]*"))
                        pureS2 = pureS2.replaceAll(" +", "");
                    //去掉特殊标点和数字的纯字集合
                    c1Pure = FindSubs.turnIntoList(new FindSubs().getAllElements(pureS1));
                    c2Pure = FindSubs.turnIntoList(new FindSubs().getAllElements(pureS2));
                    ngramSimi = 1.0 - new NGram(2).distance(pureS1, pureS2);
                    if (ngramSimi > 0.6) {
                        List<String> longOne = (c1Pure.size() > c2Pure.size()) ? c1Pure : c2Pure;
                        List<String> shortOne = (Objects.equals(longOne, c1Pure)) ? c2Pure : c1Pure;
                        //避免 出现“目测”和“测”匹配率100%这种
                        if ((pureS1.indexOf(pureS2) != -1 || pureS2.indexOf(pureS1) != -1) && !Objects.equals(pureS1, pureS2)) {
                            ngramSimi = Double.valueOf(shortOne.size()) / Double.valueOf(longOne.size());
                        }
                        if (!pureS1.contains(pureS2) && !pureS2.contains(pureS1)) {
                            int diffOne = ListEditDistance.editDistance2(FindSubs.turnIntoList(new FindSubs().getAllElements(pureS1)), FindSubs.turnIntoList(new FindSubs().getAllElements(pureS2)));
                            ngramSimi = 1.0 - Double.valueOf(diffOne) / Double.valueOf(longOne.size());
                        }
                    }
                    backNG = ngramSimi;
                    double wordsDiff = Similaritys.apply(pureS1, pureS2);
                    double backWDiff = wordsDiff;
                    //        if (result <= 0.7)
                    //          result +=0.08
                    if (ngramSimi < 0.5)
                        ngramSimi += 0.04;
                    //数字不一样
                    if (wordsDiff == 1.0 && !Objects.equals(x, y))
                        wordsDiff -= 0.01;
                    //标点不一样
                    if (backWDiff == 1.0 && !Objects.equals(xNoNums, yNoNums))
                        wordsDiff -= 0.01;
                    //大小写不一样
                    int diffWordDiffNums = ListEditDistance.editDistance2(FindSubs.trimBlank(FindSubs.turnIntoList(new FindSubs().getAllElements(s1NoNumsNoPunc))), FindSubs.trimBlank(FindSubs.turnIntoList(new FindSubs().getAllElements(s2NoNumsNoPunc))));
                    if (backWDiff == 1.0 && !Objects.equals(s1NoNumsNoPunc, s2NoNumsNoPunc))
                        wordsDiff -= 0.02 * diffWordDiffNums / 3.0;


                    //        if (result ==1.0 && !Objects.equals(x,y))
                    //          result -= 0.01
                    //        //有标点不一样
                    //        if(backRe ==1.0 && !Objects.equals(xNoNums,yNoNums))
                    //          result -= 0.01
                    //        if (backRe ==1.0 && !Objects.equals(s1NoNumsNoPunc,s2NoNumsNoPunc))
                    //          result -=0.01
                    //数字不一样
                    if (ngramSimi == 1.0 && !Objects.equals(x, y))
                        ngramSimi -= 0.01;
                    //有标点不一样
                    if (backNG == 1.0 && !Objects.equals(xNoNums, yNoNums))
                        ngramSimi -= 0.01;
                    if (backNG == 1.0 && !Objects.equals(s1NoNumsNoPunc, s2NoNumsNoPunc))
                        ngramSimi -= 0.02 * diffWordDiffNum / 3.0;
                    //        if (backNG <= 0.75 || backRe <=0.75)
                    //          (backNG-0.06)*0.5 + (backRe-0.06)*0.5
                    //        else
                    double finalRate = ngramSimi * 0.3 + wordsDiff * 0.7;
                    if (finalRate == 1.0 && !Objects.equals(s1.trim().toLowerCase(), s2.trim().toLowerCase())) {
                        finalRate = finalRate * 0.97 + (1.0 - new NGram(2).distance(s1.trim().toLowerCase(), s2.trim().toLowerCase())) * 0.03;
                    }
                    if (finalRate > 1.0)
                        finalRate = 1.0;
                    if (finalRate < 0)
                        finalRate = 0.0;
                    return finalRate;

                }
            } else {
                //      var editDiff = Similaritys(s1NormalB,s2NormalB)
                //      if (editDiff == 1.0 && !Objects.equals(s1NoNumsNoPunc,s2NoNumsNoPunc))
                //        editDiff -= 0.01
                double backNG = ngramSimi;
                double backRe = result;
                //不含数字常见普通符号字符串分词集合长度均大于4
                if (c1.size() > 5 && c2.size() > 5) {
                    if (result <= 0.4)
                        additionalScore += 0.05;
                    else if (result > 0.4 && result <= 0.6)
                        additionalScore = 2.0 * additionalScore / 5.0;
                    else if (result > 0.6 && result <= 0.75)
                        additionalScore = 7.0 * additionalScore / 20.0;
                    else if (result > 0.75 && result <= 0.8)
                        additionalScore = 3.0 / 10.0 * additionalScore + 0.02;
                    else
                        additionalScore -= 0.099;
                    matchpart = (result + additionalScore >= 1.0) ? result : result + additionalScore;
                    if (matchpart > 1.0)
                        matchpart = 1.0;
                    //数字不一样
                    if (result == 1.0 && !Objects.equals(x, y))
                        matchpart -= 0.01;

                    if (ngramSimi <= 0.61)
                        ngramSimi += 0.05;
                    else if (ngramSimi > 0.61 && ngramSimi <= 0.77)
                        ngramSimi += 0.03;
                    else if (ngramSimi > 0.77 && ngramSimi + 0.048 < 0.9645)
                        ngramSimi += 0.048;
                    else {
                        if (ngramSimi < 1.0 && ngramSimi > 0.99)
                            ngramSimi = 0.9945;
                        else {
                            if (ngramSimi != 1.0)
                                ngramSimi += 0.008;
                        }

                        if (ngramSimi < 1.0 && ngramSimi > 0.99)
                            ngramSimi = 0.9945;
                    }
                    if (ngramSimi > 0.99 && ngramSimi < 1.0)
                        ngramSimi = 0.99;
                    //标点不一样
                    if (Objects.equals(x, y) && !Objects.equals(s1NormalB, s2NormalB))
                        ngramSimi -= 0.01;
                    //数字不一样
                    if (ngramSimi == 1.0 && !Objects.equals(x, y))
                        ngramSimi -= 0.01;
                    //大小写不一样
                    int diffWordDiffNum = ListEditDistance.editDistance2(FindSubs.trimBlank(FindSubs.turnIntoList(new FindSubs().getAllElements(s1NoNumsNoPunc))), FindSubs.trimBlank(FindSubs.turnIntoList(new FindSubs().getAllElements(s2NoNumsNoPunc))));
                    if (backNG == 1.0 && !Objects.equals(s1NoNumsNoPunc, s2NoNumsNoPunc))
                        ngramSimi -= 0.02 * diffWordDiffNum / 3.0;
                    if (backRe == 1.0 && !Objects.equals(s1NoNumsNoPunc, s2NoNumsNoPunc))
                        matchpart -= 0.02 * diffWordDiffNum / 3.0;
                    //含有\t等不一样
                    if (backNG == 1.0 && !Objects.equals(s1Escape.trim(), s2Escape.trim()))
                        ngramSimi -= 0.01;
                    //含有\t等不一样
                    if (backRe == 1.0 && !Objects.equals(s1Escape.trim(), s2Escape.trim()))
                        matchpart -= 0.01;
                    //      if (editDiff + 0.1 < 1.0 && editDiff-ngramSimi <=0.21 )
                    //        editDiff += 0.1
                    //      else
                    //        editDiff = ngramSimi+0.01
                    //      if(Objects.equals(x,y) && !Objects.equals(s1NormalB,s2NormalB))
                    //        editDiff -=0.01
                    //      if(editDiff ==1.0 && !Objects.equals(x,y))
                    //        editDiff -=0.01
                    double finalMatchRate = 0.0;
                    if (matchpart - ngramSimi > 0.12)
                        matchpart = matchpart / 2.0 + ngramSimi / 2.0 - 0.03;
                    if (result == 1.0 && !Objects.equals(s1.trim(), s2.trim()))
                        result -= 0.01;
                    if (ngramSimi == 1.0 && !Objects.equals(s1.trim(), s2.trim()))
                        ngramSimi -= 0.01;
                    if (numDiff <= 0)
                        finalMatchRate = matchpart * 0.4 + ngramSimi * 0.6;
                    else {
                        if (c1Pure.size() > 5 || c2Pure.size() > 5)
                            finalMatchRate = matchpart * 0.4 + ngramSimi * 0.6 - numDiff * weight;
                        else
                            finalMatchRate = matchpart * 0.4 + ngramSimi * 0.6 - 0.01 * numDiff / 2;
                    }
                    if (finalMatchRate < 0)
                        finalMatchRate = 0.0;
                    return finalMatchRate;
                } else {
                    result = similarity(pureS1, pureS2) / Double.valueOf(100);
                    backRe = result;
                    if (!Objects.equals(pureS1.trim(), pureS2.trim()))
                        ngramSimi = 1.0 - new NGram(2).distance(pureS1, pureS2);
                    else
                        ngramSimi = 1.0;
                    backNG = ngramSimi;
                    if (result <= 0.67)
                        result += 0.03;
                    if (ngramSimi <= 0.7)
                        ngramSimi += 0.03;
                    double wordsDiff = Similaritys.apply(pureS1, pureS2);
                    double backWDiff = wordsDiff;
                    //        editDiff = Similaritys.editDiff(pureS1,pureS2)
                    //        val backEdit = editDiff

                    //数字不一样
                    if (wordsDiff == 1.0 && !Objects.equals(x, y))
                        wordsDiff -= 0.01;
                    //标点不一样
                    if (backWDiff == 1.0 && !Objects.equals(xNoNums, yNoNums))
                        wordsDiff -= 0.01;
                    //大小写不一样
                    int diffWordDiffNum = ListEditDistance.editDistance2(FindSubs.trimBlank(FindSubs.turnIntoList(new FindSubs().getAllElements(s1NoNumsNoPunc))), FindSubs.trimBlank(FindSubs.turnIntoList(new FindSubs().getAllElements(s2NoNumsNoPunc))));
                    if (backWDiff == 1.0 && !Objects.equals(s1NoNumsNoPunc, s2NoNumsNoPunc))
                        wordsDiff -= 0.02 * diffWordDiffNum / 3.0;
                    //其中一个有\t等符号
                    if (backWDiff == 1.0 && !Objects.equals(s1Escape.trim(), s2Escape.trim()))
                        wordsDiff -= 0.01;
                    if (result == 1.0 && !Objects.equals(x, y))
                        result -= 0.01;
                    //有标点不一样
                    if (backRe == 1.0 && !Objects.equals(xNoNums, yNoNums))
                        result -= 0.01;
                    //大小写不一样
                    if (backRe == 1.0 && !Objects.equals(s1NoNumsNoPunc, s2NoNumsNoPunc))
                        result -= 0.02 * diffWordDiffNum / 3.0;
                    //比如\t等不一样
                    if (backRe == 1.0 && !Objects.equals(s1Escape.trim(), s2Escape.trim()))
                        result -= 0.01;
                    if (ngramSimi == 1.0 && !Objects.equals(x, y))
                        ngramSimi -= 0.01;
                    //有标点不一样
                    if (backNG == 1.0 && !Objects.equals(xNoNums, yNoNums))
                        ngramSimi -= 0.01;
                    //大小写不一样
                    if (backNG == 1.0 && !Objects.equals(s1NoNumsNoPunc, s2NoNumsNoPunc))
                        ngramSimi -= 0.02 * diffWordDiffNum / 3.0;
                    //含有\t等不一样
                    if (backNG == 1.0 && !Objects.equals(s1Escape.trim(), s2Escape.trim()))
                        ngramSimi -= 0.01;
                    //        if (editDiff ==1.0 && !Objects.equals(x,y))
                    //          editDiff -= 0.01
                    //        //有标点不一样
                    //        if(backEdit ==1.0 && !Objects.equals(xNoNums,yNoNums))
                    //          editDiff -= 0.01
                    double finalRate = ngramSimi * 0.35 + result * 0.1 + wordsDiff * 0.55;
                    if (finalRate == 1.0 && !Objects.equals(s1.trim().toLowerCase(), s2.trim().toLowerCase())) {
                        finalRate = finalRate * 0.975 + (1.0 - new NGram(2).distance(s1.trim().toLowerCase(), s2.trim().toLowerCase())) * 0.025;
                    }
                    if (finalRate > 1.0)
                        finalRate = 1.0;
                    if (finalRate < 0)
                        finalRate = 0.0;
                    return finalRate;

                }
            }
        }
    }

    private static int similarity(String x, String y) {
        if (!loaded) {
            changeIgnorable();
        }

        int result = -1;
        String s1 = x;
        String s2 = y;
        s1 = RegParse.parseItWithBlank(x.toLowerCase().replaceAll("　+", " ").replaceAll("\\u00A0+", " ").replaceAll(" +", " ").trim());
        s2 = RegParse.parseItWithBlank(y.toLowerCase().replaceAll("　+", " ").replaceAll("\\u00A0+", " ").replaceAll(" +", " ").trim());
        if (Objects.equals(s1, s2)) {
            if (Objects.equals(x.trim(), y.trim())) {
                return 100;
            } else {
                return 100 - PENALTY;
            }
        } else {
            for (int i = 0; i < ignorables.length; i++) {
                s1 = x.toLowerCase().replaceAll("　+", " ").replaceAll("\\u00A0+", " ").replaceAll(" +", " ").trim().replaceAll(ignorables[i], ""); //$NON-NLS-1$
                s2 = y.toLowerCase().replaceAll("　+", " ").replaceAll("\\u00A0+", " ").replaceAll(" +", " ").trim().replaceAll(ignorables[i], ""); //$NON-NLS-1$
            }

            int longest = Math.max(s1.length(), s2.length());
            if (longest == 0) {
                return 0;
            }
            String a = null;
            String b = null;
            if (s1.length() == longest) {
                a = s1;
                b = s2;
            } else {
                a = s2;
                b = s1;
            }
            // a is the longest string
            int count = -1;
            int idx = -1;
            String lcs = LCS(a, b);
            while (!lcs.trim().equals("") && lcs.length() > longest * PENALTY / 100) { //$NON-NLS-1$
                count += 1;
                idx = a.indexOf(lcs);
                a = a.substring(0, idx) + a.substring(idx + lcs.length());
                idx = b.indexOf(lcs);
                b = b.substring(0, idx) + b.substring(idx + lcs.length());
                lcs = LCS(a, b);
            }
            result = (int) (Math.round(100 * (longest - a.length()) / Double.valueOf(longest)) - count * PENALTY);
            if (result < 0) {
                result = 0;
            }
            return result;
        }
    }

    private static String LCS(String x, String y) {
        String result = "";
        int M = x.length();
        int N = y.length();
        int max = 0;
        int mx = 0;
        int[][] opt = new int[M + 1][N + 1];
        for (int i = 1; i <= M; i++) {
            for (int j = 1; j <= N; j++) {
                if (x.charAt(i - 1) == y.charAt(j - 1)) {
                    opt[i][j] = opt[i - 1][j - 1] + 1;
                    if (opt[i][j] > max) { // remember where the maximum length is
                        max = opt[i][j];
                        mx = i;
                    }
                } else {
                    opt[i][j] = 0;
                }
            }
        }
        while (max > 0) {
            result = x.charAt(mx - 1) + result;
            max = max - 1;
            mx = mx - 1;
        }
        return result;
    }

    private static void changeIgnorable() {
        String ignorableChars = null;
        if (ignorableChars == null || "".equals(ignorableChars)) {
            ignorableChars = "\u0640";
            // store.setValue(IPreferenceConstants.IGNORABLE_CHARS, "\\u0640");
        }
        ignorables = ignorableChars.split(",");
        loaded = true;
    }
}
