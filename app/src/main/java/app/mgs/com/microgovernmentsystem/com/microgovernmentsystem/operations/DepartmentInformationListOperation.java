package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.operations;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.Dao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.GetUserInformationDao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.ListBean;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.ListBeanSelect;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tasks.DepartmentInformationListWorkerTask;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.GetSignTool;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Values;

/**
 * Created by Administrator on 2016/8/21.
 */
public class DepartmentInformationListOperation {

    private Context mcontext;
    private String depart;
    private String departId;
    private int Status=0;
    private ArrayList<ListBean> listBeanArrayList=new ArrayList<>();
    private ArrayList<ListBeanSelect> listBeanName=new ArrayList<>();
    private ArrayList<ListBeanSelect> listBeanType=new ArrayList<>();

    public void Set0(){Status=0;}
    public void Set1(){Status=1;}

    public DepartmentInformationListOperation(Context context,String str1,String str2){
        this.mcontext = context;
        this.depart = str1;
        this.departId = str2;
    }

    //接口
    public interface TaskWork{
        void onPostWork();
        void onPostWorkFoot();
    }

    GetUserInformationDao getUserInformationDao = new GetUserInformationDao();

    public void setDepartmentInformationList(int pageNum, final TaskWork taskWork){
        if(depart.equals("全部")){
            depart="";
        }
        if(departId.equals("全部")){
            departId="";
        }
        //Log.i("00000000000predepart","depart"+depart+"departId"+departId);
        DepartmentInformationListWorkerTask task= new DepartmentInformationListWorkerTask(
                new DepartmentInformationListWorkerTask.AsyncWork(){
//            Toast toast;
            @Override
            public void preExcute() {

//                toast = Toast.makeText(mcontext,"正在获取部门信息列表，请稍后...",Toast.LENGTH_SHORT);
//                toast.show();
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
                }else if (integer == 3){
                    //toast.cancel();
                    Toast.makeText(mcontext, "没有更多部门信息了", Toast.LENGTH_SHORT).show();
                }
                else {
                   // toast.cancel();
                    Toast.makeText(mcontext, "网络连接超时，下拉刷新以重新获取列表",
                            Toast.LENGTH_SHORT).show();
                }
                taskWork.onPostWorkFoot();
            }
        });
        task.execute(getUid(),getMac(),getSign(pageNum),getTimeStamp(), String.valueOf(pageNum),depart,departId);
        listBeanArrayList = task.getArry();
        listBeanName = task.getArrayName();
        listBeanType = task.getArrayType();
        if(Status==0){
            task.set0();
        }else{
            task.set1();
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
        map.put("dep_id",depart);
        map.put("dep_name",departId);
        GetSignTool getSignTool = new GetSignTool();
        Log.i("eeeemap",""+map);
        return getSignTool.getSign(map);
    }
    public ArrayList<ListBean> getListBeen(){
        //Log.i("00000000000predepart","op"+listBeanArrayList.size());
        return  listBeanArrayList;}
    public ArrayList<ListBeanSelect> getListBeenName(){
       // Log.i("00000000000listBeanName","op"+listBeanName.size());
        return  listBeanName;}
    public ArrayList<ListBeanSelect> getListBeenType(){
        //Log.i("00000000000listBeanType","op"+listBeanType.size());
        return  listBeanType;}
}
