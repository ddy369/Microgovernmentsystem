package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.NoticeListActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Values;

public class NoticeListRecordIdDao {

    Dao dao = new Dao(NoticeListActivity.activity,null, Values.database_version);

    public String getRecordId(){
        SQLiteDatabase dbRead = dao.getReadableDatabase();
        Cursor cursor;
        cursor = dbRead.query("RECORDIDTABLE",null,null,null,null,null,null);
        String id = null;
        if (cursor.getCount()!=0){
            cursor = dbRead.query("RECORDIDTABLE", new String[]{ "recordid" }, "id=?", new String[]{ "1" } , null, null, null);
            if (cursor.moveToNext()){
                id =  cursor.getString(0);
            }
        }
        cursor.close();
        Log.i("ID :" , "aa+" +id);
        return id;
    }

    public void setRecordId(String recordId){
        //先查询表中是否存在字段，若有则更新，若无则插入
        SQLiteDatabase dbWrite = dao.getWritableDatabase();
        String recordid = getRecordId();
        if (recordid == null){
            ContentValues values = new ContentValues();
            values.put("recordid", recordId);
            dbWrite.insert("RECORDIDTABLE", null, values);
            dbWrite.close();
        }else {
            ContentValues values = new ContentValues();
            values.put("recordid", recordId);
            String[] args = {
                    String.valueOf(1)
            };
            dbWrite.update("RECORDIDTABLE",values,"id=?",args);
        }
    }
}
