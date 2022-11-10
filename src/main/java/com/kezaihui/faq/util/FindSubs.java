package com.kezaihui.faq.util;


import java.math.BigDecimal;
import java.util.*;

/**
 * Created by C00762 on 2018/4/23.
 */
public class FindSubs {
    private List<String> container = new ArrayList<String>();

    public int leastWords(int size, BigDecimal percentage) {
        BigDecimal difference = new BigDecimal(1.0).subtract(percentage),
                delCount = difference.multiply(new BigDecimal(size));
        return size - delCount.intValue();
    }


    //获取指定匹配率下所有原顺序组合
    public List<String> getSubsets(String source, BigDecimal percentage) {
        //将source转换为数组
        String[] list = getAllElements(source);
        int size = list.length, len = leastWords(size, percentage);
        for (int i = size; i >= len; --i) {
            genCom(source, i, container);
        }
        return container;
    }

    public static void genCom(String srcStr, int n, List<String> result) {
        //循环标记
        int loop = 0;
        String[] str_list = new FindSubs().getAllElements(srcStr);

        //选号位
        int[] pos = new int[n];

        //选不出来
        if (str_list.length < n || str_list.length <= 0 || n <= 0)
            return;


        //初始化前n是选号位
        for (int i = 0; i < n; i++)
            pos[i] = i;


        //循环处理
        while (true) {
            //循环第10001次就退出
            if (loop > 10000)
                break;
            //1.生成选择数据
            StringBuffer buff = new StringBuffer();
            for (int i = 0; i < n; i++)
                buff.append(str_list[pos[i]]);

            result.add(buff.toString());

            //2.进位
            //从选号位最右边开始，选择第一个可以右移的位置进行进位
            boolean is_move = false;

            for (int i = n - 1; i >= 0; i--) {
                //可以进位
                if (pos[i] < str_list.length - n + i) {
                    pos[i]++;   //选位右移
                    //所有右边的选号全部归位
                    for (int k = i + 1; k < n; ++k)
                        pos[k] = pos[i] + k - i;
                    is_move = true;
                    break;
                }
            }
            if (!is_move)   //没有成功移位,到头了
                break;
            ++loop;
        }
    }
    /**
     * @methodName:getAllElements
     * @description:将字符串转换为数组
     * @param str String
     * @return String[]
     */
    public String[] getAllElements(String str){
        List<String> result=new ArrayList<>();
        String temp;
        StringBuffer sb=new StringBuffer();
        for(int i=0,size=str.length();i<size;i++){
            temp=str.substring(i,i+1);
            //元素不为空格
            if(!temp.equals(" ") && !temp.equals("\u00A0") && !temp.equals("　")){
                //元素是非汉字（汉字以字为单位 其他语言认为以字母组成单词为单位）且是字母或阿拉伯数字（20190712去掉）!temp.matches("[0-9]")条件
//                if(!temp.matches("^[\\u4e00-\\u9fa5]*")&&!temp.matches(".*[\\pP‘’“”]")&&!temp.matches(".*[\\p{Punct}]")&&!temp.matches("[0-9]"))
                if(!temp.matches("^[\\u4e00-\\u9fa5]*")&&!temp.matches(".*[\\pP‘’“”￥¨ˉ∩∪∈∵∴∞∝≈≠＝≤≥＜＞≮≯∷⊥∥∠⌒⊙≌∽√∏∑∨∧±＋－×÷／∫∮]")&&!temp.matches(".*[\\p{Punct}]")&&!temp.matches("[0-9]")){
                    if (sb.length()>0) {
                        if(sb.toString().matches("(-?\\d+(\\,\\d+)*(\\.)+(\\d+)*/-?\\d+(\\,\\d+)*(\\.)+(\\d+)*|-?\\d+(\\,\\d+)*(\\.)+(\\d+)*)")|| sb.toString().matches("(-?\\d+(\\,\\d+)*(\\.\\d+)*/-?\\d+(\\,\\d+)*(\\.\\d+)*|-?\\d+(\\,\\d+)*(\\.\\d+)*)")) {
                            result.add(sb.toString());
                            sb.setLength(0);
                        }
                    }
                    sb.append(temp);
                }else{
                    //若当前字符为标点'，将它加入sb中，其他不加入
                    if(temp.matches(".*[\\pP‘’“”]")&&temp.matches(".*[\\p{Punct}]")) {
                        if(Objects.equals(temp,"'") ||  Objects.equals(".",temp)) {
                            if (Objects.equals("'",temp)){
                                if(!sb.toString().matches("(-?\\d+(\\,\\d+)*(\\.)+(\\d+)*/-?\\d+(\\,\\d+)*(\\.)+(\\d+)*|-?\\d+(\\,\\d+)*(\\.)+(\\d+)*)") && !sb.toString().matches("(-?\\d+(\\,\\d+)*(\\.\\d+)*/-?\\d+(\\,\\d+)*(\\.\\d+)*|-?\\d+(\\,\\d+)*(\\.\\d+)*)") ) {
                                    sb.append(temp);
                                }else {
                                    if(sb.length()!=0){
                                        result.add(sb.toString());
                                        sb.setLength(0);
                                    }
                                    result.add(temp);
                                }

                            }else if (Objects.equals(".",temp)) {
                                //若当前temp是逗点“.”，temp之前的sb是数字且其之后还有数字
                                if ((i+1 <= size-1) && str.substring(i+1,i+2).matches("\\d") && (sb.toString().matches("(-?\\d+(\\,\\d+)*(\\.)+(\\d+)*/-?\\d+(\\,\\d+)*(\\.)+(\\d+)*|-?\\d+(\\,\\d+)*(\\.)+(\\d+)*)")|| sb.toString().matches("(-?\\d+(\\,\\d+)*(\\.\\d+)*/-?\\d+(\\,\\d+)*(\\.\\d+)*|-?\\d+(\\,\\d+)*(\\.\\d+)*)"))) {
                                    sb.append(temp);
                                }else {
                                    if(sb.length()!=0){
                                        result.add(sb.toString());
                                        sb.setLength(0);
                                    }
                                    result.add(temp);
                                }
                            }

                        } else {
                            if(sb.length()!=0){
                                result.add(sb.toString());
                                sb.setLength(0);
                            }
                            result.add(temp);
                        }

                    }else if (temp.matches("\\d")){
                        if(sb.toString().matches("(-?\\d+(\\,\\d+)*(\\.)+(\\d+)*/-?\\d+(\\,\\d+)*(\\.)+(\\d+)*|-?\\d+(\\,\\d+)*(\\.)+(\\d+)*)") || sb.toString().matches("(-?\\d+(\\,\\d+)*(\\.\\d+)*/-?\\d+(\\,\\d+)*(\\.\\d+)*|-?\\d+(\\,\\d+)*(\\.\\d+)*)")){
                            sb.append(temp);
                        } else {
                            if(sb.length()!=0){
                                result.add(sb.toString());
                                sb.setLength(0);
                            }
                            sb.append(temp);
                        }

                    }else {
                        if(sb.length()!=0){
                            result.add(sb.toString());
                            sb.setLength(0);
                        }
                        //将temp加入result
                        result.add(temp);
                    }

                }
                //若最后一个元素并且当前sb元素不为“”
                if(i==size-1&&!Objects.equals(sb.toString(),""))
                    result.add(sb.toString());
            }else{
                //temp为空格若当前sb元素不为“” 将其加入result
                //若当前sb元素不为“” 将其加入result
                if(sb!=null&& !Objects.equals(sb.toString(),""))
                    result.add(sb.toString());
                sb.setLength(0);
                result.add(temp);
            }
        }
        String[] re=new String[result.size()];
        return result.toArray(re);
    }
    /**
     * @methodName: trimBlank
     * @Description: 去掉集合中的空格元素
     * @author:C00762
     * @date: 2018/5/19
     * @param: list
     * @return: List<String>
     */

