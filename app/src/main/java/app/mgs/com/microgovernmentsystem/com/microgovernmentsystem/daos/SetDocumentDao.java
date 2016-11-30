package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import org.json.JSONObject;

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.DocumentActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Values;

/**
 * Created by dingyi on 2016-08-19.
 */
public class SetDocumentDao {
    Dao dao = new Dao(DocumentActivity.activity, null, Values.database_version);

    public void doDocumentOperation(String[] str) {
        SQLiteDatabase dbRead = dao.getReadableDatabase();
//        Cursor cursor = dbRead.rawQuery(" select * from DocumentDetail where id = ?", new String[]{str[13]});
        dbRead.execSQL(" update DocumentTable set read = ? where id  = ?  ", new String[]{"1", str[13]});
        String[] s = new String[14];
        for(int i= 0;i<=13;i++){
            s[i] = str[i];
        }
        dbRead.execSQL("insert into DocumentDetail ( mainTitle,docNumber,docPeople,signPeople,docSignDate,docPrintDate,title," +
                "docSecret,docUrgent,docContent,docAttachment,docMark,docExplain,id  ) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)",s );
        dbRead.execSQL("insert into DocumentFile (id ,fileName,filePath,parentId ) values(?,?,?,?) " , new String[]{str[14],str[15],str[16],str[13]});
    }


    public int selectCount(String id) {
        SQLiteDatabase dbRead = dao.getReadableDatabase();
        Cursor cursor = dbRead.rawQuery(" select * from DocumentDetail where id = ?",new String[]{id});
        int i = cursor.getCount();
        cursor.close();
        return i;
    }
    public int selectCount(String id,String purview) {
        int readFlag = 0;
        SQLiteDatabase dbRead = dao.getReadableDatabase();
        Cursor cursor = dbRead.rawQuery(" select * from DocumentTable where id = ? and purview = ? ",new String[]{id,purview});
        if(cursor.moveToNext()){
            readFlag = cursor.getInt(cursor.getColumnIndex("read"));
        }
        cursor.close();
        return readFlag;
    }

    public String[] attachment(String id){
        String[] str = new String[3];
        SQLiteDatabase dbRead = dao.getReadableDatabase();
        Cursor cursor = dbRead.rawQuery(" select * from DocumentFile where parentId = ?",new String[]{id});
        if (cursor.moveToNext()) {
            str[0] = cursor.getString(cursor.getColumnIndex("id"));
            str[1]= cursor.getString(cursor.getColumnIndex("fileName"));
            str[2]= cursor.getString(cursor.getColumnIndex("filePath"));
        }
        cursor.close();
        return str;
    }
}

