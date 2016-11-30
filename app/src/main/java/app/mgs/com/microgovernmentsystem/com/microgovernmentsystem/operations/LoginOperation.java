package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.operations;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.LoginActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.MainFaceActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.Dao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tasks.LoginQueryWorkerTask;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.GetSignTool;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Md5Tool;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Values;

public class LoginOperation {

    private String account;
    private String password;
    private Context context;
    Md5Tool md5Tool = new Md5Tool();

    Dao dao = new Dao(LoginActivity.activity,null, Values.database_version);

    public LoginOperation(String acc, String pw, Context context){
        this.account = acc;
        this.password = pw;
        this.context = context;
    }

    public boolean JudgeNull(){//如果账号密码均已填入则开始登录事件
        return !(account.isEmpty() || password.isEmpty());
    }

    public void Login(final LinearLayout layout){

        final boolean flag = JudgeNull();

        if (flag){
            //在验证登录之前将数据库中的所有上次登录全置为0
            final SQLiteDatabase updateLastLog = dao.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("lastlogin", "0");
            updateLastLog.update("LOGINTABLE", contentValues, null, null);

            new LoginQueryWorkerTask(new LoginQueryWorkerTask.AsyncWork(){
                Toast toast;

                @Override
                public void preExcute() {
                    toast = Toast.makeText(context,"正在寻找网络",Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    layout.setVisibility(View.VISIBLE);
                }

                @Override
                public void postWork(Integer integer) {
                    Log.i("integer","integer = "+integer);
                    if (integer==0){
                        //点击跳转页面
                        toast.cancel();
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
                        Intent intent = new Intent();
                        intent.setClass(context, MainFaceActivity.class);
                        context.startActivity(intent);
                        LoginActivity.activity.finish();
                    }else if (integer == 1){
                        toast.cancel();
                        Toast.makeText(context, "密码不正确", Toast.LENGTH_SHORT).show();
                        layout.setVisibility(View.GONE);
                    }else if (integer == 2){
                        toast.cancel();
                        Toast.makeText(context, "已绑定其他手机，请先解绑", Toast.LENGTH_SHORT).show();
                        layout.setVisibility(View.GONE);
                    }else if (integer == 3){
                        toast.cancel();
                        Toast.makeText(context, "已成功提交审核，请联系管理员审核后方可使用", Toast.LENGTH_SHORT).show();
                        layout.setVisibility(View.GONE);
                    }else if (integer == 4){
                        toast.cancel();
                        Toast.makeText(context, "等待管理员审核通过后即可使用...", Toast.LENGTH_SHORT).show();
                        layout.setVisibility(View.GONE);
                    } else if (integer == -1){
                        toast.cancel();
                        Toast.makeText(context, "网络错误，请检查是否联网", Toast.LENGTH_SHORT).show();
                        layout.setVisibility(View.GONE);
                    }else {
                        toast.cancel();
                        Toast.makeText(context, "未知错误", Toast.LENGTH_SHORT).show();
                        layout.setVisibility(View.GONE);
                    }
                }
            }).execute(account,getPassword(),getTimeStamp(),getMac(),getSign());
        }else {
            Toast.makeText(context,"请确认已经输入账号和密码",Toast.LENGTH_SHORT).show();
        }
    }

    private String getTimeStamp(){
        //获取时间戳
        SimpleDateFormat formatter  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINESE);
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        return formatter.format(curDate);
    }

    private String getPassword(){
        return md5Tool.getMDSStr(password);
    }

    private String getMac(){
        //Log.i("mac",Settings.Secure.getString(context.getContentResolver() , Settings.Secure.ANDROID_ID));
        return Settings.Secure.getString(context.getContentResolver() , Settings.Secure.ANDROID_ID);
    }

    private String getSign(){
        HashMap<String , String> map = new HashMap<>();
        map.put("mac",getMac());
        map.put("password",getPassword());
        map.put("timestamp",getTimeStamp());
        map.put("username",account);
        GetSignTool getSignTool = new GetSignTool();
        return getSignTool.getSign(map);
        //获取签名值
//        return md5Tool.getMDSStr("govoa"+"mac"+getMac()+"password"+getPassword()+"timestamp"+getTimeStamp()
//                +"username"+account+"govoa");
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