    public static List<String> trimBlank(List<String> list) {
        while (list.contains(" ")) {
            list.remove(" ");
        }
        return list;
    }

    /**
     * @methodName: matchPercent
     * @Description: 获得原与目标的匹配率
     * @author:C00762
     * @date: 2018/5/19
     * @param: src trt samePart
     * @return: matchPercent double
     */

    public static double matchPercent(List<String> src, List<String> trt) {
        List<String> longer = trimBlank(src),
                shorter = trimBlank(trt);
        if (longer.size() < shorter.size()) {
            longer = trimBlank(trt);
            shorter = trimBlank(src);
        }
        return (double) (longer.size() - ListEditDistance.editDistance(longer, shorter)) / longer.size();

    }

    public static List<String> turnIntoList(String[] arr){
        List<String> result=new ArrayList<>(Arrays.asList(arr));
        return  result;
    }
    /**
     * @methodName: myListContains
     * @Description： 比较两个String list包含关系
     * @author:C00762
     * @date: 2018/5/22
     * @param: a, b
     * @return: boolean
     */

    public static boolean myListContains(List<String> a, List<String> b) {
        List<String> longerOne = a, shorterOne = b;
        if (a.size() < b.size()) {
            longerOne = b;
            shorterOne = a;
        }
        return myToString(longerOne).toLowerCase().contains(myToString(shorterOne).toLowerCase());
    }

    /**
     * @methodName: myToString
     * @Description： 自定义List转为String规则 将所有内容拼接输出
     * @author:C00762
     * @date: 2018/5/22
     * @param: src
     * @return: String
     */
    public static String myToString(List<String> src) {
        if (src.size() <= 0)
            return "";
        StringBuilder sb = new StringBuilder();
        for (String s : src) {
            sb.append(s);
        }
        return sb.toString();
    }

    /**
     * @methodName: myListFrequency
     * @Description: 找单个字符串在一个集合的每个字符串中的出现次数
     * @author:C00762
     * @date: 2018/5/23
     * @param: src 源串集合 key 基准单个字符串
     * @return: int result
     */
    public static int myListFrequency(List<String> l, String key) {
        int result = 0;
        if (key == null) {
            for (Object e : l)
                if (e == null)
                    result++;
        } else {
            for (String e : l)
                result = result + myStringFrequency(e, key);
        }
        return result;
    }

    /**
     * @methodName: myStringFrequency
     * @Description: 找单个字符串在一个字符串中的出现次数
     * @author:C00762
     * @date: 2018/5/23
     * @param: src 源串 key 基准单个字符串
     * @return: int result
     */

    public static int myStringFrequency(String src, String key) {
        int result = 0;
        if (src == null || key == null)
            return result;
        else {
            for (int i = 0, size = src.length(); i < size; i++) {
                if (Objects.equals(String.valueOf(src.charAt(i)), key))
                    result++;
            }
        }
        return result;
    }
}
