package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools;

import java.util.Comparator;

/**
 * Created by Administrator on 2016/8/4.
 */
public class PinYinComparator implements Comparator<String> {
    public int compare(String str1,String str2){
        int flag;
        ChineseToSpell chineseToSpell = new ChineseToSpell();
        flag = chineseToSpell.getStringPinYin(str1).compareTo(chineseToSpell.getStringPinYin(str2));
        return flag;
    }
}
