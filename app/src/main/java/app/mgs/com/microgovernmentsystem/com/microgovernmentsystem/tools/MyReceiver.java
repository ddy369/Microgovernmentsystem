package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.util.EasyUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.LoginActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.MainFaceActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.Dao;
import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2016/9/20.
 */
public class MyReceiver extends BroadcastReceiver {
    static private MainFaceActivity act;
    static public void setAct(MainFaceActivity act1)
    {
        act=act1;
    }
    public void onReceive(final Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction()))
        {
            String response = bundle.getString(JPushInterface.EXTRA_EXTRA);
            try{
               if (response != null) {
                   Log.d("tuisong", response);
                 JSONObject object = new JSONObject(response);
                    int id = object.getInt("category");
                   switch (id)
                   {
                       case 0:UnreadMessageNum.addnoticenum(1);break;
                       case 1:UnreadMessageNum.adddocumentnum(1);break;
                       case 2:UnreadMessageNum.addmagazinenum(1);break;
                       case 3:UnreadMessageNum.addpublicinformationnum(1);break;
                       case 4: UnreadMessageNum.adddepartmentinformationnum(1);break;
                   }
                }
            } catch (JSONException e) {
                e.printStackTrace();

            }
            int notificationId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            UnreadMessageNum.list.add(notificationId);
            if(EasyUtils.isAppRunningForeground(Datahelper.getContext())) {
                NotificationManager manager = (NotificationManager)
                        Datahelper.getContext().getSystemService(Datahelper.getContext().NOTIFICATION_SERVICE);
                manager.cancel(notificationId);
            }
        }
        if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            // Push Talk messages are push down by custom message format
            String response = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            try{
                if (response != null) {
                    Log.d("tuisong", response);
                    JSONObject object = new JSONObject(response);
                    //解绑操作{"action":"login","status":0}
                    // int id = object.getInt("category");
                    String action =object.getString("action");
                    int  status =object.getInt("status");
                    if(action.equals("login")&&status==0)
                    {
                        Dao dao = new Dao(Datahelper.getContext() , null, Values.database_version);
                        SQLiteDatabase loginDaoWritableDatabase = dao.getWritableDatabase();
                        ContentValues cv = new ContentValues();
                        cv.put("lastlogin", "0");
                        String[] arg = {String.valueOf("1")};
                        loginDaoWritableDatabase.update("LOGINTABLE", cv, "lastlogin=?", arg);
                        loginDaoWritableDatabase.close();
                     //   Cursor cursor = readableDatabase.query("LOGINTABLE", new String[]{"lastlogin"}, "lastlogin=?", arg, null, null, null);
                        //删除已下载附件
                        JPushInterface.setAlias(Datahelper.getContext(),"",null);
                        File file=new File(Environment.getExternalStorageDirectory()+"/"+Values.cachepath+"/");
                        deleteAllFiles(file);
                        //清楚数据库缓存
                        SQLiteDatabase db = dao.getWritableDatabase();
                        db.delete("UserTable", null, null);
                        db.delete("NOTICELISTTABLE", null, null);
                        db.delete("RECORDIDTABLE", null, null);
                        db.delete("NOTICETABLE", null, null);
                        db.delete("NoticeAttachmentTable", null, null);
                        db.delete("DocumentTable", null, null);
                        db.delete("DocumentDetail", null, null);
                        db.delete("MagazineTable", null, null);
                        db.delete("PublicInformationListTable", null, null);
                        db.delete("PublicInformationTable", null, null);
                        db.delete("DepartmentInformationListTable", null, null);
                        db.delete("DepartmentInformationTable", null, null);
                        db.delete("RemarkTable", null, null);
                        db.close();
                        EMClient.getInstance().logout(true, new EMCallBack() {

                            @Override
                            public void onSuccess() {
                                // TODO Auto-generated method stub

                            }

                            @Override
                            public void onProgress(int progress, String status) {
                                // TODO Auto-generated method stub

                            }

                            @Override
                            public void onError(int code, String message) {
                                // TODO Auto-generated method stub

                            }
                        });

                        if(EasyUtils.isAppRunningForeground(Datahelper.getContext()))
                        {

                            //对话框
                            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityCollector.getlast());
                            builder.setTitle("警告");
                            //正文
                            builder.setMessage("此设备已被解除绑定！！！。");
                            //不可取消
                            builder.setCancelable(false);
                            //按钮
                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //销毁所有活动
                                    ActivityCollector.finishAll();
                                    //启动登陆活动
                                    Intent intent = new Intent(ActivityCollector.getlast(),LoginActivity.class);
                                    //在广播中启动活动，需要添加如下代码
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intent);
                                }
                            });
                            AlertDialog alterDialog = builder.create();
                            //添加对话框类型：保证在广播中正常弹出
                         //   alterDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                            //对话框展示
                            alterDialog.show();

                        }else {
                            //销毁所有活动
                            ActivityCollector.finishAll();
                         //   act.finish();
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();

            }

        }

    }
    public static boolean isBackground(Context context) {

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
                    Log.i("后台", appProcess.processName);
                    return true;
                }else{
                    Log.i("前台", appProcess.processName);

                    return false;
                }
            }
        }
        return false;
    }
    private void deleteAllFiles(File root) {
        File files[] = root.listFiles();
        if (files != null)
            for (File f : files) {
                if (f.isDirectory()) { // 判断是否为文件夹
                    deleteAllFiles(f);
                    try {
                        f.delete();
                    } catch (Exception e) {
                    }
                } else {
                    if (f.exists()) { // 判断是否存在
                        deleteAllFiles(f);
                        try {
                            f.delete();
                        } catch (Exception e) {
                        }
                    }
                }
            }
    }

}
