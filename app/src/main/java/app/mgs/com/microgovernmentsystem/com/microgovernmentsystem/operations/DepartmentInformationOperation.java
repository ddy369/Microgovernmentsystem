package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.operations;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.GetUserInformationDao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tasks.DepartmentInformationWorkerTask;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.GetSignTool;

/**
 * Created by Administrator on 2016/8/29.
 */
public class DepartmentInformationOperation {

    private Context mcontext;

    public DepartmentInformationOperation(Context context){
        this.mcontext = context;
    }

    GetUserInformationDao dao = new GetUserInformationDao();

    public interface TaskWork{
        void onPostWork();
    }

    public void SetDepartmentInformation(String id , final TaskWork taskWork){
        new DepartmentInformationWorkerTask(new DepartmentInformationWorkerTask.AsyncWork() {

            Toast toast=new Toast(mcontext);
//            final ProgressDialog progressDialog = new ProgressDialog
//                    (mcontext);
            @Override
            public void preExcute() {
                /*toast = Toast.makeText(mcontext,"正在加载通知内容，请稍候...",Toast.LENGTH_SHORT);
                toast.show();*/
//
//            progressDialog.setTitle("正在加载部门信息内容，请稍候...");
//            progressDialog.setMessage("Loading...");
//            progressDialog.setCancelable(false);
//            progressDialog.show();
        }

            @Override
            public void postWork(Integer integer) {
                Log.i("integer2",":"+integer);
                if (integer == 0){
                    toast.cancel();
                }else if (integer == 1){
                    toast.cancel();
                    Toast.makeText(mcontext, "用户id失效，请重新登录", Toast.LENGTH_SHORT).show();
                }else if (integer == 2){
                    toast.cancel();
                    Toast.makeText(mcontext, "无法获取机器码，请重新登录", Toast.LENGTH_SHORT).show();
                }else if (integer == 3){
                    toast.cancel();
                    Toast.makeText(mcontext, "无效的文章id", Toast.LENGTH_SHORT).show();
                }else if (integer == -2){
                    toast.cancel();
                    Toast.makeText(mcontext, "发生了未知错误", Toast.LENGTH_SHORT).show();
                }else {
                    toast.cancel();
                    Toast.makeText(mcontext, "请求超时，请检查网络连接", Toast.LENGTH_SHORT).show();
                }
                taskWork.onPostWork();
                //progressDialog.dismiss();
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
