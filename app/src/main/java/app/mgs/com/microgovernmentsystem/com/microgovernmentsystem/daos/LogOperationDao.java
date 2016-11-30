package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.LoginActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Values;


public class LogOperationDao {

    Dao dao = new Dao(LoginActivity.activity,null, Values.database_version);

    public void daoOperation(String account , String password , String name ,
                             String position , String mac ,String uid){
        //通过验证
        //查是否是已经存在的账号，若已存在则更新密码，否则记录该账号密码到数据库,并设置此账号为最近一次登录
        SQLiteDatabase readAccount = dao.getReadableDatabase();
        Cursor cursor = readAccount.query("LOGINTABLE", null, "account=?", new String[]{account}, null, null, null);
        if (cursor.moveToNext()){
            SQLiteDatabase updatePassword = dao.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("password", password);
            values.put("lastlogin", "1");
            String[] args = {
                    String.valueOf(account)
            };
            updatePassword.update("LOGINTABLE", values, "account=?", args);
            cursor.close();
            updatePassword.close();
        }else {
            SQLiteDatabase insertUser = dao.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("account", account);
            values.put("password", password);
            values.put("lastlogin", "1");
            insertUser.insert("LOGINTABLE", null, values);
            insertUser.close();
        }
        //在本地存储之后将用户的信息存入数据库
        setUserName(account , name);
        setUserPosition(account , position);
        setUserMac(account , mac);
        setUserId(account , uid);
    }

    private void setUserName(String account , String str){
        Log.i("name:",str);
        SQLiteDatabase updatePassword = dao.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", str);
        String[] args = {
                String.valueOf(account)
        };
        updatePassword.update("LOGINTABLE", values, "account=?", args);
        updatePassword.close();
    }

    private void setUserPosition(String account ,String str){
        Log.i("position:",str);
        SQLiteDatabase updatePassword = dao.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("position", str);
        String[] args = {
                String.valueOf(account)
        };
        updatePassword.update("LOGINTABLE", values, "account=?", args);
        updatePassword.close();
    }

    private void setUserMac(String account , String str){
        Log.i("mac:",str);
        SQLiteDatabase updateMac = dao.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("mac", str);
        String[] args = {
                String.valueOf(account)
        };
        updateMac.update("LOGINTABLE", values, "account=?", args);
        updateMac.close();
    }

    private void setUserId(String account , String str){
        Log.i("uid:",str);
        SQLiteDatabase updateMac = dao.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("uid", str);
        String[] args = {
                String.valueOf(account)
        };
        updateMac.update("LOGINTABLE", values, "account=?", args);
        updateMac.close();
    }
}
