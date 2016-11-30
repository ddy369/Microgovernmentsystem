package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.MagazineListActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.PublicInformationListActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.Dao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.ListBeanMagazine;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.ListBeanSelect;

/**
 * Created by Administrator on 2016-08-22.
 */
public class MagazineListDataHelper {

    Dao dao = new Dao(MagazineListActivity.activity, null, Values.database_version);

    ArrayList<ListBeanMagazine> listBeansMagazine = new ArrayList<>();

    private String strWhere;

    private int read;

    private String sText;

    public MagazineListDataHelper() {
    }

    public MagazineListDataHelper(String str, int reads) {
        strWhere = str;
        read = reads;
    }

    public MagazineListDataHelper(String selectText) {
        sText = selectText;
        read = 2;
        strWhere = "";
    }

    public MagazineListDataHelper(String str, int reads,String selectText) {
        strWhere = str;
        read = reads;
        sText = selectText;
    }


    public ArrayList<ListBeanMagazine> setListBeansMagazine() {
        //连接数据库查询列表中所需的数据
        String check = "0";
        SQLiteDatabase dbRead = dao.getReadableDatabase();
        Cursor cursor = null;
            if(sText == "全部" | sText == null){
                sText ="";
            }
            if (read == 2) {
                if (strWhere != null && strWhere.length() <= 0) {
                    cursor = dbRead.rawQuery("select * from MagazineTable where title like ? order by updateTime desc", new String[]{"%" + sText + "%"});
                } else {
                    cursor = dbRead.rawQuery("select * from MagazineTable where subject like ? and title like ? order by updateTime desc", new String[]{"%" + strWhere + "%","%" + sText + "%"});
                }
            } else {
                if (strWhere != null && strWhere.length() <= 0) {
                    cursor = dbRead.rawQuery("select * from MagazineTable where read = ? and title like ?  order by updateTime desc", new String[]{String.valueOf(read), "%" + sText + "%"});
                } else {
                    cursor = dbRead.rawQuery("select * from MagazineTable where subject like ? and read = ? and title like ?  order by updateTime desc", new String[]{"%" + strWhere + "%", String.valueOf(read), "%" + sText + "%"});
                }
            }
        if (cursor.moveToFirst()) {
            for (int i = 1; i <= cursor.getCount(); i++){
                String title = cursor.getString(cursor.getColumnIndex("title"));
                if (title.length() > 20) {
                    title = title.substring(0,20);
                }
                String subject = cursor.getString(cursor.getColumnIndex("subject"));
                String id = cursor.getString(cursor.getColumnIndex("id"));
                String updateTime = cursor.getString(cursor.getColumnIndex("updateTime"));
                int read = cursor.getInt(cursor.getColumnIndex("read"));
                String sendPeople = cursor.getString(cursor.getColumnIndex("sendPeople"));
                String sysId = cursor.getString(cursor.getColumnIndex("sysId"));
                String year_no = cursor.getString(cursor.getColumnIndex("year_no"));
                setListBean(id, title, subject, updateTime, read,sendPeople,sysId,year_no);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return listBeansMagazine;
    }

    public int selectCount() {
        //查询数据条目确定pagenumber
        SQLiteDatabase dbRead = dao.getReadableDatabase();
        Cursor cursor = dbRead.query("MagazineTable", null, null, null, null, null, null);
        int i =  cursor.getCount();
        cursor.close();
        return  i;
    }

    private void setListBean(String id, String title, String subject, String updateTime, int read,String sendPeople,String sysId,String year_no) {
        ListBeanMagazine listBean = new ListBeanMagazine(id, title, subject, updateTime, read,sendPeople,sysId,year_no);
        listBeansMagazine.add(listBean);
    }

    public void setAllRead() {
        SQLiteDatabase sqLiteDatabase = dao.getReadableDatabase();
        sqLiteDatabase.execSQL(" update MagazineTable set read = ? ", new String[]{"1"});
        Dao dao = new Dao(MagazineListActivity.activity, null, Values.database_version);
        SQLiteDatabase dbRead1 = dao.getReadableDatabase();
        SQLiteDatabase dbRead2 = dao.getReadableDatabase();
        SQLiteDatabase dbWrite2 = dao.getWritableDatabase();
        Cursor cursor = dbRead1.query("MagazineTable", null, null , null, null, null, null);
        int i =cursor.getCount();
        if (cursor.moveToNext()){
            for(int j =0;j<i;j++) {
                String id = cursor.getString(cursor.getColumnIndex("id"));
                ContentValues values = new ContentValues();
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
        Cursor cursor = dbRead.rawQuery("select distinct title from MagazineTable ", null);
        listBeanSelect.add(new ListBeanSelect("全部",true));
        if (cursor.moveToFirst()) {
            do {
                String values = cursor.getString(cursor.getColumnIndex("title"));
                ListBeanSelect listBeanSelect1 = new ListBeanSelect(values, false);
                listBeanSelect.add(listBeanSelect1);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return  listBeanSelect;
    }

}
