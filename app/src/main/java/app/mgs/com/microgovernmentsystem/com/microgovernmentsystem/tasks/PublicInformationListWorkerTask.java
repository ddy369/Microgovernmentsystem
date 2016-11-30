package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tasks;

import android.annotation.TargetApi;
import android.app.Activity;
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

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.PublicInformationActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.PublicInformationListActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.Dao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.SetPublicInformationListDao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.ListBean;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.ListBeanSelect;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Values;

public class PublicInformationListWorkerTask extends AsyncTask<String, Integer, Integer> {

    //开启一个异步任务进行联网登录的处理

    private AsyncWork asyncWork;
    private int status;
    private ArrayList<ListBean> listBean = new ArrayList<>();
    private ArrayList<ListBeanSelect> listBeanType = new ArrayList<>();


    public void set0(){status=0;}
    public void set1(){status=1;}

    public interface AsyncWork{
        void preExcute();
        void postWork(Integer integer);
    }

    public PublicInformationListWorkerTask(AsyncWork asyncWork) {
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
            String title1 = params[5];
            if(pageNum.equals("1")&&status==1){
                Dao dao = new Dao(PublicInformationListActivity.activity,null, Values.database_version);
                SQLiteDatabase dbRead = dao.getReadableDatabase();
                SQLiteDatabase dbWrite = dao.getWritableDatabase();
                Cursor cursor = dbRead.query("PublicInformationListTable",null,null,null,null,null,null);
                if (cursor.getCount() != 0){
                    dbWrite.execSQL("delete from PublicInformationListTable");
                }
            }
            String address = Values.ServerAddress+"/PublicInformation";
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
            content += "&pub_type=" + URLEncoder.encode(title1, "UTF-8");
            dataOutputStream.writeBytes(content);
            Log.i("contenting",content);
            dataOutputStream.flush();
            dataOutputStream.close();
            //监听网络连接是否超时
            int code = conn.getResponseCode();
            Log.i("code2",""+code);
            InputStream inputStream = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            Log.i("line2:",line);
            while (line != null) {
                JSONObject object = new JSONObject(line);
                int errorCode = object.getInt("error_code");
                //Log.i("object.getString",""+object.getString("data").length());
                if (object.getString("data").length()<3){
                    //Log.i("object.getString",""+object.getString("data").length());
                    return 3;//3为没有更多数据的情况
                }else if (errorCode==0) {
                    JSONArray jsonArray = object.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object1 = (JSONObject) jsonArray.get(i);
                        String id = object1.getString("id");
                        String field = object1.getString("field");
                        String title = object1.getString("title");
                        String updateDate = object1.getString("updateDate");
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE);
                        Date date = sdf.parse(updateDate);
                        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE).format(date);
                        //Log.i("timetimetimetime",""+time);

                        if(title1.isEmpty()) {
                            SetPublicInformationListDao setPublicInformationDao = new SetPublicInformationListDao();
                            setPublicInformationDao.SetPublicInformationListDao(id, title, time, field);
                        }else{
                           //Log.i("listBeansize",""+i+" "+listBean.size());
                            setListBean(title,updateDate,id,field);
                        }
                    }
                    if (object.getJSONArray("pubTypeList")!=null) {
                        JSONArray jsonArray1 = object.getJSONArray("pubTypeList");
                        listBeanType.add(new ListBeanSelect("全部", true));
                        for (int j = 0; j < jsonArray1.length(); j++) {
                            JSONObject object2 = (JSONObject) jsonArray1.get(j);
                            String pubType = object2.getString("pubType");
                            setListBeanType(pubType);
                        }
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
                             String field){
        ListBean listBeans = new ListBean();
        listBeans.setTitle(title);
        listBeans.setUpdateDate(updateDate);
        listBeans.setId(id);
        listBeans.setField(field);
        listBeans.setRead(0);
        listBean.add(listBeans);
    }
    private void setListBeanType(String pubType){
        ListBeanSelect listBeans = new ListBeanSelect();
        listBeans.setListViewItem(pubType);
        listBeans.setStatus(false);
        listBeanType.add(listBeans);
    }
    public int getPageSize() {
        return 15;
    }
    public ArrayList<ListBean> getListBean(){
        return listBean;
    }
    public ArrayList<ListBeanSelect> getListBeanType(){
        //Log.i("listBeanTypesize"," "+listBeanType.size());
        return listBeanType;
    }
}