package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.operations;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.GetUserInformationDao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.NoticeListRecordIdDao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.ListBean;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tasks.NoticeListWorkerTask;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tasks.NoticeWorkerTask;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.GetSignTool;

public class NoticeListOperation  {

    private Context mcontext;
    private int mStatus;
    public ArrayList<ListBean> arrayList = new ArrayList<>();

    public NoticeListOperation(Context context){
        this.mcontext = context;
    }

    //接口
    public interface TaskWork{
        void onPostWork();
        void onPostWorkFoot();
    }

    GetUserInformationDao getUserInformationDao = new GetUserInformationDao();

    public void setNoticeList(int pageNum, int status, final TaskWork taskWork){
        mStatus = status;
        NoticeListWorkerTask workerTask = new NoticeListWorkerTask(new NoticeListWorkerTask.AsyncWork(){
//            Toast toast;

            @Override
            public void preExcute() {
//                toast = Toast.makeText(mcontext,"正在获取通知列表，请稍候...",Toast.LENGTH_SHORT);
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
                }else if (integer == 3){
                    Toast.makeText(mcontext, "没有更多通知了", Toast.LENGTH_SHORT).show();
                }else {
//                    toast.cancel();
                    Toast.makeText(mcontext, "网络连接超时，下拉刷新以重新获取列表", Toast.LENGTH_SHORT).show();
                }
                taskWork.onPostWorkFoot();
            }
        });
        workerTask.execute(getUid(),getMac(),getSign(pageNum),getTimeStamp(), String.valueOf(pageNum), String.valueOf(status));
        if (status!=0){
            arrayList =  workerTask.getArrayList();
        }
    }

    public ArrayList<ListBean> getAL(){
        return arrayList;
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
        if (mStatus!=0){
            map.put("status",String.valueOf(mStatus));
        }
        GetSignTool getSignTool = new GetSignTool();
        return getSignTool.getSign(map);
    }


}
