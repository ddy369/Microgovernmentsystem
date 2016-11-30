package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.PublicInformationListActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Values;

/**
 * Created by Administrator on 2016/8/21.
 */
public class SetPublicInformationListDao {

    Dao dao = new Dao(PublicInformationListActivity.activity,null, Values.database_version);
    public void SetPublicInformationListDao(String id, String title,String updateDate, String field){
        //先查看数据库中是否存在该条数据,如果存在则不做任何操作，如果不存在则插入该条数据
        SQLiteDatabase dbRead = dao.getReadableDatabase();
        Cursor cursor = dbRead.query("PublicInformationListTable",null,"id=?",new String[]{ id},null,null,"updateDate desc");
        Cursor cursor1 = dbRead.query("ReadStatusTable",null,"id=?",new String[]{id},null,null,null);
        int i = cursor.getCount();
        int j = cursor1.getCount();
        Log.i("i:"," "+i);
        //Log.i("00000000j:","已读"+j);
        if (i == 0){
            //若无数据则进行数据插入
            SQLiteDatabase dbInsert = dao.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("id", id);
            values.put("title", title);
            values.put("updateDate", updateDate);
            values.put("field", field);
            if(j!=0){
                values.put("read",1);
            }
            dbInsert.insert("PublicInformationListTable", null, values);
            dbInsert.close();
        }
        cursor.close();
    }
}