package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tasks;

import android.annotation.TargetApi;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.DepartmentInformationListActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.Dao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.SetDepartmentInformationListDao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.ListBean;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.ListBeanSelect;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Values;

public class DepartmentInformationListWorkerTask extends AsyncTask<String, Integer, Integer> {

    //开启一个异步任务进行联网登录的处理
    private ArrayList<ListBean> listBeanArrayList = new ArrayList<>();
    private AsyncWork asyncWork;
    private int status;
    private ArrayList<ListBeanSelect> listBeanName = new ArrayList<>();
    private ArrayList<ListBeanSelect> listBeanType = new ArrayList<>();
    public interface AsyncWork{
        void preExcute();
        void postWork(Integer integer);
    }
    public void set0(){ status = 0;}
    public void set1(){ status = 1;}

    public DepartmentInformationListWorkerTask(AsyncWork asyncWork) {
        this.asyncWork = asyncWork;
    }

    @Override
    protected void onPreExecute() {
        asyncWork.preExcute();
    }

    @Override
    protected void onPostExecute(Integer integer) {
        asyncWork.postWork(integer);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected Integer doInBackground(String... params) {
        HttpURLConnection conn;
        try {
            String uid = params[0];
            String mac = params[1];
            String sign = params[2];
            String timeStamp = params[3];
            String pageNum = params[4];
            String depart = params[5];
            String departId = params[6];
            if(status==1&&pageNum.equals("1")){
                Dao dao = new Dao(DepartmentInformationListActivity.activity,null, Values.database_version);
                SQLiteDatabase dbRead = dao.getReadableDatabase();
                SQLiteDatabase dbWrite = dao.getWritableDatabase();
                Cursor cursor = dbRead.query("DepartmentInformationListTable",null,null,null,null,null,null);
                if (cursor.getCount() != 0){
                    dbWrite.execSQL("delete from DepartmentInformationListTable");
                }
            }
            String address = Values.ServerAddress+"/DepartmentInformation";
            URL url = new URL(address);
            conn = (HttpURLConnection) url.openConnection();//获得已注册用户的账号密码
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            conn.setConnectTimeout(20000);
            conn.setReadTimeout(20000);
            conn.connect();

            DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());
            String content = "uid=" + URLEncoder.encode(uid, "UTF-8");
            content += "&page_size=" + getPageSize();
            content += "&page_number=" + URLEncoder.encode(pageNum, "UTF-8");
            content += "&mac=" + URLEncoder.encode(mac, "UTF-8");
            content += "&sign=" + URLEncoder.encode(sign, "UTF-8");
            content += "&timestamp=" + URLEncoder.encode(timeStamp, "UTF-8");
            content += "&dep_id=" + URLEncoder.encode(depart, "UTF-8");
            content += "&dep_name=" + URLEncoder.encode(departId, "UTF-8");
            dataOutputStream.writeBytes(content);
            Log.i("contentDepart",content);
            dataOutputStream.flush();
            dataOutputStream.close();
            //监听网络连接是否超时
            int code = conn.getResponseCode();
            Log.i("code2",""+code);
            InputStream inputStream = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            Log.i("lineeee2:",line);
            while (line != null) {
                JSONObject object = new JSONObject(line);
                int errorCode = object.getInt("error_code");
                if (object.getString("data").length()<3){
                    return 3;//3为没有更多数据的情况
                }else if (errorCode==0) {
                    JSONArray jsonArray = object.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object1 = (JSONObject) jsonArray.get(i);
                        String id = object1.getString("id");
                        String subject = object1.getString("subject");
                        String title = object1.getString("title");
                        String updateDate = object1.getString("updateDate");
                        String sendPeople = object1.getString("sendPeople");
                        String departName = object1.getString("departName");

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINESE);
                        Date date = sdf.parse(updateDate);
                        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE).format(date);
                        if(depart.isEmpty()&&departId.isEmpty()){
                            //Log.i("000000000000",""+i);
                            SetDepartmentInformationListDao setDepartmentInformationListDao = new SetDepartmentInformationListDao();
                            setDepartmentInformationListDao.SetDepartmentInformationListDao(id,title,subject,sendPeople,time,departName);
                        }else{
                            Log.i("000000000000ArrayList",""+i+" "+listBeanArrayList.size());
                            setListBean(title,updateDate,id,subject,sendPeople,departName);
                        }
                    }
                    JSONArray jsonArray1 = object.getJSONArray("depName");
                    listBeanName.add(new ListBeanSelect("全部", true));
                    for (int i = 0; i < jsonArray1.length(); i++) {
                        JSONObject object2 = (JSONObject) jsonArray1.get(i);
                        String depName = object2.getString("depName");
                        String orderBy = object2.getString("orderBy");
                        setListBeanName(depName);
                        //Log.i("000000000000Name",""+listBeanName.size());
                    }
                    listBeanType.add(new ListBeanSelect("全部", true));
                    JSONArray jsonArray2 = object.getJSONArray("depType");
                    for (int i = 0; i < jsonArray2.length(); i++) {
                        JSONObject object3 = (JSONObject) jsonArray2.get(i);
                        String depType = object3.getString("depType");
                        setListBeanType(depType);
                       // Log.i("000000000000Name",""+listBeanType.size());
                    }
                    return 0;//0为通过验证
                } else if (errorCode==201){
                    return 1;//uid无效
                } else if (errorCode==202){
                    return 2;//MAC地址错误
                } else if (errorCode==203){
                    return 3;//3为错误的文章id
                } else {
                    return -2;//-2为未知错误
                }
            }
            bufferedReader.close();
            conn.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        } catch (JSONException e) {
            e.printStackTrace();
            return -1;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;//-1通常为网络错误
    }
    private void setListBean(String title,
                             String updateDate,
                             String id,
                             String subject,
                             String sendPeople,
                             String departName){
        ListBean listBean = new ListBean();
        listBean.setTitle(title);
        listBean.setUpdateDate(updateDate);
        listBean.setId(id);
        listBean.setSendPeople(sendPeople);
        listBean.setSubject(subject);
        listBean.setDepartName(departName);
        listBean.setRead(0);
        listBeanArrayList.add(listBean);
    }
    private void setListBeanType(String pubType){
        ListBeanSelect listBean = new ListBeanSelect();
        listBean.setListViewItem(pubType);
        listBean.setStatus(false);
        listBeanType.add(listBean);
    }
    private void setListBeanName(String pubName){
        ListBeanSelect listBean = new ListBeanSelect();
        listBean.setListViewItem(pubName);
        listBean.setStatus(false);
        listBeanName.add(listBean);
    }
    public ArrayList<ListBean> getArry(){
        //Log.i("000000000000ArrayList",""+listBeanArrayList.size());
        return listBeanArrayList;
    }
    public int getPageSize() {
        return 15;
    }

    public ArrayList<ListBeanSelect> getArrayName(){
        //Log.i("000000000000Name",""+listBeanName.size());
        return listBeanName;
    }
    public ArrayList<ListBeanSelect> getArrayType(){
        //Log.i("000000000000Type",""+listBeanType.size());
        return listBeanType;
    }
}