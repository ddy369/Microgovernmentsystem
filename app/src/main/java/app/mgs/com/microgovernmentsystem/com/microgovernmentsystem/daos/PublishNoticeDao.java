package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.PublishNoticeActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Values;

public class PublishNoticeDao {

    private Context context;

    public PublishNoticeDao(Context context){
        this.context = context;
    }

    Dao dao = new Dao(PublishNoticeActivity.activity,null, Values.database_version);
    SQLiteDatabase dbRead = dao.getReadableDatabase();
    SQLiteDatabase dbWrite = dao.getWritableDatabase();

    public String selectPeopleList(){
        Cursor cursor = dbRead.query(true,"ReceiverTable",null,"isChoice=?",
                new String[]{ "1" },null,null,null,null);
        String peopleList = "";
        if (cursor.moveToFirst()){
            peopleList = cursor.getString(cursor.getColumnIndex("receiverId"));
            cursor.moveToNext();
            for (int i = 1; i < cursor.getCount(); i++){
                peopleList = peopleList + "," + cursor.getString(cursor.getColumnIndex("receiverId"));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return peopleList;
    }

    public String selectOrganizeList(){
        Cursor cursor = dbRead.query(true,"DepartmentTable",null,"isChoice=?",
                new String[]{ "1" },null,null,null,null);
        String organizeList = "";
        if (cursor.moveToFirst()){
            organizeList = cursor.getString(cursor.getColumnIndex("departmentId"));
            cursor.moveToNext();
            for (int i = 1; i < cursor.getCount(); i++){
                organizeList = organizeList + "," + cursor.getString(cursor.getColumnIndex("departmentId"));
                cursor.moveToNext();
            }
        }
        Log.i("organizeList",organizeList);
        cursor.close();
        return organizeList;
    }

    /**
     * 将数据库表中所有关于选中的字段重置为0
     */
    public void resetChoice(){
        ContentValues values = new ContentValues();
        values.put("isChoice",0);
        values.put("selected",0);
        dbWrite.update("DepartmentTable",values,null,null);

        ContentValues values1 = new ContentValues();
        values1.put("isChoice",0);
        dbWrite.update("ReceiverTable",values1,null,null);
    }

}
