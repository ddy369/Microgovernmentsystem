package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.DepartmentInformationActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Values;

/**
 * Created by Administrator on 2016/8/29.
 */
public class SetDepartmentInformationDao {
    Dao dao = new Dao(DepartmentInformationActivity.activity, null, Values.database_version);

    public void daoDepartmentInformationOperation(String depid,
                                                  String mainTitle,
                                                  String depPeople,
                                                  String departName,
                                                  String depDate,
                                                  String depAttachment,
                                                  String fileName){
        //先查看数据库中是否存在该条数据,如果存在则不做任何操作，如果不存在则插入该条数据
        SQLiteDatabase dbRead = dao.getReadableDatabase();
        SQLiteDatabase dbWrite = dao.getWritableDatabase();
//        Cursor cursor = dbRead.query("DepartmentInformationTable",null,null,null,null,null,null);
//        if (cursor.getCount() != 0){
//            dbWrite.execSQL("delete from DepartmentInformationTable");
//        }
        //若无数据则进行数据插入
        ContentValues values = new ContentValues();
        values.put("id", depid);
        values.put("mainTitle", mainTitle);
        values.put("depPeople", depPeople);
        values.put("departName", departName);
        values.put("depDate", depDate);
        values.put("depAttachment", depAttachment);
        values.put("fileName",fileName);
        dbWrite.insert("DepartmentInformationTable", null, values);
//        cursor.close();
    }

    public String getName(String id){
        SQLiteDatabase dbRead = dao.getReadableDatabase();
        String fileName = "";
        Cursor cursor = dbRead.rawQuery( "select * from DepartmentInformationTable where id  = ? ",new String[]{id} );
        if(cursor.moveToNext()){
            fileName = cursor.getString(cursor.getColumnIndex("FileName"));
        }
        return fileName;
    }
}
