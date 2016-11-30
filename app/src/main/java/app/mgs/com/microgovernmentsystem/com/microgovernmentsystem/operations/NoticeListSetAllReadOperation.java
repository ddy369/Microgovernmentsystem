package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.operations;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.GetUserInformationDao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tasks.NoticeListSetAllReadWorkerTask;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.GetSignTool;


public class NoticeListSetAllReadOperation {
    private Context mcontext;

    public NoticeListSetAllReadOperation(Context context){
        this.mcontext = context;
    }

    //接口
    public interface TaskWork{
        void onPostWork();
    }

    GetUserInformationDao getUserInformationDao = new GetUserInformationDao();

    public void NoticeListSetAllRead(final TaskWork taskWork){
        new NoticeListSetAllReadWorkerTask(new NoticeListSetAllReadWorkerTask.AsyncWork(){

            @Override
            public void preExcute() {
            }

            @Override
            public void postWork(Integer integer) {
                Log.i("integeraa:"," "+integer);
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
                }else {
//                    toast.cancel();
                    Toast.makeText(mcontext, "网络连接超时，全标记已读失败", Toast.LENGTH_SHORT).show();
                }
            }
        }).execute(getUid(),getMac(),getSign(),getTimeStamp());
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

    private String getSign(){
        HashMap<String , String> map = new HashMap<>();
        map.put("mac",getMac());
        map.put("uid",getUid());
        map.put("timestamp",getTimeStamp());
        GetSignTool getSignTool = new GetSignTool();
        return getSignTool.getSign(map);
    }
}
