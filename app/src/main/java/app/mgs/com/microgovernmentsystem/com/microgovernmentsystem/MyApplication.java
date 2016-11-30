package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem;

import android.app.Application;

import com.hyphenate.easeui.controller.EaseUI;

import org.xutils.x;

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.im.imhelper;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Datahelper;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.UnreadMessageNum;
import cn.jpush.android.api.JPushInterface;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        imhelper im=new imhelper();

        EaseUI.getInstance().init(this, null);
        im.initim(this);
        Datahelper.setContext(this);
        UnreadMessageNum.resetall();
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        x.Ext.init(this);
        x.Ext.setDebug(true);

    }


}
