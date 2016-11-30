package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;
import android.webkit.WebView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.MagazineActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.MagazineListActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.PublicInformationActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.Dao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.SetMagazineDao;

import static android.os.Environment.getExternalStorageDirectory;

/**
 * Created by dingyi on 2016-08-22.
 */
public class MagazineDataHelper {

    public void getMagazine(Context context,String id,TextView mtvTitle,TextView mtvIssue,
                            TextView mtvDepartment,TextView mtvDate,WebView mwebView,String cont,String sysId,String year_no) {
        Dao dao = new Dao(context,null, Values.database_version);
        SQLiteDatabase database = dao.getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from MagazineTable where id = ?", new String[]{id});
        database.execSQL(" update MagazineTable set read = ? where id  = ?  ", new String[]{"1", id});
        if (cursor.moveToNext()) {
                mtvTitle.setText(cursor.getString(cursor.getColumnIndex("title")));
                mtvIssue.setText(cursor.getString(cursor.getColumnIndex("subject")));
                mtvDepartment.setText(cursor.getString(cursor.getColumnIndex("sendPeople")));
                mtvDate.setText(cursor.getString(cursor.getColumnIndex("updateTime")));
            try {
                  SetMagazineDao setMagazineDao = new SetMagazineDao();
                  String idadd = setMagazineDao.getID(cursor.getString(cursor.getColumnIndex("id")).toString(), context);
                  String titleAdd = setMagazineDao.getTitle(cursor.getString(cursor.getColumnIndex("id")).toString(), context);

                String[] sss = idadd.split(",");//根据“,”区分
                String[] ttt = titleAdd.split(",");
                List<String> lines = new ArrayList<>();
                for (int i = 0; i < sss.length; i++) {
//                    String file = "/storage/emulated/0/gsDownload/" + s + ".html";//.replace("/","\\");
                    String file = getExternalStorageDirectory()+"/gsDownload/"+ sss[i] + ".html";//.replace("/","\\");
                    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "GB2312"));
                    String line;
                    lines.add("<br />"+ttt[i].toString()+"<br />");
                    while ((line = br.readLine()) != null) {
                        lines.add(line);
                    }
                    br.close();
                }
                mwebView.loadDataWithBaseURL(null, lines.toString().replace("[","").replace("]","").replace(",",""), "text/html", "UTF-8", null);
            }catch(FileNotFoundException e){
                e.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
            }
            }else{
                String[] str = cont.split(",");
                mtvTitle.setText(str[0]);
                mtvIssue.setText(str[1]);
                mtvDepartment.setText(str[2]);
                mtvDate.setText(str[3]);
                try {
                    String idadd = sysId;
                    String[] sss = idadd.split(",");//根据“,”区分
                    String[] ttt = year_no.split(",");
                    List<String> lines = new ArrayList<>();
                    for (int i = 0; i < sss.length; i++) {
                        String file = "/storage/emulated/0/gsDownload/" + sss[i] + ".html";//.replace("/","\\");
                        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "GB2312"));
                        String line ;
                        lines.add("<br />"+ttt[i].toString()+"<br />");
                        while ((line = br.readLine()) != null) {
                            lines.add(line);
                        }
                        br.close();
                    }
                    mwebView.loadDataWithBaseURL(null, lines.toString().replace("[","").replace("]",""), "text/html", "utf-8", null);
                }catch(FileNotFoundException e){
                    e.printStackTrace();
                }catch(IOException e){
                    e.printStackTrace();
                }
        }
        cursor.close();

        }
    public void recordReadStatus(Context context,String id){
        Dao dao1 = new Dao(context,null,Values.database_version);
        SQLiteDatabase dbRead = dao1.getReadableDatabase();
        SQLiteDatabase dbWrite = dao1.getWritableDatabase();
        Cursor cursor = dbRead.query("ReadStatusTable",null,"id=?",new String[]{ id },null,null,null);
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

    public int selectCount(String str){
        Dao dao = new Dao(MagazineListActivity.activity,null, Values.database_version);
        SQLiteDatabase database = dao.getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from MagazineTable where id = ?", new String[]{str});
        return cursor.getCount();
    }
}