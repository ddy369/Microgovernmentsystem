package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.Dao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.ListBeanPNCReceiver;

public class PublishNoticeChoiceReceiverDataHelper {

    private Context context;

    public PublishNoticeChoiceReceiverDataHelper(Context context){
        this.context = context;
    }

    public List<String> setParentData(){
        Dao dao = new Dao(context,null, Values.database_version);
        SQLiteDatabase dbRead = dao.getReadableDatabase();

        List<String> parent = new ArrayList<>();

        //应该在此处查询receiver表来防止部门下没有人的情况
        Cursor cursor = dbRead.query(true,"ReceiverTable",new String[]{"departmentId"},null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            for (int i = 0; i < cursor.getCount(); i++){
                String departmentId = cursor.getString(cursor.getColumnIndex("departmentId"));
                Cursor cursor1 = dbRead.query(true,"DepartmentTable",null,
                        "departmentId=?", new String[]{ departmentId },null,null,null,null);
                if (cursor1.moveToNext())
                    parent.add(cursor1.getString(cursor1.getColumnIndex("departmentName")));
                cursor1.close();
                cursor.moveToNext();
            }
        }
        cursor.close();
        return parent;
    }

    public Map<String,ArrayList<ListBeanPNCReceiver>> setChildData(){
        Dao dao = new Dao(context,null, Values.database_version);
        SQLiteDatabase dbRead = dao.getReadableDatabase();

        Map<String,ArrayList<ListBeanPNCReceiver>> map = new HashMap<>();

        Cursor cursor = dbRead.rawQuery("select distinct departmentId from ReceiverTable",null);
        if (cursor.moveToFirst()){
            for (int i = 0; i < cursor.getCount(); i++){

                ArrayList<ListBeanPNCReceiver> list = new ArrayList<>();

                String departmentId = cursor.getString(cursor.getColumnIndex("departmentId"));
                String departmentName = null;

                //查询部门名称
                Cursor cursor2 = dbRead.rawQuery("select * from DepartmentTable " +
                        "where  departmentId='"+departmentId+"'",null);
                if (cursor2.moveToNext()){
                    departmentName = cursor2.getString(cursor2.getColumnIndex("departmentName"));
                }

                //查询部门id并将每个部门下的通知人添加到数组
                Cursor cursor1 = dbRead.rawQuery("select * from ReceiverTable " +
                        "where departmentId='"+departmentId+"' order by orderNum asc",null);
                if (cursor1.moveToFirst()){
                    for (int j = 0; j < cursor1.getCount(); j++){
                        ListBeanPNCReceiver listbean = new ListBeanPNCReceiver();
                        listbean.setName(cursor1.getString(cursor1.getColumnIndex("receiverName")));
                        listbean.setId(cursor1.getString(cursor1.getColumnIndex("receiverId")));
                        listbean.setChoice(cursor1.getInt(cursor1.getColumnIndex("isChoice")));
                        list.add(listbean);
                        cursor1.moveToNext();
                    }
                }
                map.put(departmentName,list);
                cursor2.close();
                cursor1.close();
                cursor.moveToNext();
            }
//            for (int c = 0; c < 18; c++){
//                String name = map.get("担保中心").get(c).getName();
//                Log.i("aaaaaaaaaa",name);
//            }
        }
        cursor.close();
        return map;
    }

    //如果点击组控件，则子控件也全部勾选上
    public void selectGroup(String name,int i){
        /**
         * 首先对通知人表进行操作，如果组控件被勾选，则子控件全部被勾选上
         */
        Dao dao = new Dao(context,null, Values.database_version);
        SQLiteDatabase dbWrite = dao.getWritableDatabase();
        SQLiteDatabase dbRead = dao.getReadableDatabase();

        String id = null;
        Cursor cursor = dbRead.query("DepartmentTable",null,"departmentName=?",new String[]{ name },null,null,null);
        if (cursor.moveToNext())
            id = cursor.getString(cursor.getColumnIndex("departmentId"));
        ContentValues values = new ContentValues();
        if (i==1)
            values.put("isChoice","1");
        else
            values.put("isChoice","0");
        dbWrite.update("ReceiverTable",values,"departmentId=?",new String[]{ id });
        cursor.close();

        /**
         * 其次对部门表进行勾选，将selected属性置为1
         */
        ContentValues values1 = new ContentValues();
        if (i==1)
            values1.put("selected","1");
        else
            values1.put("selected","0");
        dbWrite.update("DepartmentTable",values1,"departmentId=?",new String[]{ id });
        cursor.close();
    }

    /**
     *
     * @return 返回
     */
//    public int setSelectGroup(){
//        Dao dao = new Dao(context,null, Values.database_version);
//        SQLiteDatabase dbWrite = dao.getWritableDatabase();
//        SQLiteDatabase dbRead = dao.getReadableDatabase();
//
//        Cursor cursor = dbRead.query("DepartmentTable",null,"departmentName=?",new String[]{ name },null,null,null);
//
//
//    }
}
