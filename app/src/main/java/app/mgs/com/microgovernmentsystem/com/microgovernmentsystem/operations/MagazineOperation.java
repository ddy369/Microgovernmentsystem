package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.operations;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.GetUserInformationDao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tasks.MagazineWorkerTask;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.GetSignTool;

/**
 * Created by dingyi on 2016-08-22.
 */
public class MagazineOperation {

    private Context mcontext;

    public MagazineOperation(Context context){
        this.mcontext = context;
    }

    GetUserInformationDao dao = new GetUserInformationDao();

    public interface TaskWork{
        void onPostWork();
    }

    public void SetMagazine(String id , final TaskWork taskWork){
        new MagazineWorkerTask(new MagazineWorkerTask.AsyncWork() {
            Toast toast;
            @Override
            public void preExecute() {
//                toast = Toast.makeText(mcontext,"正在加载报刊内容，请稍候...",Toast.LENGTH_SHORT);
//                toast.show();
//                ProgressDialog progressDialog = new ProgressDialog(mcontext);
//                progressDialog.setTitle("正在加载报刊内容，请稍候...");
//                progressDialog.setCancelable(true);
            }

            @Override
            public void postWork(Integer integer) {
                Log.i("integer2",":"+integer);
                if (integer == 0){
                }else if (integer == 1){
                    Toast.makeText(mcontext, "用户id失效，请重新登录", Toast.LENGTH_SHORT).show();
                }else if (integer == 2){
                    Toast.makeText(mcontext, "无法获取机器码，请重新登录", Toast.LENGTH_SHORT).show();
                }else if (integer == 3){
                    Toast.makeText(mcontext, "无效的文章id", Toast.LENGTH_SHORT).show();
                }else if (integer == -2){
                    Toast.makeText(mcontext, "发生了未知错误", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(mcontext, "请求超时，请检查网络连接", Toast.LENGTH_SHORT).show();
                }
                taskWork.onPostWork();
            }
        }).execute(id,getUid(),getTimeStamp(),getMac(),getSign());
    }

    private String getUid(){
        return dao.getUid();
    }

    private String getTimeStamp(){
        return dao.getTimeStamp();
    }

    private String getMac(){
        return dao.getMac();
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

