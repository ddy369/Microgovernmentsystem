package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.PublishNoticeChoiceDepartmentActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.Dao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.ListBeanPNLDepartment;


public class PublishNoticeChoiceDepartmentDataHelper {

    Dao dao = new Dao(PublishNoticeChoiceDepartmentActivity.activity,null, Values.database_version);
    SQLiteDatabase dbRead = dao.getReadableDatabase();
    SQLiteDatabase dbWrite = dao.getWritableDatabase();

    ArrayList<ListBeanPNLDepartment> listBeans = new ArrayList<>();

    /**
     * 以下部分的功能是添加列表内容
     */
    public ArrayList<ListBeanPNLDepartment> setListBeans(){
        //连接数据库查询列表中所需的数据
        Cursor cursor = dbRead.query("DepartmentTable", null,null,null,null,null,null);
        Log.i("arrayListBeans","cursor"+cursor.getCount());
        if (cursor.moveToFirst()){
            for (int i = 1; i <= cursor.getCount(); i++){
                String name = cursor.getString(cursor.getColumnIndex("departmentName"));
                String id = cursor.getString(cursor.getColumnIndex("departmentId"));
                int isChoice = cursor.getInt(cursor.getColumnIndex("isChoice"));
                setListBean(id, name, isChoice);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return listBeans;
    }

    private void setListBean(String id, String name, int isChoice){
        ListBeanPNLDepartment listBean = new ListBeanPNLDepartment();
        listBean.setId(id);
        listBean.setName(name);
        listBean.setIsChoice(isChoice);
        listBeans.add(listBean);
    }

    /**
     * 以下部分是点击checkbox时对数据的更新操作
     * 1.若是选中checkbox，则置isChecked的值为1
     * 2.若是取消checkbox，则置isChecked的值为0
     */
    public void setChecked(int flag, String id){
        //若flag传来的值为0，则表示checkbox变成未选中状态，反之则为选中
        if (flag==0){
            ContentValues values = new ContentValues();
            values.put("isChoice","0");
            dbWrite.update("DepartmentTable",values,"departmentId=?",new String[]{ id });
        }else {
            ContentValues values = new ContentValues();
            values.put("isChoice","1");
            dbWrite.update("DepartmentTable",values,"departmentId=?",new String[]{ id });
        }
    }

}
