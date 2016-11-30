package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.operations;

import android.content.Context;
import android.widget.Toast;

import java.util.HashMap;

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.GetUserInformationDao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tasks.DocumentWorkerTask;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.GetSignTool;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.UnreadMessageNum;

/**
 * Created by dingyi on 2016-08-18.
 */
public class DocumentOperation {

    private Context mcontext;

    public DocumentOperation(Context context){
        this.mcontext = context;
    }

    GetUserInformationDao dao = new GetUserInformationDao();

    public interface TaskWork{
        void onPostWork();
    }

    public void SetDocument(String id , final TaskWork taskWork){
        new DocumentWorkerTask(new DocumentWorkerTask.AsyncWork() {
            Toast toast=new Toast(mcontext);

            //final ProgressDialog progressDialog = new ProgressDialog(mcontext);

            @Override
            public void preExecute() {
//                progressDialog.setTitle("正在加载公文内容，请稍候...");
//                progressDialog.setMessage("Loading...");
//                progressDialog.setCancelable(false);
//                progressDialog.show();
            }

            @Override
            public void postWork(Integer integer) {
                if (integer == 0){
                    UnreadMessageNum.adddocumentnum(-1);
                    toast.cancel();
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
