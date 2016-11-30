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
import java.util.ArrayList;

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.MagazineListActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.PublicInformationListActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.Dao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.SetMagazineListDao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.ListBeanMagazine;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.ListBeanSelect;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Values;

/**
 * Created by Administrator on 2016-08-22.
 */
public class MagazineListWorkerTask extends AsyncTask<String, Integer, Integer>    {

        private AsyncWork asyncWork;

        public ArrayList<ListBeanMagazine> arrayList = new ArrayList<>();
        public ArrayList<ListBeanSelect> listBeanSelect = new ArrayList<>();
        public interface AsyncWork{
            void preExecute();
            void postWork(Integer integer);
        }

        public MagazineListWorkerTask(AsyncWork asyncWork) {
        this.asyncWork = asyncWork;
    }

        @Override
        protected void onPreExecute() {
        asyncWork.preExecute();
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
            String mag_type= params[5];
            if(pageNum.equals("1")){
                Dao dao = new Dao(MagazineListActivity.activity,null, Values.database_version);
                SQLiteDatabase dbRead = dao.getReadableDatabase();
                SQLiteDatabase dbWrite = dao.getWritableDatabase();
                Cursor cursor = dbRead.query("MagazineTable",null,null,null,null,null,null);
                if (cursor.getCount() != 0){
                    dbWrite.execSQL("delete from MagazineTable");
                }
            }
            String address = Values.ServerAddress+"/Magazine";
            URL url = new URL(address);
            conn = (HttpURLConnection) url.openConnection();
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
            content += "&mag_type=" + URLEncoder.encode(mag_type, "UTF-8");
            dataOutputStream.writeBytes(content);
            dataOutputStream.flush();
            dataOutputStream.close();
//            //监听网络连接是否超时
//            int code = conn.getResponseCode();
//            Log.i("code1",""+code);
            InputStream inputStream = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while (line != null) {
                Log.i("lines",line);
                JSONObject object = new JSONObject(line);
                int errorCode = object.getInt("error_code");
                if (object.getString("data").length() < 3){
                    return 3;//3为没有更多数据的情况
                }else if (errorCode==0) {
                    JSONArray jsonArrays = object.getJSONArray("MagType");
                    int c = 0;
                    for (int j = 0; j < jsonArrays.length(); j++) {
                        JSONObject objects = (JSONObject) jsonArrays.get(j);
                        String mt = objects.getString("magType");
                        if(mag_type.length() < 3 && c ==0) {
                            listBeanSelect.add(new ListBeanSelect("全部", true));
                            c=1;
                        }else if(c == 0){
                            listBeanSelect.add(new ListBeanSelect("全部", false));
                            c = 1;
                        }
                        if(mag_type.equals(mt)){
                            listBeanSelect.add(new ListBeanSelect(mt,true));
                            continue;
                        }
                        listBeanSelect.add(new ListBeanSelect(mt,false));
                    }
                    JSONArray jsonArray = object.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object1 = (JSONObject) jsonArray.get(i);
                        String id = object1.getString("id");
                        String title = object1.getString("title");
                        String subject = object1.getString("subject");
                        String updateDate = object1.getString("updateDate");
                        String sendPeople = object1.getString("sendPeople");
                        String numNO = object1.getString("numNO");
                        String year = object1.getString("year");
                        String year_no = object1.getString("year_no");
                        String sysId = object1.getString("sysId");
                        if(mag_type.length()<=2) {
                            SetMagazineListDao setMagazineListDao = new SetMagazineListDao();
                            setMagazineListDao.setMagazineList(id, title, subject, updateDate, sendPeople, numNO, year, year_no, sysId);
                        }else{
                            arrayList = setListBean(id, title, subject, updateDate,0,sendPeople,sysId,year_no);
                        }
                    }

                    return 0;//0为通过验证
                } else if (errorCode == 201) {
                    return 1;//uid无效
                } else if (errorCode == 202) {
                    return 2;//MAC地址错误
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
        }
        return -1;//-1通常为网络错误
    }

    public int getPageSize() {
        return 15;
    }

    public ArrayList<ListBeanMagazine> setListBean(String id,String title,String subject,String updateDate,
                                                   int read,String sendPeople,String sysId,String year_no){
        ListBeanMagazine listBean = new ListBeanMagazine(id,title,subject,updateDate,read,sendPeople,sysId,year_no);
        arrayList.add(listBean);
        return arrayList;
    }
    public ArrayList<ListBeanSelect> selectsListBean(){
        return listBeanSelect;
    }

    public ArrayList<ListBeanMagazine> getAL(){
        return arrayList;
    }
}



