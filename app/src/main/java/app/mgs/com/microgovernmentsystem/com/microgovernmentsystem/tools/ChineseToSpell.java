package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * Created by Administrator on 2016/8/4.
 */
public class ChineseToSpell {

    private HanyuPinyinOutputFormat format = null;
    private String[] pinyin;
    public  String[] b1 = { "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z" };
    public ChineseToSpell() {
        format = new HanyuPinyinOutputFormat();
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        pinyin = null;
    }

    /**
     * 转换单个字符
     * @param c
     * @return
     */
    public String getCharacterPinYin(char c) {
        try {
            pinyin = PinyinHelper.toHanyuPinyinStringArray(c, format);
        }catch(BadHanyuPinyinOutputFormatCombination e){
            e.printStackTrace();
        }
        // 如果c不是汉字，toHanyuPinyinStringArray会返回null
        if(pinyin == null) return null;
        // 只取一个发音，如果是多音字，仅取第一个发音
        return pinyin[0];
    }
    /**
     * 转换一个字符串
     * @param str
     * @return
     */
    public String getStringPinYin(String str) {
        StringBuilder sb = new StringBuilder();
        String tempPinyin = null;
        for(int i = 0; i < str.length(); ++i) {
            tempPinyin =getCharacterPinYin(str.charAt(i));
            if(tempPinyin == null) {
                // 如果str.charAt(i)非汉字，则保持原样
                sb.append(str.charAt(i));
            }else {
                sb.append(tempPinyin);
            }
        }
        return sb.toString();
    }

    /**
     * 提取每个汉字的首字母
     * @param str
     * @return String
     */
    public String getPinYinHeadChar(String str) {
        String convert = "";
        for (int j = 0; j < str.length(); j++) {
            char word = str.charAt(j);
            // 提取汉字的首字母
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
            if (pinyinArray != null) {
                convert += String.valueOf(pinyinArray[0].charAt(0)).toUpperCase();
            } else {
                convert += String.valueOf(word).toUpperCase();
            }
        }
        return convert;
    }
    /**
     * 将字符串转换成ASCII码
     * @param cnStr
     * @return String
     */
    public int  getPinYinSCII(String cnStr) {
        char[] chars = cnStr.toCharArray();
        int i = (int) chars[0];
        //Log.i("aaa","aaa:"+i);
        return i;
    }
    public int getMul(int i,int j){
        return i-j;
    }
}
