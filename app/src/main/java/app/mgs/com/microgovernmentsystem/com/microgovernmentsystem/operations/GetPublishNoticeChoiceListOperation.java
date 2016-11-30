package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.operations;


import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.GetUserInformationDao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tasks.PublishNoticeChoiceListWorkerTask;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.GetSignTool;

public class GetPublishNoticeChoiceListOperation {
    private Context mcontext;
    private String flag;

    public GetPublishNoticeChoiceListOperation(Context context,String flag){
        this.mcontext = context;
        this.flag = flag;
    }

    //接口
    public interface TaskWork{
        void onPostWork();
    }

    GetUserInformationDao getUserInformationDao = new GetUserInformationDao();

    public void setPublishNoticeChoiceList(final TaskWork taskWork){
        new PublishNoticeChoiceListWorkerTask(new PublishNoticeChoiceListWorkerTask.AsyncWork(){
//            Toast toast;

            @Override
            public void preExcute() {
//                toast = Toast.makeText(mcontext,"正在联网获取部门和通知人列表，请稍后点击添加...",Toast.LENGTH_SHORT);
//                toast.show();
            }

            @Override
            public void postWork(Integer integer) {
                Log.i("integer3:"," "+integer);
                if (integer == 0){
//                    toast.cancel();
//                    Toast.makeText(mcontext, "获取列表成功！", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(mcontext, "获取部门与联系人列表失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                }
            }
        }).execute(getUid(),getMac(),getSign(),getTimeStamp(),flag);
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
