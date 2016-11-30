package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools;

/**
 * Created by Administrator on 2016/9/21.
 */
public class Passwordcheck {
    public static boolean check(String s)
    {
        if(s==null)
            return false;
        if(s.length()>6)
            return true;
        if(!isNumeric(s))
        {
            return true;
        }
        return false;
    }
    public static boolean isNumeric(String str){
        for (int i = str.length();--i>=0;){
            if (!Character.isDigit(str.charAt(i))){
                return false;
            }
        }
        return true;}
}
