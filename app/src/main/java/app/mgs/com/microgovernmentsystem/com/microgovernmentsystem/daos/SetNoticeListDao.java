package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.NoticeListActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Values;

public class SetNoticeListDao {

    Dao dao = new Dao(NoticeListActivity.activity, null, Values.database_version);

    public void setNoticeList(String recordId, String department, String sendTime, String title,String isRead){
        //先查看数据库中是否存在该条数据,如果存在则不做任何操作，如果不存在则插入该条数据
        SQLiteDatabase dbRead = dao.getReadableDatabase();
        Cursor cursor = dbRead.query("NOTICELISTTABLE", null,"recordid=?", new String[]{ recordId }, null ,null,"sendtime desc");
        int i = cursor.getCount();
        Log.i("i:"," "+i);
        if (i == 0){
            SQLiteDatabase dbInsert = dao.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("department", department);
            values.put("sendtime", sendTime);
            values.put("title", title);
            values.put("recordid", recordId);
            if (isRead.equals("true")){
                values.put("read",1);
            }else {
                values.put("read",0);
            }
            dbInsert.insert("NOTICELISTTABLE", null, values);
            dbInsert.close();
        }
        cursor.close();
    }
}
