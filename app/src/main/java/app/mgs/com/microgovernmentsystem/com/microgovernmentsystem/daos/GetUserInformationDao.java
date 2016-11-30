package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.LoginActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Values;

/**
 * 此类用来连接收据库设置页面上显示的个人信息
 */
public class GetUserInformationDao {

    Dao dao = new Dao(LoginActivity.activity,null, Values.database_version);

    public String getUserName(){
        SQLiteDatabase readName = dao.getReadableDatabase();
        Cursor cursor = readName.query("LOGINTABLE", new String[]{ "name" }, "lastlogin=?", new String[]{ "1" } , null, null, null);
        String name = "";
        if (cursor.moveToNext()){
            name =  cursor.getString(0);
        }
        cursor.close();
        readName.close();
        return name;
    }

    public String getPosition(){
        SQLiteDatabase readPosition = dao.getReadableDatabase();
        Cursor cursor = readPosition.query("LOGINTABLE", new String[]{ "position" }, "lastlogin=?", new String[]{ "1" } , null, null, null);
        String position = "";
        if (cursor.moveToNext()){
            position =  cursor.getString(0);
        }
        cursor.close();
        readPosition.close();
        return position;
    }

    public String getAccount(){
        SQLiteDatabase readAccount = dao.getReadableDatabase();
        Cursor cursor = readAccount.query("LOGINTABLE", new String[]{ "account" }, "lastlogin=?", new String[]{ "1" } , null, null, null);
        String account = "";
        if (cursor.moveToNext()){
            account =  cursor.getString(0);
        }
        cursor.close();
        readAccount.close();
        return account;
    }

    public String getPassword(){
        SQLiteDatabase readPassword = dao.getReadableDatabase();
        Cursor cursor = readPassword.query("LOGINTABLE", new String[]{ "password" }, "lastlogin=?", new String[]{ "1" } , null, null, null);
        String password = "";
        if (cursor.moveToNext()){
            password =  cursor.getString(0);
        }
        cursor.close();
        readPassword.close();
        return password;
    }

    public String getMac(){
        SQLiteDatabase readMac = dao.getReadableDatabase();
        Cursor cursor = readMac.query("LOGINTABLE", new String[]{ "mac" }, "lastlogin=?", new String[]{ "1" } , null, null, null);
        String mac = "";
        if (cursor.moveToNext()){
            mac =  cursor.getString(0);
        }
        cursor.close();
        readMac.close();
        return mac;
    }

    public String getUid(){
        SQLiteDatabase readUid = dao.getReadableDatabase();
        Cursor cursor = readUid.query("LOGINTABLE", new String[]{ "uid" }, "lastlogin=?", new String[]{ "1" } , null, null, null);
        String uid = "";
        if (cursor.moveToNext()){
            uid =  cursor.getString(0);
        }
        cursor.close();
        readUid.close();
        return uid;
    }

    public String getTimeStamp(){
        //获取时间戳
        SimpleDateFormat formatter  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE);
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        return formatter.format(curDate);
    }
}
