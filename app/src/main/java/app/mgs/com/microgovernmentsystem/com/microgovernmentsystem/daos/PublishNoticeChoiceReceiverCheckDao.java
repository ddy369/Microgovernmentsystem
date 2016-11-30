package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.PublishNoticeChoiceDepartmentActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Values;

public class PublishNoticeChoiceReceiverCheckDao  {

    private Context context;

    public PublishNoticeChoiceReceiverCheckDao(Context context){
        this.context = context;
    }

    /**
     * 以下部分是点击checkbox时对数据的更新操作
     * 1.若是选中checkbox，则置isChecked的值为1
     * 2.若是取消checkbox，则置isChecked的值为0
     */
    public void setChecked(int flag, String id){
        Dao dao = new Dao(context,null, Values.database_version);
        SQLiteDatabase dbWrite = dao.getWritableDatabase();

        //若flag传来的值为0，则表示checkbox变成未选中状态，反之则为选中
        if (flag==0){
            ContentValues values = new ContentValues();
            values.put("isChoice","0");
            dbWrite.update("ReceiverTable",values,"receiverId=?",new String[]{ id });
        }else {
            ContentValues values = new ContentValues();
            values.put("isChoice","1");
            dbWrite.update("ReceiverTable",values,"receiverId=?",new String[]{ id });
        }
    }
}
