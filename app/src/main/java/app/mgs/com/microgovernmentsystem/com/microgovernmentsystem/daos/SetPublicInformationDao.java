package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.PublicInformationActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Values;

/**
 * Created by Administrator on 2016/8/26.
 */
public class SetPublicInformationDao {
    Dao dao = new Dao(PublicInformationActivity.activity,null, Values.database_version);
    public void daoPublicInformationOperation(String pubid,
                                              String infoPeople,
                                              String infoDate,
                                              String field,
                                              String infotitle,
                                              String infocContent,
                                              String infoSecret,
                                              String infoVideoAddress,
                                              String infoPictureFile){
        //先查看数据库中是否存在该条数据,如果存在则不做任何操作，如果不存在则插入该条数据
        SQLiteDatabase dbRead = dao.getReadableDatabase();
        SQLiteDatabase dbWrite = dao.getWritableDatabase();
        Cursor cursor = dbRead.query("PublicInformationTable",null,null,null,null,null,null);
        if (cursor.getCount() != 0){
            dbWrite.execSQL("delete from PublicInformationTable");
        }
            //若无数据则进行数据插入
        ContentValues values = new ContentValues();
        values.put("id", pubid);
        values.put("infoPeople", infoPeople);
        values.put("infoDate", infoDate);
        values.put("field", field);
        values.put("infotitle", infotitle);
        values.put("infocContent", infocContent);
        values.put("infoSecret", infoSecret);
        values.put("infoVideoAddress", infoVideoAddress);
        values.put("infoPictureFile", infoPictureFile);
        dbWrite.insert("PublicInformationTable", null, values);
        cursor.close();
    }
}
