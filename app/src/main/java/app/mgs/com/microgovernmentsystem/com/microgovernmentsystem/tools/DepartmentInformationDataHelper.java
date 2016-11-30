package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.webkit.WebView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.DepartmentInformationActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.DepartmentInformationListActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.PublicInformationActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.Dao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.SetDepartmentInformationDao;

import static android.os.Environment.getExternalStorageDirectory;

public class DepartmentInformationDataHelper {
    public void getPublicInformation(TextView tvmainTitle,
                                     TextView tvdepPeople,
                                     TextView tvdepDate,
                                     TextView tvdepartName,
                                     TextView tvdepAttachment,
                                     WebView webview,
                                     String id) {
        Dao dao = new Dao(DepartmentInformationActivity.activity, null, Values.database_version);
        SQLiteDatabase dbRead = dao.getReadableDatabase();
        Cursor cursor = dbRead.rawQuery("select * from DepartmentInformationTable where id = ? ", new String[]{id});
        if (cursor.moveToNext()) {
            tvmainTitle.setText(cursor.getString(cursor.getColumnIndex("mainTitle")));
            tvdepPeople.setText(cursor.getString(cursor.getColumnIndex("depPeople")));
            tvdepDate.setText(cursor.getString(cursor.getColumnIndex("depDate")));
            tvdepartName.setText(cursor.getString(cursor.getColumnIndex("departName")));
            SetDepartmentInformationDao departmentInformationDao = new SetDepartmentInformationDao();
            String s = departmentInformationDao.getName(cursor.getString(cursor.getColumnIndex("id")));
            tvdepAttachment.setText(s);
            try {
//                String files = "/storage/emulated/0/gsDownload/"+cursor.getString(cursor.getColumnIndex("id"))+".html";//.replace("/","\\");
                String file = getExternalStorageDirectory()+"/gsDownload/"+cursor.getString(cursor.getColumnIndex("id"))+".html";//.replace("/","\\");
                BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(file),"GB2312"));
                List<String> lines=new ArrayList<>();
                String line = null;
                while ((line = br.readLine()) != null) {
                    lines.add(line);
                }
                br.close();
                //webview.loadDataWithBaseURL(null, lines.toString(), "text/html", "utf-8", null);
                webview.loadDataWithBaseURL(null, lines.toString().replace("[","").replace("]","")+ "<p>文件名："+s+" <br />具体内容详情见附件</p>", "text/html", "utf-8", null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        cursor.close();
    }
    public void setRead(String id){
        Dao dao = new Dao(DepartmentInformationListActivity.activity, null, Values.database_version);
        SQLiteDatabase dbRead = dao.getReadableDatabase();
        SQLiteDatabase dbWrite = dao.getWritableDatabase();
        String sql = "select * from DepartmentInformationListTable where id='"+id+"'";
        Cursor cursor = dbRead.rawQuery(sql,null);
        if (cursor.getCount() > 0){
            dbWrite.execSQL("update DepartmentInformationListTable set read=1 where id='"+id+"'");
        }
        cursor.close();
    }

    public void setToolTitle(String id,TextView tvTitle){
        Dao dao = new Dao(DepartmentInformationListActivity.activity, null, Values.database_version);
        SQLiteDatabase dbRead = dao.getReadableDatabase();
        String sql = "select title from DepartmentInformationListTable where id='"+id+"'";
        Cursor cursor = dbRead.rawQuery(sql,null);
        if (cursor.moveToNext()) {
            tvTitle.setText(cursor.getString(cursor.getColumnIndex("title")));
        }
        cursor.close();
    }
    public void recordReadStatus(String id){
        Dao dao1 = new Dao(DepartmentInformationActivity.activity,null, Values.database_version);
        SQLiteDatabase dbRead = dao1.getReadableDatabase();
        SQLiteDatabase dbWrite = dao1.getWritableDatabase();
        Cursor cursor = dbRead.query("ReadStatusTable",null,"id=?",new String[]{id},null,null,null);
        int i = cursor.getCount();
        Log.i("i:"," "+i);
        if(i==0){
            ContentValues values = new ContentValues();
            values.put("id", id);
            long o= dbWrite.insert("ReadStatusTable",null,values);
//            Cursor cursor1 = dbRead.query("ReadStatusTable",null,"id=?",new String[]{ id },null,null,null);
//            int j =cursor1.getCount();
//            Log.i("00000000j:"," "+j+" "+o);
//            if(j!=0){
//                Log.i("00000000","已插入");
//            }
//            cursor1.close();
            cursor.close();
        }
    }
}
