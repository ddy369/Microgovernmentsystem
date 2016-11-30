package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools;

import android.provider.Settings;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.GetUserInformationDao;

/**
 * Created by mqs on 2016/8/5.
 */
public class GetSignTool {

    Md5Tool md5Tool = new Md5Tool();

    public String getSign(HashMap<String , String> map){

        List<Map.Entry<String , String>> infoIds = new ArrayList<>(map.entrySet());

        //排序
        Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {
            @Override
            public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                return (o1.getKey()).toString().compareTo(o2.getKey());
            }
        });

        //得到源字符串
        String sign = "govoa";
        for (int i = 0; i < infoIds.size(); i++){
            sign += infoIds.get(i).getKey() + infoIds.get(i).getValue();
        }
        sign += "govoa";

        Log.i("sign",sign);
        return md5Tool.getMDSStr(sign);

//        GetUserInformationDao getUser = new GetUserInformationDao();
//
//        Log.i("sign",getUser.getMac()+" "+getUser.getPassword()+" "+getUser.getTimeStamp()+" "+getUser.getAccount());
//        return md5Tool.getMDSStr("govoa"
//                +"mac"+getUser.getMac()
//                +"password"+getUser.getPassword()
//                +"timestamp"+getUser.getTimeStamp()
//                +"username"+getUser.getAccount()
//                +"govoa");
    }
}
