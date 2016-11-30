package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.DepartmentInformationListActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.Dao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.ListBean;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.ListBeanSelect;
public class DepartmentInformationListDataHelper {
    Dao dao = new Dao(DepartmentInformationListActivity.activity,null, Values.database_version);
    ArrayList<ListBean> listBeans = new ArrayList<>();
    private String strWhere;
    private int read=1;
    private String sText;
    private String sTitle;
    private String search;
    public DepartmentInformationListDataHelper() {
    }

    public DepartmentInformationListDataHelper(String str1,String str2,String str3,int reads) {
        strWhere = str1;
        sTitle = str2;
        read = reads;
        search = str3;
    }
    public DepartmentInformationListDataHelper(String str1,String str2) {
        strWhere = str1;
        sTitle = str2;
    }

//    public DepartmentInformationListDataHelper(String selectText) {
//        sText = selectText;
//        read = 2;
//        strWhere = "";
//    }

    public ArrayList<ListBean> setListBeans(String str){
        search = str;
        //连接数据库查询列表中所需的数据
        SQLiteDatabase dbRead = dao.getReadableDatabase();
        Cursor cursor = dbRead.rawQuery("select * from DepartmentInformationListTable where subject like ? order by updateDate desc",new String[]{"%"+search+"%"});
        if (cursor.moveToFirst()){
            for (int i = 1; i <= cursor.getCount(); i++){
                String id = cursor.getString(cursor.getColumnIndex("id"));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String updateDate = cursor.getString(cursor.getColumnIndex("updateDate"));
                String sendPeople = cursor.getString(cursor.getColumnIndex("sendPeople"));
                int read = cursor.getInt(cursor.getColumnIndex("read"));
                String subject = cursor.getString(cursor.getColumnIndex("subject"));
                String departName = cursor.getString(cursor.getColumnIndex("departName"));
                setListBean(title,updateDate,id,subject,read,sendPeople,departName);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return listBeans;
    }

    public int selectCount(){
        //查询数据条目确定pagenumber
        SQLiteDatabase dbRead = dao.getReadableDatabase();
        Cursor cursor = dbRead.query("DepartmentInformationListTable", null,null,null,null,null,null);
        int i = cursor.getCount();
        cursor.close();
        return i;
    }

    private void setListBean(String title,
                             String updateDate,
                             String id,
                             String subject,
                             int read,
                             String sendPeople,
                             String departName){
        ListBean listBean = new ListBean();
        listBean.setTitle(title);
        listBean.setUpdateDate(updateDate);
        listBean.setId(id);
        listBean.setSendPeople(sendPeople);
        listBean.setSubject(subject);
        listBean.setDepartName(departName);
        listBean.setRead(read);
        listBeans.add(listBean);
    }

    public ArrayList<ListBean> setListBeansRead(String str){
        search = str;
        //连接数据库查询列表中的所有已读数据
        SQLiteDatabase dbRead = dao.getReadableDatabase();
        Cursor cursor = dbRead.rawQuery("select * from DepartmentInformationListTable where read=1 and subject like ? order by updateDate desc",new String[]{"%"+search+"%"});
        if (cursor.moveToFirst()){
            for (int i = 1; i <= cursor.getCount(); i++){
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String sendPeople = cursor.getString(cursor.getColumnIndex("sendPeople"));
                String subject = cursor.getString(cursor.getColumnIndex("subject"));
                String departName = cursor.getString(cursor.getColumnIndex("departName"));
                String updateDate = cursor.getString(cursor.getColumnIndex("updateDate"));
                String id = cursor.getString(cursor.getColumnIndex("id"));
                int read = cursor.getInt(cursor.getColumnIndex("read"));
                setListBean(title,updateDate,id,subject,read,sendPeople,departName);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return listBeans;
    }

    public ArrayList<ListBean> setListBeansNoRead(String str){
        search = str;
        //连接数据库查询列表中的所有未读数据
        SQLiteDatabase dbRead = dao.getReadableDatabase();
        Cursor cursor = dbRead.rawQuery("select * from DepartmentInformationListTable where read=0 and subject like ? order by updateDate desc",new String[]{"%"+search+"%"});
        if (cursor.moveToFirst()){
            for (int i = 1; i <= cursor.getCount(); i++){
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String sendPeople = cursor.getString(cursor.getColumnIndex("sendPeople"));
                String subject = cursor.getString(cursor.getColumnIndex("subject"));
                String departName = cursor.getString(cursor.getColumnIndex("departName"));
                String updateDate = cursor.getString(cursor.getColumnIndex("updateDate"));
                String id = cursor.getString(cursor.getColumnIndex("id"));
                int read = cursor.getInt(cursor.getColumnIndex("read"));
                setListBean(title,updateDate,id,subject,read,sendPeople,departName);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return listBeans;
    }

//    public ArrayList<ListBean> setListBeansDepartment(String str){
//        search = str;
//        //连接数据库查询列表中的数据
//        SQLiteDatabase dbRead = dao.getReadableDatabase();
//        Cursor cursor = dbRead.rawQuery("select * from DepartmentInformationListTable where departName = '信息中心-信息交流'",null);
//        if (cursor.moveToFirst()){
//            for (int i = 1; i <= cursor.getCount(); i++){
//                String title = cursor.getString(cursor.getColumnIndex("title"));
//                String sendPeople = cursor.getString(cursor.getColumnIndex("sendPeople"));
//                String subject = cursor.getString(cursor.getColumnIndex("subject"));
//                String departName = cursor.getString(cursor.getColumnIndex("departName"));
//                String updateDate = cursor.getString(cursor.getColumnIndex("updateDate"));
//                String id = cursor.getString(cursor.getColumnIndex("id"));
//                int read = cursor.getInt(cursor.getColumnIndex("read"));
//                setListBean(title,updateDate,id,subject,read,sendPeople,departName);
//                cursor.moveToNext();
//            }
//        }
//        cursor.close();
//        return listBeans;
//    }
    public void setAllRead(){
        Dao dao = new Dao(DepartmentInformationListActivity.activity, null, Values.database_version);
        SQLiteDatabase dbRead1 = dao.getReadableDatabase();
        SQLiteDatabase dbWrite1 = dao.getWritableDatabase();
        SQLiteDatabase dbRead2 = dao.getReadableDatabase();
        SQLiteDatabase dbWrite2 = dao.getWritableDatabase();
        Cursor cursor = dbRead1.query("DepartmentInformationListTable", null, null , null, null, null, null);
        int i = cursor.getCount();
        if (cursor.moveToNext()){
            for (int j = 1; j <= cursor.getCount(); j++) {
                String sql = "update DepartmentInformationListTable set read=1";
                dbWrite1.execSQL(sql);
                String id = cursor.getString(cursor.getColumnIndex("id"));
                ContentValues values = new ContentValues();
                Cursor cursor1 = dbRead2.rawQuery("select * from ReadStatusTable where id = ?", new String[]{id});
                int k = cursor1.getCount();
                //Log.i("ididididididid", "" + k + " " + i + " " + id);
                if (k == 0) {
                   // Log.i("idididididididid", "" + id);
                    values.put("id", id);
                    long o = dbWrite2.insert("ReadStatusTable", null, values);
                }
                cursor1.close();
                cursor.moveToNext();
            }
        }
        cursor.close();
    }
    public ArrayList<ListBean> setListBeansDepartmentSort() {
        //连接数据库查询列表中所需的数据
        SQLiteDatabase dbRead = dao.getReadableDatabase();
        Cursor cursor = null;
        if(search==null){
            search="";
        }
        if (strWhere.equals("全部")&&sTitle.equals("全部")) {
            cursor = dbRead.rawQuery("select * from DepartmentInformationListTable where subject like ? order by updateDate desc", new String[]{"%"+search+"%"} );
        }else if (!strWhere.equals("全部")&& !sTitle.equals("全部")) {
            cursor = dbRead.rawQuery("select * from DepartmentInformationListTable where title = ? and departName = ? and subject like ? order by updateDate desc", new String[]{sTitle,strWhere,"%"+search+"%"});
        }else{
            if (!strWhere.equals("全部") && sTitle.equals("全部")) {
                cursor = dbRead.rawQuery("select * from DepartmentInformationListTable where departName = ? and subject like ? order by updateDate desc", new String[]{strWhere,"%"+search+"%"});
            }
            if (!sTitle.equals("全部") && strWhere.equals("全部")) {
                cursor = dbRead.rawQuery("select * from DepartmentInformationListTable where title = ? and subject like ? order by updateDate desc", new String[]{sTitle,"%"+search+"%"});
            }
        }
        //assert cursor != null;
        if (cursor.moveToFirst()){
            for (int i = 1; i <= cursor.getCount(); i++){
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String sendPeople = cursor.getString(cursor.getColumnIndex("sendPeople"));
                String subject = cursor.getString(cursor.getColumnIndex("subject"));
                String departName = cursor.getString(cursor.getColumnIndex("departName"));
                String updateDate = cursor.getString(cursor.getColumnIndex("updateDate"));
                String id = cursor.getString(cursor.getColumnIndex("id"));
                int read = cursor.getInt(cursor.getColumnIndex("read"));
                setListBean(title,updateDate,id,subject,read,sendPeople,departName);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return listBeans;
    }
}
