package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.DepartmentInformationListActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.PublishNoticeActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Values;

public class SetPublishNoticeDao {

    Dao dao;
    SQLiteDatabase dbRead;
    SQLiteDatabase dbWrite;

    public SetPublishNoticeDao(String flag){
        /**
         * 在初始化数据库方法的时候清空一下表
         *
         */
        if (flag.equals("1")){
            dao = new Dao(DepartmentInformationListActivity.activity,null, Values.database_version);
        }else {
            dao = new Dao(PublishNoticeActivity.activity, null, Values.database_version);
        }
        dbRead= dao.getReadableDatabase();
        dbWrite = dao.getWritableDatabase();
//        clearTable("DepartmentTable");
//        clearTable("ReceiverTable");
    }

    /**
     * 将部门信息存入数据库
     */
    public void setDepartmentList(String departmentId,String departmentName){
        //将原来的清空表改为插入时检查是否存在此部门，如果不存在则插入，如果存在则不做任何改动
        Cursor cursor = dbRead.query("DepartmentTable",null,"departmentId=?",new String[]{departmentId},null,null,null);
        if (cursor.getCount()==0){
            ContentValues values = new ContentValues();
            values.put("departmentId", departmentId);
            values.put("departmentName", departmentName);

            dbWrite.insert("DepartmentTable", null, values);
        }
        cursor.close();
    }

    /**
     * 将接收人信息存入数据库
     */
    public void setReceiver(String receiverId,String receiverName,String departmentId,String orderNum){
        //将原来的清空表改为插入时检查是否存在此人，如果不存在则插入，如果存在则不做任何改动
        //增加了一个人同属于多个部门的情况
        Cursor cursor = dbRead.rawQuery("select * from ReceiverTable where receiverId=? and departmentId=?",
                new String[]{receiverId,departmentId});
        if (cursor.getCount()==0){
            ContentValues values = new ContentValues();
            values.put("departmentId", departmentId);
            values.put("receiverId", receiverId);
            values.put("receiverName",receiverName);
            values.put("orderNum",orderNum);
            dbWrite.insert("ReceiverTable", null, values);
        }
        cursor.close();
    }

    /**
     * 查询数据库表，如果表不为空则清空表存储新数据
     */
//    private void clearTable(String tableName){
//        Cursor cursor;
//        cursor = dbRead.query(tableName,null,null,null,null,null,null);
//        if (cursor.getCount()!=0){
//            dbWrite.delete(tableName,null,null);
//        }
//        cursor.close();
//    }
}
