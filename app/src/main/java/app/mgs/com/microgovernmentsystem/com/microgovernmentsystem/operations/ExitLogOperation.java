package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.operations;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.LoginActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.MainFaceActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.Dao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Values;


public class ExitLogOperation {

    public void exitLog(Context context){
        Dao dao = new Dao(MainFaceActivity.activity , null, Values.database_version);
        SQLiteDatabase loginDaoWritableDatabase = dao.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("lastlogin", "0");
        String[] arg = {String.valueOf("1")};
        loginDaoWritableDatabase.update("LOGINTABLE", cv, "lastlogin=?", arg);
        Intent intent = new Intent();
        intent.setClass(context, LoginActivity.class);
        context.startActivity(intent);
        MainFaceActivity.activity.finish();



    }
}
