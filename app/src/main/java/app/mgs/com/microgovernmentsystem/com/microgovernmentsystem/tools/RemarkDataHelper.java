package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.ContactsInformationActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.RemarksActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.Dao;

public class RemarkDataHelper {

    /**
     * 从数据库中取出flag对应的备注
     * @param flag=姓名+部门+职位的md5加密
     * @return 备注信息
     */
    public String selectRemark(String flag){
        Dao dao = new Dao(ContactsInformationActivity.activity,null,Values.database_version);

        String remark = "";

        SQLiteDatabase dbRead = dao.getReadableDatabase();

        Cursor cursor = dbRead.query("RemarkTable",null,"flag=?",new String[]{flag},null,null,null);
        if (cursor.moveToNext()){
            remark = cursor.getString(cursor.getColumnIndex("remark"));
        }
        cursor.close();
        return remark;
    }

    public int selectExis(String flag){
        Dao dao = new Dao(ContactsInformationActivity.activity,null,Values.database_version);
        SQLiteDatabase dbRead = dao.getReadableDatabase();
        Cursor cursor = dbRead.query("RemarkTable",null,"flag=?",new String[]{flag},null,null,null);
        int i = cursor.getCount();
        cursor.close();
        return i;
    }

    public void insertRemark(String flag,String remark){
        Dao dao = new Dao(RemarksActivity.activity,null,Values.database_version);
        SQLiteDatabase dbWrite = dao.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("remark",remark);
        values.put("flag",flag);
        dbWrite.insert("RemarkTable",null,values);
    }

    public void updateRemark(String remark,String flag){
        Dao dao = new Dao(RemarksActivity.activity,null,Values.database_version);

        SQLiteDatabase dbWrite = dao.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("remark",remark);
        dbWrite.update("RemarkTable",values,"flag=?",new String[]{ flag });
    }
}
