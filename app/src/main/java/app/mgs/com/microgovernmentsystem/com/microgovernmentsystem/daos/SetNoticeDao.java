package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.NoticeActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Values;


public class SetNoticeDao {

    Dao dao = new Dao(NoticeActivity.activity,null, Values.database_version);

    public void daoNoticeOperation(String recordid,
                             String title,
                             String sender,
                             String time,
                             String organizelist,
                             String peoplelist,
                             String isreturn,
                             String sendmessage,
                             String content
                             ){
        //先查看表中是否有数据，有则删掉
        SQLiteDatabase dbRead = dao.getReadableDatabase();
        SQLiteDatabase dbWrite = dao.getWritableDatabase();
//        Cursor cursor = dbRead.query("NOTICETABLE",null,null,null,null,null,null);
//        if (cursor.getCount() != 0){
//            dbWrite.execSQL("delete from NOTICETABLE");
//        }
        //若无数据则进行数据插入
        ContentValues values = new ContentValues();
        values.put("recordid", recordid);
        values.put("title", title);
        values.put("sender", sender);
        values.put("time", time);
        values.put("organizelist", organizelist);
        values.put("peoplelist", peoplelist);
        values.put("isreturn", isreturn);
        values.put("sendmessage", sendmessage);
        values.put("content", content);

        dbWrite.insert("NOTICETABLE", null, values);
//        cursor.close();
    }

    public void daoAttachment(String name,
                              String url,
                              String size,
                              String recordid){
        //先查看表中是否有数据，有则删掉
        SQLiteDatabase dbRead = dao.getReadableDatabase();
        SQLiteDatabase dbWrite = dao.getWritableDatabase();
        //若无数据则进行数据插入
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("url", url);
        values.put("size", size);
        values.put("recordid",recordid);
        dbWrite.insert("NoticeAttachmentTable", null, values);
        dbWrite.close();
    }
}
