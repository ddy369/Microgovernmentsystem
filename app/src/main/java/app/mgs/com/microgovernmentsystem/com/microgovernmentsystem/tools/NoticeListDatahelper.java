package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.NoticeListActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.Dao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.ListBean;

public class NoticeListDatahelper {

    Dao dao = new Dao(NoticeListActivity.activity,null, Values.database_version);
    ArrayList<ListBean> listBeans = new ArrayList<>();

    public ArrayList<ListBean> setListBeans(){
//        search = str;
        //连接数据库查询列表中所需的数据
        SQLiteDatabase dbRead = dao.getReadableDatabase();
        Cursor cursor = dbRead.rawQuery("select * from  NOTICELISTTABLE order by sendtime desc",null);
        //Cursor cursor = dbRead.query("NOTICELISTTABLE", null,null,null,null,null,"sendtime");
        if (cursor.moveToFirst()){
            for (int i = 1; i <= cursor.getCount(); i++){
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String department = cursor.getString(cursor.getColumnIndex("department"));
                String time = cursor.getString(cursor.getColumnIndex("sendtime"));
                String id = cursor.getString(cursor.getColumnIndex("recordid"));
                int read = cursor.getInt(cursor.getColumnIndex("read"));
                setListBean(title,department,time,id,read);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return listBeans;
    }

    public int selectCount(){
        //查询数据条目确定pagenumber
        SQLiteDatabase dbRead = dao.getReadableDatabase();
        Cursor cursor = dbRead.query("NOTICELISTTABLE", null,null,null,null,null,null);
        return cursor.getCount();
    }

    public ArrayList<ListBean> setListBean(String title,
                             String department,
                             String time,
                             String id,
                             int read){
        ListBean listBean = new ListBean();
        listBean.setTitle(title);
        listBean.setDepartment(department);
        listBean.setTime(time);
        listBean.setId(id);
        listBean.setRead(read);
        listBeans.add(listBean);
        return listBeans;
    }

//    public ArrayList<ListBean> setListBeansRead(String str){
//        //连接数据库查询列表中的所有已读数据
//        search = str;
//        SQLiteDatabase dbRead = dao.getReadableDatabase();
//        Cursor cursor = dbRead.rawQuery("select * from NOTICELISTTABLE where read=1 and title like ? order by sendtime desc",new String[]{"%"+search+"%"});
//        if (cursor.moveToFirst()){
//            for (int i = 1; i <= cursor.getCount(); i++){
//                String title = cursor.getString(cursor.getColumnIndex("title"));
//                String department = cursor.getString(cursor.getColumnIndex("department"));
//                String time = cursor.getString(cursor.getColumnIndex("sendtime"));
//                String id = cursor.getString(cursor.getColumnIndex("recordid"));
//                int read = cursor.getInt(cursor.getColumnIndex("read"));
//                setListBean(title,department,time,id,read);
//                cursor.moveToNext();
//            }
//        }
//        return listBeans;
//    }
//    public ArrayList<ListBean> setListBeansNoRead(String str){
//        search = str;
//        //连接数据库查询列表中的所有未读数据
//        SQLiteDatabase dbRead = dao.getReadableDatabase();
//        Cursor cursor = dbRead.rawQuery("select * from NOTICELISTTABLE where read=0 and title like ? order by sendtime desc",new String[]{"%"+search+"%"});
//        if (cursor.moveToFirst()){
//            for (int i = 1; i <= cursor.getCount(); i++){
//                String title = cursor.getString(cursor.getColumnIndex("title"));
//                String department = cursor.getString(cursor.getColumnIndex("department"));
//                String time = cursor.getString(cursor.getColumnIndex("sendtime"));
//                String id = cursor.getString(cursor.getColumnIndex("recordid"));
//                int read = cursor.getInt(cursor.getColumnIndex("read"));
//                setListBean(title,department,time,id,read);
//                cursor.moveToNext();
//            }
//        }
//        return listBeans;
//    }
}
