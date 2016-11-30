package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.NoticeActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.NoticeListActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.Dao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.ListBeanAttachment;

public class NoticeDatahelper {

    public void getNotice(TextView tvTitle,
                          TextView tvSender,
                          TextView tvSendTime,
                          TextView tvDepartment,
                          TextView tvMessage,
                          TextView tvReceiver,
                          TextView tvReceipt,
                        //  String tvContent,
                          String recordid) {
        Log.d("recordid-szy", "getNotice: "+recordid);
        Dao dao = new Dao(NoticeActivity.activity, null, Values.database_version);
        SQLiteDatabase dbRead = dao.getReadableDatabase();
        Cursor cursor = dbRead.query("NOTICETABLE", null,"recordid=?",new String[]{recordid},null,null,null);
        if (cursor.moveToNext()) {
            tvTitle.setText(cursor.getString(cursor.getColumnIndex("title")));
            tvSender.setText(cursor.getString(cursor.getColumnIndex("sender")));
            tvSendTime.setText(cursor.getString(cursor.getColumnIndex("time")));
         //   tvContent=cursor.getString(cursor.getColumnIndex("content"));
            if (cursor.getString(cursor.getColumnIndex("organizelist")).equals("null")){
                tvDepartment.setText("");
            }else {
                tvDepartment.setText(cursor.getString(cursor.getColumnIndex("organizelist")));
            }
            if (cursor.getString(cursor.getColumnIndex("peoplelist")).equals("null")){
                tvReceiver.setText("");
            }else {
                tvReceiver.setText(cursor.getString(cursor.getColumnIndex("peoplelist")));
            }
            if (cursor.getString(cursor.getColumnIndex("isreturn")).equals("false")) {
                tvReceipt.setText("不需要");
            } else {
                tvReceipt.setText("需要");
            }
            if (cursor.getString(cursor.getColumnIndex("sendmessage")).equals("false")) {
                tvMessage.setText("不需要");
            } else {
                tvMessage.setText("需要");
            }
        }
        cursor.close();
    }
    public String gethtml(String recordid)
    {
        Dao dao = new Dao(NoticeActivity.activity, null, Values.database_version);
        SQLiteDatabase dbRead = dao.getReadableDatabase();
        Cursor cursor = dbRead.query("NOTICETABLE", null,"recordid=?",new String[]{recordid},null,null,null);
        String tem="";
        if (cursor.moveToNext()) {
            tem=cursor.getString(cursor.getColumnIndex("content"));
        }
        return tem;
    }
    public void getAttachment(List<ListBeanAttachment> Attachmentlist, String recordid,
                              ImageButton imageButton, TextView textView) {
        Dao dao = new Dao(NoticeActivity.activity, null, Values.database_version);
        SQLiteDatabase dbRead = dao.getReadableDatabase();
        Cursor cursor = dbRead.query("NoticeAttachmentTable",null,"recordid=?",new String[]{recordid},null,null,null);
        if (cursor.getCount()==0){
            imageButton.setVisibility(View.INVISIBLE);
            textView.setVisibility(View.INVISIBLE);
        }
        while (cursor.moveToNext()) {
            ListBeanAttachment listBeanAttachment = new ListBeanAttachment( cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getString(cursor.getColumnIndex("size")),
                    cursor.getString(cursor.getColumnIndex("url")));
                    Attachmentlist.add(listBeanAttachment);
//            Attachmentlist.add(String.format("%s %s %s",
//                    cursor.getString(cursor.getColumnIndex("name")),
//                    cursor.getString(cursor.getColumnIndex("size")),
//                    cursor.getString(cursor.getColumnIndex("url"))));
            Log.d("recordid-szy", String.format("%s %s",
                    cursor.getString(cursor.getColumnIndex("name")), cursor.getString(cursor.getColumnIndex("size"))));
        }
        cursor.close();
    }

    public void setRead(String id){
        Dao dao = new Dao(NoticeActivity.activity, null, Values.database_version);
        SQLiteDatabase dbRead = dao.getReadableDatabase();
        SQLiteDatabase dbWrite = dao.getWritableDatabase();
        String sql = "select * from NOTICELISTTABLE where recordid='"+id+"'";
        Cursor cursor = dbRead.rawQuery(sql,null);
        if (cursor.getCount() > 0){
            dbWrite.execSQL("update NOTICELISTTABLE set read=1 where recordid='"+id+"'");
        }
        cursor.close();
    }

    public void setAllRead(){
        Dao dao = new Dao(NoticeListActivity.activity, null, Values.database_version);
        SQLiteDatabase dbRead1 = dao.getReadableDatabase();
        SQLiteDatabase dbWrite1 = dao.getWritableDatabase();
        Cursor cursor = dbRead1.query("NOTICELISTTABLE", null, null , null, null, null, null);
        if (cursor.moveToNext()){
            String sql = "update NOTICELISTTABLE set read=1";
            dbWrite1.execSQL(sql);
        }
        cursor.close();
    }

}
