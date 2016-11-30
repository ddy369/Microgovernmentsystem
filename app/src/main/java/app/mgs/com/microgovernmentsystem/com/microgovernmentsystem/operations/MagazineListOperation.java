package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.operations;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.GetUserInformationDao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.ListBeanMagazine;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.ListBeanSelect;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tasks.MagazineListWorkerTask;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.GetSignTool;

/**
 * Created by Administrator on 2016-08-22.
 */
public class MagazineListOperation {
    private Context mcontext;
    private String mag_type;
    private int status;
    public MagazineListOperation(Context context ,String str){
        this.mcontext = context;
        if(str.equals("全部")){
            str="";
        }
        this.mag_type = str;
    }
    public void Set0(){status = 0;}
    public void Set1(){status = 1;}



    public MagazineListOperation(Context context){
        this.mcontext = context;
    }

    public ArrayList<ListBeanMagazine> arrayList = new ArrayList<>();

    public ArrayList<ListBeanSelect> listBeanSelect = new ArrayList<>();
    //接口
    public interface TaskWork{
        void onPostWork();
        void onPostWorkFoot();
    }

    GetUserInformationDao getUserInformationDao = new GetUserInformationDao();

    public void setMagazineList(int pageNum,String magType, final TaskWork taskWork){
        mag_type =magType;
        MagazineListWorkerTask magazineListWorkerTask = new MagazineListWorkerTask(new MagazineListWorkerTask.AsyncWork(){
//            Toast toast;
            @Override
            public void preExecute() {
//                toast = Toast.makeText(mcontext,"正在获取报刊列表，请稍候...",Toast.LENGTH_SHORT);
//                toast.show();
            }

            @Override
            public void postWork(Integer integer) {
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
//                    toast.cancel();
                    Toast.makeText(mcontext, "没有更多内部刊物了", Toast.LENGTH_SHORT).show();
                }
                else {
//                    toast.cancel();
                    Toast.makeText(mcontext, "网络连接超时，下拉刷新以重新获取列表", Toast.LENGTH_SHORT).show();
                }

                taskWork.onPostWorkFoot();
            }
        });
//        execute(getUid(),getMac(),getSign(pageNum),getTimeStamp(), String.valueOf(pageNum),magType
        magazineListWorkerTask.execute(getUid(),getMac(),getSign(pageNum),getTimeStamp(), String.valueOf(pageNum),magType);
        if (!mag_type.equals("2")){
            arrayList =  magazineListWorkerTask.getAL();
            listBeanSelect = magazineListWorkerTask.selectsListBean();
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
        map.put("mag_type",mag_type);
        GetSignTool getSignTool = new GetSignTool();
        return getSignTool.getSign(map);
    }
    public ArrayList<ListBeanMagazine> getAL(){
        return arrayList;
    }
    public ArrayList<ListBeanSelect> getSelect(){
        return listBeanSelect;
    }
}
