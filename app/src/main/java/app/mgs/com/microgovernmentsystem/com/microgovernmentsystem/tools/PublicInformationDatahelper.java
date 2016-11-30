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

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.PublicInformationActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.PublicInformationListActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.Dao;

import static android.os.Environment.getExternalStorageDirectory;


public class PublicInformationDatahelper {
    public void getPublicInformation(TextView tvInfoTitle,
                                     TextView tvInfoPeople,
                                     TextView tvInfoDate,
                                     TextView tvInfoSecret,
                                     TextView tvField,
                                     TextView tvVideo,
                                     TextView tvPicture,
                                     WebView webView) {
        Dao dao = new Dao(PublicInformationActivity.activity, null, Values.database_version);
        SQLiteDatabase dbRead = dao.getReadableDatabase();
        Cursor cursor = dbRead.query("PublicInformationTable", null, null, null, null, null, null);
        if (cursor.moveToNext()) {
            tvInfoTitle.setText(cursor.getString(cursor.getColumnIndex("infotitle")));
            tvInfoPeople.setText(cursor.getString(cursor.getColumnIndex("infoPeople")));
            tvInfoDate.setText(cursor.getString(cursor.getColumnIndex("infoDate")));
            tvInfoSecret.setText(cursor.getString(cursor.getColumnIndex("infoSecret")));
            tvField.setText(cursor.getString(cursor.getColumnIndex("field")));
            tvVideo.setText(cursor.getString(cursor.getColumnIndex("infoVideoAddress")));
            tvPicture.setText(cursor.getString(cursor.getColumnIndex("infoPictureFile")));
            try {
//                String file = "/storage/emulated/0/gsDownload/"+cursor.getString(cursor.getColumnIndex("id"))+".html";//.replace("/","\\");
                String file = getExternalStorageDirectory()+"/gsDownload/"+cursor.getString(cursor.getColumnIndex("id"))+".html";//.replace("/","\\");
                List<String> lines=new ArrayList<>();
                BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(file),"GB2312"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    lines.add(line);
                }
                br.close();
                webView.loadDataWithBaseURL(null, lines.toString().replace("[","").replace("]","").replace(",",""), "text/html", "utf-8", null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        cursor.close();
    }
    public void setRead(String id){
        Dao dao = new Dao(PublicInformationListActivity.activity, null, Values.database_version);
        SQLiteDatabase dbRead = dao.getReadableDatabase();
        SQLiteDatabase dbWrite = dao.getWritableDatabase();
        String sql = "select * from PublicInformationListTable where id='"+id+"'";
        Cursor cursor = dbRead.rawQuery(sql,null);
        if (cursor.getCount() > 0){
            dbWrite.execSQL("update PublicInformationListTable set read=1 where id='"+id+"'");
        }
        cursor.close();
    }
    public void recordReadStatus(String id){
        Dao dao1 = new Dao(PublicInformationActivity.activity,null, Values.database_version);
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

//    public void setAllRead(){
//        Dao dao = new Dao(PublicInformationListActivity.activity, null, Values.database_version);
//        SQLiteDatabase dbRead1 = dao.getReadableDatabase();
//        SQLiteDatabase dbWrite1 = dao.getWritableDatabase();
//        Cursor cursor = dbRead1.query("PublicInformationListTable", null, null , null, null, null, null);
//        if (cursor.moveToNext()){
//            String sql = "update PublicInformationListTable set read=1";
//            dbWrite1.execSQL(sql);
//        }
//    }
}
