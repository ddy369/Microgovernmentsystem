package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.MemorandumActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.NoticeListActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.Dao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.ListBean;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.MemorandumListBean;

/**
 * 此类用来将memorandum(备忘录表)中的数据拿出放入adapter的listbean中
 */
public class MemorandumListDatahelper {
    Dao dao = new Dao(MemorandumActivity.activity ,null, Values.database_version);

    ArrayList<MemorandumListBean> listBeans = new ArrayList<>();

    public ArrayList<MemorandumListBean> setListBeans(){
        //连接数据库查询列表中所需的数据
        SQLiteDatabase dbRead = dao.getReadableDatabase();
        Cursor cursor = dbRead.query("MemorandumTable", null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            for (int i = 1; i <= cursor.getCount(); i++){
                String title = cursor.getString(cursor.getColumnIndex("title"));
                int year = cursor.getInt(cursor.getColumnIndex("year"));
                int day = cursor.getInt(cursor.getColumnIndex("day"));
                int month = cursor.getInt(cursor.getColumnIndex("month"));
                int hour = cursor.getInt(cursor.getColumnIndex("hour"));
                int minute = cursor.getInt(cursor.getColumnIndex("minute"));
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                setMemorandumListBean(title,year,day,month,hour,minute,id);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return listBeans;
    }

    private void setMemorandumListBean(String title,
                             int year,
                             int day,
                             int month,
                             int hour,
                             int minute,
                             int id){
        MemorandumListBean listBean = new MemorandumListBean();
        listBean.setTitle(title);
        listBean.setYear(year);
        listBean.setMonth(month);
        listBean.setDay(day);
        listBean.setHour(hour);
        listBean.setMinute(minute);
        listBean.setId(id);
        listBeans.add(listBean);
    }

    /**
     * 查询备忘录列表是否为空
     */
    public int selectNum(){
        SQLiteDatabase dbRead = dao.getReadableDatabase();
        Cursor cursor = dbRead.query("MemorandumTable", null,null,null,null,null,null);
        if (cursor.getCount()==0){
            cursor.close();
            return 0;
        }
        else {
            cursor.close();
            return 1;
        }
    }
}
