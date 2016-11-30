package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.operations;


import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.HashMap;

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.GetUserInformationDao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tasks.RemoveBindingWorkerTask;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.GetSignTool;

public class RemoveBindingOperation {

    private Context context;


    public RemoveBindingOperation(Context context1){
        this.context = context1;
    }

    public void Remove(final LinearLayout layout){

        new RemoveBindingWorkerTask(new RemoveBindingWorkerTask.AsyncWork() {
            Toast toast;

            @Override
            public void preExcute() {
                toast = Toast.makeText(context,"正在寻找网络",Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                layout.setVisibility(View.VISIBLE);
            }

            @Override
            public void postWork(Integer integer) {
                Log.i("integer","integer = "+integer);
                if (integer==0) {
                    //点击跳转页面
                    toast.cancel();

                }else if (integer==1){
                    toast.cancel();
                    Toast.makeText(context, "账号密码错误", Toast.LENGTH_SHORT).show();
                    layout.setVisibility(View.GONE);
                }else if (integer==2){
                    toast.cancel();
                    Toast.makeText(context, "机器码错误", Toast.LENGTH_SHORT).show();
                    layout.setVisibility(View.GONE);
                }else if (integer==3){
                    toast.cancel();
                    Toast.makeText(context, "本账号未进行绑定，请先绑定", Toast.LENGTH_SHORT).show();
                    layout.setVisibility(View.GONE);
                }else if (integer==-1){
                    toast.cancel();
                    Toast.makeText(context, "网络错误,请检查是否联网", Toast.LENGTH_SHORT).show();
                    layout.setVisibility(View.GONE);
                }else {
                    toast.cancel();
                    Toast.makeText(context, "发生未知错误", Toast.LENGTH_SHORT).show();
                    layout.setVisibility(View.GONE);
                }
                ExitLogOperation operation = new ExitLogOperation();
                operation.exitLog(context);
            }
        }).execute(getAccount(),getPassword(),getMac(),getTimeStamp(),getSign());
    }

    private String getAccount(){
        GetUserInformationDao getUserInformationDao = new GetUserInformationDao();
        return getUserInformationDao.getAccount();
    }

    private String getPassword(){
        GetUserInformationDao getUserInformationDao = new GetUserInformationDao();
        return getUserInformationDao.getPassword();
    }

    private String getMac(){
        GetUserInformationDao getUserInformationDao = new GetUserInformationDao();
        return getUserInformationDao.getMac();
    }

    private String getTimeStamp(){
        GetUserInformationDao getUserInformationDao = new GetUserInformationDao();
        return getUserInformationDao.getTimeStamp();
    }

    private String getSign(){
        HashMap<String , String> map = new HashMap<>();
        map.put("mac",getMac());
        map.put("password",getPassword());
        map.put("timestamp",getTimeStamp());
        map.put("username",getAccount());
        GetSignTool getSignTool = new GetSignTool();
        return getSignTool.getSign(map);
    }
}
