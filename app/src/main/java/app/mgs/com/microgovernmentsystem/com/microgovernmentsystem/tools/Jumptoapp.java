package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

/**
 * Created by Administrator on 2016/8/9.
 */
public class Jumptoapp {
    static public void jump(Context context,String app)
    {
        try {
            PackageManager packageManager = context.getPackageManager();
            Intent intent=new Intent();
            intent = packageManager.getLaunchIntentForPackage(app);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Intent i = GetUri.getIntent(context,app);
            boolean b = GetUri.judge(context, i);
            if(b==false)
            {
                context.startActivity(i);
            }

        }
    }





}
