package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.MagazineListActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Values;

/**
 * Created by Administrator on 2016-08-22.
 */
public class SetMagazineListDao {

    Dao dao = new Dao(MagazineListActivity.activity, null, Values.database_version);

    public void setMagazineList(String id, String title, String subject, String updateDate,
                                String sendPeople,String numNO,String year,String year_no,String sysId){
        //先查看数据库中是否存在该条数据,如果存在则不做任何操作，如果不存在则插入该条数据
        SQLiteDatabase dbRead = dao.getReadableDatabase();
        //Cursor cursor = dbRead.rawQuery("select * from MagazineTable where id = ? and sendPeople = ? ", new String[]{ id,sendPeople });
        Cursor cursor = dbRead.rawQuery("select * from MagazineTable where id = ?  ", new String[]{ id });
        Cursor cursor1 = dbRead.query("ReadStatusTable", null,"id=?", new String[]{ id }, null ,null,null);
        int j = cursor1.getCount();
        int i = cursor.getCount();
        if (i == 0){
            SQLiteDatabase dbInsert = dao.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("id", id);
            values.put("title", title);
            values.put("subject", subject);
            values.put("updateTime", updateDate);
            values.put("sendPeople",sendPeople);
            values.put("numNO",numNO);
            values.put("year",year);
            values.put("year_no",year_no);
            values.put("sysId",sysId);
            if(j!=0){
                values.put("read",1);
            }
            dbInsert.insert("MagazineTable", null, values);
            dbInsert.close();
        }
        cursor.close();
    }

}

