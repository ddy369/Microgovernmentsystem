package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.PublicInformationListActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.Dao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.ListBean;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.ListBeanSelect;

public class PublicInformationListDatahelper {

    Dao dao = new Dao(PublicInformationListActivity.activity,null, Values.database_version);
    private String strWhere="";

    private int read;

    private String sText="";

    ArrayList<ListBean> listBeans = new ArrayList<>();

    public PublicInformationListDatahelper() {
    }

    public PublicInformationListDatahelper(String str, int reads) {
        strWhere = str;
        read = reads;
    }

    public PublicInformationListDatahelper(String selectText) {
        sText = selectText;
        read = 2;
        strWhere = "";
    }

    public ArrayList<ListBean> setListBeans(){
        //连接数据库查询列表中所需的数据
        //连接数据库查询列表中所需的数据
        SQLiteDatabase dbRead = dao.getReadableDatabase();
        Cursor cursor;
        Log.i("STEXT"," "+sText);
        if(sText.equals("全部")){
            sText ="";
        }
        if(strWhere.equals("全部")){
            strWhere="";
        }
//        if(sText.equals("全部")){
//            sText ="";
//        }
        if (read == 2) {
            if (strWhere.isEmpty()) {
                cursor = dbRead.rawQuery("select * from PublicInformationListTable where field like ? order by updateDate desc", new String[]{"%" + sText + "%"});
            } else {
                cursor = dbRead.rawQuery("select * from PublicInformationListTable where title like ? and field like ? order by updateDate desc", new String[]{"%" + strWhere + "%","%" + sText + "%"});
            }
        } else {
            if (read==0) {
                cursor = dbRead.rawQuery("select * from PublicInformationListTable where read = 0 and field like ?  order by updateDate desc", new String[]{"%" + sText + "%"});
            } else {
                cursor = dbRead.rawQuery("select * from PublicInformationListTable where title like ? and read = 1 and field like ?  order by updateDate desc", new String[]{"%" + strWhere + "%", "%" + sText + "%"});
            }
        }
        if (cursor.moveToFirst()){
            for (int i = 1; i <= cursor.getCount(); i++){
                String id = cursor.getString(cursor.getColumnIndex("id"));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String updateDate = cursor.getString(cursor.getColumnIndex("updateDate"));
                String field = cursor.getString(cursor.getColumnIndex("field"));
                int read = cursor.getInt(cursor.getColumnIndex("read"));
                setListBean(title,updateDate,id,field,read);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return listBeans;
    }

    public int selectCount(){
        //查询数据条目确定pagenumber
        SQLiteDatabase dbRead = dao.getReadableDatabase();
        Cursor cursor = dbRead.query("PublicInformationListTable", null,null,null,null,null,null);
        int i =cursor.getCount();
        cursor.close();
        return i;
    }
    public ArrayList<ListBean> setListBean(){
        //连接数据库查询列表中所需的数据
        //连接数据库查询列表中所需的数据
        SQLiteDatabase dbRead = dao.getReadableDatabase();
        Cursor cursor;
        Log.i("STEXT"," "+sText);
        if( sText.isEmpty()||sText.equals("全部")){
            sText ="";
        }
//        if(sText.equals("全部")){
//            sText ="";
//        }
        if (read == 2) {
            if (strWhere != null && strWhere.length() <= 0) {
                cursor = dbRead.rawQuery("select * from PublicInformationListTable where field like ? order by updateDate desc", new String[]{"%" + sText + "%"});
            } else {
                cursor = dbRead.rawQuery("select * from PublicInformationListTable where title like ? and field like ? order by updateDate desc", new String[]{"%" + strWhere + "%","%" + sText + "%"});
            }
        } else {
            if (strWhere != null && strWhere.length() <= 0) {
                cursor = dbRead.rawQuery("select * from PublicInformationListTable where read = ? and field like ?  order by updateDate desc", new String[]{String.valueOf(read), "%" + sText + "%"});
            } else {
                cursor = dbRead.rawQuery("select * from PublicInformationListTable where title like ? and read = ? and field like ?  order by updateDate desc", new String[]{"%" + strWhere + "%", String.valueOf(read), "%" + sText + "%"});
            }
        }
        if (cursor.moveToFirst()){
            for (int i = 1; i <= cursor.getCount(); i++){
                String id = cursor.getString(cursor.getColumnIndex("id"));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String updateDate = cursor.getString(cursor.getColumnIndex("updateDate"));
                String field = cursor.getString(cursor.getColumnIndex("field"));
                int read = cursor.getInt(cursor.getColumnIndex("read"));
                setListBean(title,updateDate,id,field,read);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return listBeans;
    }

    private void setListBean(String title,
                             String updateDate,
                             String id,
                             String field,
                             int read){
        ListBean listBean = new ListBean();
        listBean.setTitle(title);
        listBean.setUpdateDate(updateDate);
        listBean.setId(id);
        listBean.setField(field);
        listBean.setRead(read);
        listBeans.add(listBean);
    }

//    public ArrayList<ListBean> setListBeansRead(){
//        //连接数据库查询列表中的所有已读数据
//        SQLiteDatabase dbRead = dao.getReadableDatabase();
//        Cursor cursor = dbRead.rawQuery("select * from PublicInformationListTable where read=1",null);
//        if (cursor.moveToFirst()){
//            for (int i = 1; i <= cursor.getCount(); i++){
//                String title = cursor.getString(cursor.getColumnIndex("title"));
//                String field = cursor.getString(cursor.getColumnIndex("field"));
//                String updateDate = cursor.getString(cursor.getColumnIndex("updateDate"));
//                String id = cursor.getString(cursor.getColumnIndex("id"));
//                int read = cursor.getInt(cursor.getColumnIndex("read"));
//                setListBean(title,updateDate,id,field,read);
//                cursor.moveToNext();
//            }
//        }
//        cursor.close();
//        return listBeans;
//    }

//    public ArrayList<ListBean> setListBeansNoRead(){
//        //连接数据库查询列表中的所有未读数据
//        SQLiteDatabase dbRead = dao.getReadableDatabase();
//        Cursor cursor = dbRead.rawQuery("select * from PublicInformationListTable where read=0",null);
//        if (cursor.moveToFirst()){
//            for (int i = 1; i <= cursor.getCount(); i++){
//                String title = cursor.getString(cursor.getColumnIndex("title"));
//                String field = cursor.getString(cursor.getColumnIndex("field"));
//                String updateDate = cursor.getString(cursor.getColumnIndex("updateDate"));
//                String id = cursor.getString(cursor.getColumnIndex("id"));
//                int read = cursor.getInt(cursor.getColumnIndex("read"));
//                setListBean(title,updateDate,id,field,read);
//                cursor.moveToNext();
//            }
//        }
//        return listBeans;
//    }

//    public ArrayList<ListBean> setListBeansactivity(){
//        //连接数据库查询列表中的所有未读数据
//        Dao dao = new Dao(PublicInformationListActivity.activity, null, Values.database_version);
//        SQLiteDatabase dbRead = dao.getReadableDatabase();
//        Cursor cursor = dbRead.rawQuery("select * from PublicInformationListTable where field = '活动资讯'",null);
//        if (cursor.moveToFirst()){
//            for (int i = 1; i <= cursor.getCount(); i++){
//                String title = cursor.getString(cursor.getColumnIndex("title"));
//                String field = cursor.getString(cursor.getColumnIndex("field"));
//                String updateDate = cursor.getString(cursor.getColumnIndex("updateDate"));
//                String id = cursor.getString(cursor.getColumnIndex("id"));
//                int read = cursor.getInt(cursor.getColumnIndex("read"));
//                setListBean(title,updateDate,id,field,read);
//                cursor.moveToNext();
//            }
//        }
//        return listBeans;
//    }
    public void setAllRead(){
        Dao dao = new Dao(PublicInformationListActivity.activity, null, Values.database_version);
        SQLiteDatabase dbRead1 = dao.getReadableDatabase();
        SQLiteDatabase dbWrite1 = dao.getWritableDatabase();
        SQLiteDatabase dbRead2 = dao.getReadableDatabase();
        SQLiteDatabase dbWrite2 = dao.getWritableDatabase();
        Cursor cursor = dbRead1.query("PublicInformationListTable", null, null , null, null, null, null);
        int i =cursor.getCount();
        if (cursor.moveToNext()){
            for(int j =0;j<i;j++) {
                String id = cursor.getString(cursor.getColumnIndex("id"));
                String sql = "update PublicInformationListTable set read=1";
                ContentValues values = new ContentValues();
                dbWrite1.execSQL(sql);
                Cursor cursor1 = dbRead2.rawQuery("select * from ReadStatusTable where id = ?", new String[]{id});
                int k = cursor1.getCount();
                //Log.i("ididididididid", "" + k + " " + i + " " + id);
                if (k == 0) {
                    //Log.i("idididididididid", "" + id);
                    values.put("id", id);
                    long o = dbWrite2.insert("ReadStatusTable", null, values);

                }
                cursor.moveToNext();
                cursor1.close();
            }
        }
        cursor.close();
    }
    public ArrayList<ListBeanSelect> getSelectBeans(Context context) {
        Dao daos = new Dao(context, null, Values.database_version);
        SQLiteDatabase dbRead = daos.getReadableDatabase();
        ArrayList<ListBeanSelect> listBeanSelect = new ArrayList<>();
        Cursor cursor = dbRead.rawQuery("select distinct field from PublicInformationListTable ", null);
        listBeanSelect.add(new ListBeanSelect("全部",true));
        if (cursor.moveToFirst()) {
            do {
                String values = cursor.getString(cursor.getColumnIndex("field"));
                ListBeanSelect listBeanSelect1 = new ListBeanSelect(values, false);
                listBeanSelect.add(listBeanSelect1);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return  listBeanSelect;
    }
}