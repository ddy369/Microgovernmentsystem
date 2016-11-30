package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.util.Map;

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.DocumentActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.Dao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.SetDocumentDao;

/**
 * Created by dingyi on 2016-08-18.
 */
public class DocumentDataHelper {
    Dao dao = new Dao(DocumentActivity.activity, null, Values.database_version);

    private String attachmentURL;
    private String attachmentName;

    public String getAttachmentURL(){
        return attachmentURL;
    }
    public String getAttachmentName(){
        return attachmentName;
    }

    public String  getURL(String id){
        SQLiteDatabase dbRead = dao.getReadableDatabase();
        Cursor cursor = dbRead.rawQuery("select * from DocumentDetail where id = ? ",new String[] { id } );
        if (cursor.moveToFirst()) {
            do {
                attachmentURL = cursor.getString(cursor.getColumnIndex("docAttachment"));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return attachmentURL;
    }

    public void getDocument( TextView mtvDocumentTitle,
                             TextView mtvNumber,
                             TextView mtvSender,
                             TextView mtvCC,
                             TextView mtvSignDate,
                             TextView mtvPrintDate,
                             TextView mtvTitle,
                             TextView mtvDense,
                             TextView mtvUrgency,
                             TextView mtvText,
                             TextView mtvAttachment,
                             TextView mtvRemarks,
                             TextView mtvExplain,String id ) {
        SQLiteDatabase dbRead = dao.getReadableDatabase();
        Cursor cursor = dbRead.rawQuery("select * from DocumentDetail where id = ? ",new String[] { id } );
        SetDocumentDao setDocumentDao = new SetDocumentDao();
        String[] str = setDocumentDao.attachment(id);
        if (cursor.moveToFirst()) {
            do {
                mtvDocumentTitle.setText(cursor.getString(cursor.getColumnIndex("mainTitle")));
                mtvNumber.setText(cursor.getString(cursor.getColumnIndex("docNumber")));
                mtvSender.setText(cursor.getString(cursor.getColumnIndex("docPeople")));
                mtvCC.setText(cursor.getString(cursor.getColumnIndex("signPeople")));
                mtvSignDate.setText(cursor.getString(cursor.getColumnIndex("docSignDate")));
                mtvPrintDate.setText(cursor.getString(cursor.getColumnIndex("docPrintDate")));
                mtvTitle.setText(cursor.getString(cursor.getColumnIndex("title")));
                mtvDense.setText(cursor.getString(cursor.getColumnIndex("docSecret")));
                mtvUrgency.setText(cursor.getString(cursor.getColumnIndex("docUrgent")));
//                mtvText.setText(cursor.getString(cursor.getColumnIndex("docContent")));
                mtvText.setText(str[1].substring(0,8)+"..."+str[1].substring(str[1].length()-8,str[1].length()));
                attachmentName = cursor.getString(cursor.getColumnIndex("docAttachmentName"));
                String s = cursor.getString(cursor.getColumnIndex("docAttachment"));
                if(s.length() > 0){
                    mtvAttachment.setText("附件下载");
                }
                attachmentURL = cursor.getString(cursor.getColumnIndex("docAttachment"));
                mtvRemarks.setText(cursor.getString(cursor.getColumnIndex("docMark")));
                mtvExplain.setText(cursor.getString(cursor.getColumnIndex("docExplain")));
            }while (cursor.moveToNext());
        }
        cursor.close();
    }

}
