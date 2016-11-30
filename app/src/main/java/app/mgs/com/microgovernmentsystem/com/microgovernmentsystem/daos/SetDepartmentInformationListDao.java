package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.DepartmentInformationListActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Values;

/**
 * Created by Administrator on 2016/8/21.
 */
public class SetDepartmentInformationListDao {

    Dao dao = new Dao(DepartmentInformationListActivity.activity,null, Values.database_version);
    public void SetDepartmentInformationListDao(String id, String title,String subject, String sendPeople,String updateDate, String departName){
        //先查看数据库中是否存在该条数据,如果存在则不做任何操作，如果不存在则插入该条数据
        SQLiteDatabase dbRead = dao.getReadableDatabase();
        Cursor cursor = dbRead.query("DepartmentInformationListTable",null,"id=?",new String[]{ id},null,null,"updateDate desc");
        int i = cursor.getCount();
        Cursor cursor1 = dbRead.query("ReadStatusTable",null,"id=?",new String[]{ id},null,null,null);
        int j = cursor1.getCount();
        Log.i("i:"," "+i);
        if (i == 0){
            //若无数据则进行数据插入
            SQLiteDatabase dbInsert = dao.getWritableDatabase();
            ContentValues values = new ContentValues();;
            values.put("id", id);
            values.put("title", title);
            values.put("subject", subject);
            values.put("sendPeople", sendPeople);
            values.put("updateDate", updateDate);
            if(j!=0){
                values.put("read",1);
            }
            if(departName.isEmpty())
            {
                departName ="无归属部门";
            }
            values.put("departName", departName);
            dbInsert.insert("DepartmentInformationListTable", null, values);
            dbInsert.close();
        }
        cursor.close();
    }
}