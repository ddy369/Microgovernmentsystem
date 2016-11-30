package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools;


import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.PublishNoticeActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.Dao;

public class PublishChoiceDataHelper {

    Dao dao = new Dao(PublishNoticeActivity.activity,null, Values.database_version);
    SQLiteDatabase dbRead = dao.getReadableDatabase();

    /**
     * 查询已经被选中的部门返回给textview赋值
     * @return departmentName
     */
    public String ReadDepartment(){
        String departmentName = null;
        Cursor cursor = dbRead.query("DepartmentTable", new String[]{ "departmentName" },
                "isChoice=?",new String[]{"1"},
                null,null,null);
        if (cursor.moveToFirst()){
            for (int i = 1; i <= cursor.getCount(); i++){
                if (i==1){
                    departmentName = cursor.getString(cursor.getColumnIndex("departmentName"));
                }else {
                    departmentName = departmentName + ","+ cursor.getString(cursor.getColumnIndex("departmentName"));
                }
                cursor.moveToNext();
            }
        }
        cursor.close();
        return departmentName;
    }

    /**
     * 查询已经被选中的通知人返回给textview赋值
     * @return departmentName
     */
    public String ReadReceiver(){
        String ReceiverName = null;
        Cursor cursor = dbRead.query(true,"ReceiverTable", new String[]{ "receiverName" },
                "isChoice=?",new String[]{"1"},
                null,null,null,null);
        if (cursor.moveToFirst()){
            for (int i = 1; i <= cursor.getCount(); i++){
                if (i==1){
                    ReceiverName = cursor.getString(cursor.getColumnIndex("receiverName"));
                }else {
                    ReceiverName = ReceiverName + ","+ cursor.getString(cursor.getColumnIndex("receiverName"));
                }
                cursor.moveToNext();
            }
        }
        cursor.close();
        return ReceiverName;
    }

}
