package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools;

import android.util.Log;

import java.util.Calendar;


public class CompareTime {

    /**
     * 此类主要用于比较当前时间和设置提醒的时间，判断提醒时间是否有效，若无效则无法不能设置提醒
     *
     * @param y  年
     * @param mo 月
     * @param d  日
     * @param h  小时
     * @param mi 分钟
     * @return 返回1则表示设置时间有效，返回0则表示设置提醒无效
     */
    public int compareTime(int y, int mo, int d, int h, int mi) {
        int i = 0;//i为返回值，如果提醒有效，则返回1，否则返回0
        java.util.Calendar c = java.util.Calendar.getInstance();
        long systemTime = System.currentTimeMillis();//截取当前时间（以毫秒计
        c.set(Calendar.MINUTE, mi);
        c.set(Calendar.HOUR_OF_DAY, h);
        c.set(Calendar.DAY_OF_MONTH, d);
        c.set(Calendar.MONTH, mo-1);
        c.set(Calendar.YEAR, y);
        long selectTime = c.getTimeInMillis();//获取设定时间
        long time = selectTime - systemTime;
        Log.i("time time","aaa"+time);
        if (time > 0) {
            i = 1;
        }
        return i;
    }
}
