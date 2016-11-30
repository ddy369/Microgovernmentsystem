package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.operations;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.GetUserInformationDao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tasks.FeedBackWorkerTask;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.GetSignTool;


public class FeedBackOpreation {

    private Context mcontext;
    private String text;

    public FeedBackOpreation(Context context,String text){
        this.mcontext = context;
        this.text = text;
    }

    GetUserInformationDao getUserInformationDao = new GetUserInformationDao();

    public void feedBack(){
        new FeedBackWorkerTask(new FeedBackWorkerTask.AsyncWork() {
            Toast toast;

            @Override
            public void preExecute() {
                toast = Toast.makeText(mcontext,"正在反馈，请稍候...",Toast.LENGTH_SHORT);
                toast.show();
            }

            @Override
            public void postWork(Integer integer) {
                if (integer == 0) {
                    toast.cancel();
                    Toast.makeText(mcontext, "成功反馈", Toast.LENGTH_SHORT).show();
                } else if (integer == 1) {
                    toast.cancel();
                    Toast.makeText(mcontext, "无效的用户id，请重新登录", Toast.LENGTH_SHORT).show();
                } else if (integer == 2) {
                    toast.cancel();
                    Toast.makeText(mcontext, "获取机器码错误，请重新登录", Toast.LENGTH_SHORT).show();
                } else if (integer == -2) {
                    toast.cancel();
                    Toast.makeText(mcontext, "未知错误", Toast.LENGTH_SHORT).show();
                } else {
                    toast.cancel();
                    Toast.makeText(mcontext, "网络连接超时,反馈信息失败...", Toast.LENGTH_SHORT).show();
                }
            }
        }).execute(getUid(),getTimeStamp(),getMac(),getSign(),text);
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
        map.put("return_content",text);
        map.put("timestamp",getTimeStamp());
        GetSignTool getSignTool = new GetSignTool();
        return getSignTool.getSign(map);
    }
}
