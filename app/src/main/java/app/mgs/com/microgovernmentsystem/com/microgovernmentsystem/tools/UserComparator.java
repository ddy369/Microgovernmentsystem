package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools;

import java.util.Comparator;

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.Contacts;

/**
 * Created by Administrator on 2016/8/5.
 */
public class UserComparator implements Comparator {
    public int compare(Object u1, Object u2){
        int flag;
        Contacts a=(Contacts)u1;
        Contacts b=(Contacts)u2;
        ChineseToSpell chineseToSpell = new ChineseToSpell();
        flag = chineseToSpell.getStringPinYin(a.name).compareTo(chineseToSpell.getStringPinYin(b.name));
        return flag;
    }
}
