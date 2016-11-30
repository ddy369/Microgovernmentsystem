package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;

/**
 * Created by Administrator on 2016/8/6.
 */
public class checkNetworkState {
    static public int checkNetworkState() {
        boolean flag = false;
        int i=0;
        //得到网络连接信息
        ConnectivityManager manager;
        manager = (ConnectivityManager)Datahelper.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        //去进行判断网络是否连接
        if (manager.getActiveNetworkInfo() != null) {
            flag = manager.getActiveNetworkInfo().isAvailable();
        }
        if(flag) {
            State gprs = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
            State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
            if (gprs == State.CONNECTED || gprs == State.CONNECTING) {
                i=1;
            }
            //判断为wifi状态下才加载广告，如果是GPRS手机网络则不加载！
            if (wifi == State.CONNECTED || wifi == State.CONNECTING) {

                i=2;
            }
        }else
        {
            i=0;
        }
        return i;
    }
}
