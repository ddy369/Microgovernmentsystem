package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by dy on 2016-09-27.
 */
public class StringTransformation {

    public String Transformation(String str) throws ParseException {
        SimpleDateFormat strEnd = new SimpleDateFormat("yyyy.MM.dd");
        SimpleDateFormat strStart = new SimpleDateFormat("yyyy.M.d");
        return strEnd.format(strStart.parse(str));
    }

}
