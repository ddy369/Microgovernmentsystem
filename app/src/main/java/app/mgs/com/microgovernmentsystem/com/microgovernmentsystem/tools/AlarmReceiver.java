package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import app.mgs.com.microgovernmentsystem.R;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.MainFaceActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.MemorandumActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.SetMemorandumDao;


public class AlarmReceiver extends BroadcastReceiver {

    SetMemorandumDao dao = new SetMemorandumDao();

    @Override
    public void onReceive(Context context, Intent intent) {
        //设置通知内容并在onReceive开始时开启提醒
        //设置通知栏提示
        setNotification(context);
    }

    /**
     * 在通知栏中显示
     */
    private void setNotification(Context context){
        //将数据库中最新插入的id来当做notifyId的id
        int notifyId = getNotifyID();
        //用此id查询出相应的标题
        String title = dao.selectTitle(notifyId);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentTitle("高新微政务提醒")
                .setContentText(title)
                .setContentIntent(getDefaultIntent(Notification.FLAG_INSISTENT,context,notifyId)) //设置通知栏点击意图
                .setTicker("高新微政务提醒")//通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
                .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与
                // (如播放音乐)或以某种方式正在等待,
                // 因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使
                // 用当前的用户默认设置，使用defaults属性，可以组合
                .setSmallIcon(R.drawable.ic_launcher);//设置通知小ICON
        manager.notify(notifyId,builder.build());
    }

    private PendingIntent getDefaultIntent(int flags,Context context,int notifyId){
        SetMemorandumDao dao = new SetMemorandumDao();
        dao.cancelNotice(notifyId);
        Intent intent = new Intent(context,MemorandumActivity.class);
        PendingIntent pendingIntent= PendingIntent.getActivity(context, notifyId, intent, flags);
        return pendingIntent;
    }

    public int getNotifyID(){
        //查询插入到数据库的最新id，设置为notifyId
        return dao.selectMemorandum();
    }
}
