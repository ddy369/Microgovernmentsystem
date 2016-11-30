package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.operations;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.GetUserInformationDao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.ListBeanDocument;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tasks.DocumentListWorkerTask;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.GetSignTool;

/**
 * Created by Administrator on 2016-08-13.
 */
public class DocumentListOperation {
    private Context mcontext;
    private String  status;

    public ArrayList<ListBeanDocument> arrayList = new ArrayList<>();
    public DocumentListOperation(Context context){
        this.mcontext = context;
    }

    //接口
    public interface TaskWork{
        void onPostWork();
        void onPostWorkFoot();
    }

    GetUserInformationDao getUserInformationDao = new GetUserInformationDao();

    public void setDocumentList(int pageNum,String mStatus,  final TaskWork taskWork){
        status = mStatus;
        DocumentListWorkerTask docWorkerTask = new DocumentListWorkerTask(new DocumentListWorkerTask.AsyncWork(){
            @Override
            public void preExcute() {
//                toast = Toast.makeText(mcontext,"正在获取公文列表，请稍候...",Toast.LENGTH_SHORT);
//                toast.show();
            }

            @Override
            public void postWork(Integer integer) {
                Log.i("integer1:"," "+integer);
                if (integer == 0){
                    taskWork.onPostWork();
                }else if (integer == 1){
//                    toast.cancel();
                    Toast.makeText(mcontext, "无效的用户id，请重新登录", Toast.LENGTH_SHORT).show();
                }else if (integer == 2){
//                    toast.cancel();
                    Toast.makeText(mcontext, "获取机器码错误，请重新登录", Toast.LENGTH_SHORT).show();
                }else if (integer == -2){
//                    toast.cancel();
                    Toast.makeText(mcontext, "未知错误", Toast.LENGTH_SHORT).show();
                }else if (integer==3){
//                    toast.cancel();
                    Toast.makeText(mcontext, "没有更多公文了", Toast.LENGTH_SHORT).show();
                }
                else {
//                    toast.cancel();
                    Toast.makeText(mcontext, "网络连接超时，下拉刷新以重新获取列表", Toast.LENGTH_SHORT).show();
                }
                taskWork.onPostWorkFoot();
            }
        });
//        .execute(getUid(),getMac(),getSign(pageNum),getTimeStamp(), String.valueOf(pageNum),mStatus)
        docWorkerTask.execute(getUid(),getMac(),getSign(pageNum),getTimeStamp(), String.valueOf(pageNum),mStatus);
        if (!status.equals("2")){
            arrayList =  docWorkerTask.getArrayList();
        }
    }

    private String getUid(){
        return getUserInformationDao.getUid();
    }

    private String getMac(){
        return getUserInformationDao.getMac();
    }

    private String getTimeStamp(){
        return getUserInformationDao.getTimeStamp();
    }

    private String getSign(int pageNum){
        HashMap<String , String> map = new HashMap<>();
        map.put("mac",getMac());
        map.put("uid",getUid());
        map.put("page_size","15");
        map.put("page_number", String.valueOf(pageNum));
        map.put("timestamp",getTimeStamp());
        map.put("doc_status",status);
        GetSignTool getSignTool = new GetSignTool();
        return getSignTool.getSign(map);
    }
    public ArrayList<ListBeanDocument> getAL(){
        return arrayList;
    }
}
