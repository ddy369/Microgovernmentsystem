package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.DocumentListActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.StringTransformation;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Values;

/**
 * Created by dingyi on 2016-08-15.
 */
public class SetDocumentListDao {
    Dao dao = new Dao(DocumentListActivity.activity, null, Values.database_version);

    public void setDocumentList(String id, String title, String subject, String updateDate,String sendPeople,
                                String readFlag,String purview){
        //先查看数据库中是否存在该条数据,如果存在则不做任何操作，如果不存在则插入该条数据
        SQLiteDatabase dbRead = dao.getReadableDatabase();
        Cursor cursor = dbRead.rawQuery("select * from DocumentTable where purview = ? and id = ?", new String[]{ purview,id });
        int i = cursor.getCount();
        if (i == 0) {
            SQLiteDatabase dbInsert = dao.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("id", id);
            values.put("title", title);
            values.put("subject", subject);
            values.put("updateTime",updateDate);
            values.put("sendPeople", sendPeople);
            values.put("read",readFlag);
            values.put("purview",purview);
            dbInsert.insert("DocumentTable", null, values);
            dbInsert.close();
        }
        cursor.close();
    }

}
