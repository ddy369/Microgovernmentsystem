package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.operations;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.GetUserInformationDao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.ListBean;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.ListBeanSelect;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tasks.PublicInformationListWorkerTask;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.GetSignTool;

/**
 * Created by Administrator on 2016/8/21.
 */
public class PublicInformationListOperation {

    private Context mcontext;
    private String title;
    private int status;
    private ArrayList<ListBean> listBean = new ArrayList<>();
    private ArrayList<ListBeanSelect> listBeanType = new ArrayList<>();

    public PublicInformationListOperation(Context context,String str){
        this.mcontext = context;
        if(str.equals("全部")){
            str="";
        }
        this.title = str;
    }
    public void Set0(){status = 0;}
    public void Set1(){status = 1;}


    //接口
    public interface TaskWork{
        void onPostWork();
        void onPostWorkFoot();
    }

    GetUserInformationDao getUserInformationDao = new GetUserInformationDao();

    public void setPublicInformationList(int pageNum, final TaskWork taskWork){
        PublicInformationListWorkerTask task =
                new PublicInformationListWorkerTask(new PublicInformationListWorkerTask.AsyncWork(){
            //Toast toast;

            @Override
            public void preExcute() {
                //toast = Toast.makeText(mcontext,"正在获取公共信息列表，请稍后...",Toast.LENGTH_SHORT);
                //toast.show();
            }

            @Override
            public void postWork(Integer integer) {
                Log.i("integer1:"," "+integer);
                if (integer == 0){
                    taskWork.onPostWork();
                }else if (integer == 1){
                    //toast.cancel();
                    Toast.makeText(mcontext, "无效的用户id，请重新登录", Toast.LENGTH_SHORT).show();
                }else if (integer == 2){
                    //toast.cancel();
                    Toast.makeText(mcontext, "获取机器码错误，请重新登录", Toast.LENGTH_SHORT).show();
                }else if (integer == -2){
                    //toast.cancel();
                    Toast.makeText(mcontext, "未知错误", Toast.LENGTH_SHORT).show();
                }else if(integer ==3){
                    Toast.makeText(mcontext, "没有更多公共信息了", Toast.LENGTH_SHORT).show();
                }
                else {
                    //toast.cancel();
                    Toast.makeText(mcontext, "网络连接超时，下拉刷新以重新获取列表",
                            Toast.LENGTH_SHORT).show();
                }
                taskWork.onPostWorkFoot();
            }
        });
        task.execute(getUid(),getMac(),getSign(pageNum),getTimeStamp(), String.valueOf(pageNum),title);
        listBean = task.getListBean();
        listBeanType = task.getListBeanType();
        if(status==0){
            task.set0();
        }else{
            task.set1();
        }
    }

    public ArrayList<ListBean> getListBean(){ return  listBean;}

    public ArrayList<ListBeanSelect> getListBeanType(){ return  listBeanType;}

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
        map.put("pub_type",title);
        GetSignTool getSignTool = new GetSignTool();
        return getSignTool.getSign(map);
    }
}
