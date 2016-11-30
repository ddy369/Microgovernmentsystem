package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.MagazineActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Values;

/**
 * Created by Administrator on 2016-08-22.
 */
public class SetMagazineDao {

    Dao dao = new Dao(MagazineActivity.activity,null, Values.database_version);

    public void doMangazineOperation(String[] str) {
        SQLiteDatabase dbRead = dao.getReadableDatabase();
        Cursor cursor = dbRead.rawQuery(" select * from MagazineDetail where id = ?", new String[]{str[0]});
        if (cursor.getCount() == 0) {
            //dbRead.execSQL("delete from DocumentDetail where id = ? ",new String[]{str[13]});
            dbRead.execSQL(" update MagazineTable set read = ? where id  = ?  ", new String[]{"1", str[0]});
            dbRead.execSQL("insert into MagazineDetail ( id,title,magNum,magDepart,updateTime,subject ) values (?,?,?,?,?,?)", str);
        }
        cursor.close();
    }

    public String getID(String id,Context context){
        Dao dao = new Dao(context,null, Values.database_version);
        SQLiteDatabase dbReads = dao.getReadableDatabase();
        String idArray = id;
        Cursor cursor = dbReads.rawQuery("select * from MagazineTable where id = ? ",new String[] { id } );
        if (cursor.moveToFirst()) {
            do {
                idArray = cursor.getString(cursor.getColumnIndex("sysId"));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return idArray;
    }
    public String getTitle(String id,Context context){
        Dao dao = new Dao(context,null, Values.database_version);
        SQLiteDatabase dbReads = dao.getReadableDatabase();
        String idArray = id;
        Cursor cursor = dbReads.rawQuery("select * from MagazineTable where id = ? ",new String[] { id } );
        if (cursor.moveToFirst()) {
            do {
                idArray = cursor.getString(cursor.getColumnIndex("year_no"));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return idArray;
    }
}