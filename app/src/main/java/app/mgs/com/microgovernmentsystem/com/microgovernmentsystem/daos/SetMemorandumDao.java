package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.HashMap;

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.MemorandumTagActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.CompareTime;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Values;

/**
 * 备忘录功能数据库的相关操作
 */
public class SetMemorandumDao {

    Dao dao = new Dao(MemorandumTagActivity.activity ,null, Values.database_version);
    SQLiteDatabase dbRead = dao.getReadableDatabase();
    SQLiteDatabase dbWrite = dao.getWritableDatabase();

    public void insertMemorandum(String title,
                              String content,
                              int year,
                              int month,
                              int day,
                              int hour,
                              int minute,
                              int notice){
            //插入备忘录消息
            ContentValues values = new ContentValues();
            values.put("month", month);
            values.put("hour", hour);
            values.put("title", title);
            values.put("minute", minute);
            values.put("day", day);
            values.put("switch", notice);
            values.put("content", content);
            values.put("year", year);
            dbWrite.insert("MemorandumTable", null, values);
    }

    public void updateMemorandum(String title,
                                 String content,
                                 int year,
                                 int month,
                                 int day,
                                 int hour,
                                 int minute,
                                 int notice,
                                 int id){
        //插入备忘录消息
        ContentValues values = new ContentValues();
        values.put("month", month);
        values.put("hour", hour);
        values.put("title", title);
        values.put("minute", minute);
        values.put("day", day);
        values.put("switch", notice);
        values.put("content", content);
        values.put("year", year);
        dbWrite.update("MemorandumTable", values, "id=?",new String[]{String.valueOf(id)});
    }

    /**
     *取最新一条备忘录消息的id
     */
    public int selectMemorandum(){
        int id = 0;
        Cursor cursor = dbRead.query("MemorandumTable",null, null, null, null, null, null);
        if (cursor.moveToFirst()){
            for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext())
            {
                id = cursor.getInt(cursor.getColumnIndex("id"));
            }
        }
        return id;
    }

    /**
     * 查询对应id的标题
     *@return 返回标题
     */
    public String selectTitle(int id){
        String sql = "select title from MemorandumTable where id='"+id+"'";
        Cursor cursor = dbRead.rawQuery(sql,null);
        String title = null;
        if (cursor.moveToFirst()){
            for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext())
            {
                title = cursor.getString(cursor.getColumnIndex("title"));
            }
        }
        return title;
    }

    /**
     * 查询对应id的所有信息
     */
    public HashMap<String , String> getContent(int id){
        String title = null;//标题
        String content = null;//内容
        String time = null;//时间
        String notice = null;//是否提醒
        String sql = "select * from MemorandumTable where id='"+id+"'";
        Cursor cursor = dbRead.rawQuery(sql,null);
        if (cursor.moveToNext()){
            title = cursor.getString(cursor.getColumnIndex("title"));
            content = cursor.getString(cursor.getColumnIndex("content"));
            int y = cursor.getInt(cursor.getColumnIndex("year"));//年
            int mo = cursor.getInt(cursor.getColumnIndex("month"));//月
            int d = cursor.getInt(cursor.getColumnIndex("day"));//日
            int h = cursor.getInt(cursor.getColumnIndex("hour"));//时
            int m = cursor.getInt(cursor.getColumnIndex("minute"));//分
            int notice1 = cursor.getInt(cursor.getColumnIndex("switch"));//是否设置闹钟提示
            time = h+"时"+m+"分";
            CompareTime compareTime = new CompareTime();
            int i = compareTime.compareTime(y,mo,d,h,m);
            if (i == 1 && notice1 == 1){
                notice = "t";
            }else {
                notice = "f";
            }
        }
        HashMap<String , String> map = new HashMap<>();
        map.put("title",title);
        map.put("time",time);
        map.put("content",content);
        map.put("notice",notice);
        return map;
    }

    /**
     * 单独再查询一下时间,方便存取
     */
    public HashMap<String , Integer> getTime(int id){
        int y = 0;
        int mo = 0;
        int d = 0;
        int h = 0;
        int m = 0;
        String sql = "select * from MemorandumTable where id='"+id+"'";
        Cursor cursor = dbRead.rawQuery(sql,null);
        if (cursor.moveToNext()){
             y = cursor.getInt(cursor.getColumnIndex("year"));
             mo = cursor.getInt(cursor.getColumnIndex("month"));
             d = cursor.getInt(cursor.getColumnIndex("day"));
             h = cursor.getInt(cursor.getColumnIndex("hour"));
             m = cursor.getInt(cursor.getColumnIndex("minute"));
        }
        HashMap<String , Integer> map = new HashMap<>();
        map.put("year",y);
        map.put("month",mo);
        map.put("day",d);
        map.put("hour",h);
        map.put("minute",m);
        Log.i("年月日"," "+y+" "+mo+" "+d);
        return map;
    }

    /**
     * 传入id，根据id删除对应的记录行
     */
    public void deleteMemorandum(int id){
        dbWrite.execSQL("delete from MemorandumTable where id='"+id+"'");
    }

    /**
     * 取消消息提示，在用户点击通知栏的消息之后，消息变为已读，不再通知
     */
    public void cancelNotice(int id){
        dbWrite.execSQL("update MemorandumTable set switch=0 where id='"+id+"'");
    }
}
