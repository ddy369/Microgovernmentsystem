package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.NoticeListActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.PublishedNoticeListActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Values;

/**
 * Created by mqs on 2016/10/10.
 */
public class SetPublishedNoticeListDao {
    Dao dao = new Dao(PublishedNoticeListActivity.activity, null, Values.database_version);

    public void setPublishedNoticeList(String recordId, String department, String sendTime, String title){
        //先查看数据库中是否存在该条数据,如果存在则不做任何操作，如果不存在则插入该条数据
        SQLiteDatabase dbRead = dao.getReadableDatabase();
        Cursor cursor = dbRead.query("PublishedNoticeListTable", null,"recordid=?", new String[]{ recordId }, null ,null,"sendtime desc");
        int i = cursor.getCount();
        Log.i("i:"," "+i);
        if (i == 0){
            SQLiteDatabase dbInsert = dao.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("department", department);
            values.put("sendtime", sendTime);
            values.put("title", title);
            values.put("recordid", recordId);
            dbInsert.insert("PublishedNoticeListTable", null, values);
            dbInsert.close();
        }
        cursor.close();
    }
}
