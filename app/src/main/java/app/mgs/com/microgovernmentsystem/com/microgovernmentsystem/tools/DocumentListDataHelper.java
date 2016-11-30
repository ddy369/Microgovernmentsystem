package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.DocumentListActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.Dao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.ListBeanDocument;

/**
 * Created by dingyi on 2016-08-13.
 */
public class DocumentListDataHelper {

    Dao dao = new Dao(DocumentListActivity.activity,null, Values.database_version);

    ArrayList<ListBeanDocument> listBeansDocument = new ArrayList<>();

    private String strWhere;

    private int read;

    public DocumentListDataHelper(){}

    public DocumentListDataHelper(String str,int reads){
        strWhere = str;
        read = reads;
    }


    public ArrayList<ListBeanDocument> setListBeansDocument(){
        //连接数据库查询列表中所需的数据
        SQLiteDatabase dbRead = dao.getReadableDatabase();
        Cursor cursor = null;
        if(read == 2) {  //2 表示全部
            if (strWhere != null && strWhere.length() <= 0) {
                cursor = dbRead.rawQuery("select * from DocumentTable  order by updateTime desc", null);
            } else {
                cursor = dbRead.rawQuery("select * from DocumentTable where subject like ? order by updateTime desc", new String[]{"%" + strWhere.trim() + "%"});
            }
        }else{
            if (strWhere != null && strWhere.length() <= 0) {
                cursor = dbRead.rawQuery("select * from DocumentTable where read = ?  order by updateTime desc ", new String[]{String.valueOf(read)});
            } else {
                cursor = dbRead.rawQuery("select * from DocumentTable where subject like ? and read = ?  order by updateTime desc", new String[]{"%" + strWhere.trim() + "%",String.valueOf(read)});
            }
        }
        if (cursor.moveToFirst()){
            for (int i = 1; i <= cursor.getCount(); i++){
                String id = cursor.getString(cursor.getColumnIndex("id"));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                if(title.length() > 20) {
                    title = title.substring(0,20);
                }
                String subject= cursor.getString(cursor.getColumnIndex("subject"));
                String updateTime = cursor.getString(cursor.getColumnIndex("updateTime"));
                int read = cursor.getInt(cursor.getColumnIndex("read"));
                String purview = cursor.getString(cursor.getColumnIndex("purview"));
                setListBean(id,title,subject,updateTime,read,purview);
                cursor.moveToNext();
            }
        }
        return listBeansDocument;
    }

    public int selectCount(){
        //查询数据条目确定pagenumber
        SQLiteDatabase dbRead = dao.getReadableDatabase();
        Cursor cursor = dbRead.query("DocumentTable", null,null,null,null,null,null);
        return cursor.getCount();
    }

    private void setListBean(String id,String title,String subject, String updateTime,int read,String purview){
        ListBeanDocument listBean = new ListBeanDocument(id,title,subject,updateTime,read,purview);
        listBeansDocument.add(listBean);
    }

    public void setAllRead(){
        SQLiteDatabase sqLiteDatabase = dao.getReadableDatabase();
        sqLiteDatabase.execSQL("update DocumentTable set read = ? ",new String[]{"1"});
    }
}
