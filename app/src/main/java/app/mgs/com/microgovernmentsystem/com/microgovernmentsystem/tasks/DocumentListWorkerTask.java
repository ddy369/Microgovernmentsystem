package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tasks;

import android.annotation.TargetApi;
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

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.DocumentListActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.Dao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.SetDocumentListDao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.ListBeanDocument;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Values;


public class DocumentListWorkerTask extends AsyncTask<String, Integer, Integer> {

    Dao dao = new Dao(DocumentListActivity.activity,null, Values.database_version);

    public ArrayList<ListBeanDocument> arrayList = new ArrayList<>();

    private AsyncWork asyncWork;

    public interface AsyncWork{
        void preExcute();
        void postWork(Integer integer);
    }

    public DocumentListWorkerTask(AsyncWork asyncWork) {
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
            String status = params[5];
            String address = Values.ServerAddress+"/Document/";
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
            content += "&doc_status=" + URLEncoder.encode(status, "UTF-8");
            dataOutputStream.writeBytes(content);
            dataOutputStream.flush();
            dataOutputStream.close();
            //监听网络连接是否超时
            int code = conn.getResponseCode();
            Log.i("code1",""+code);
            InputStream inputStream = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while (line != null) {
                JSONObject object = new JSONObject(line);
                int errorCode = object.getInt("error_code");
                if (object.getString("data").length() < 3){
                    return 3;//3为没有更多数据的情况
                }else if (errorCode==0) {
                    if (pageNum.equals("1")&&status.equals("2")){
                        SQLiteDatabase db = dao.getWritableDatabase();
                        db.delete("DocumentTable", null, null);
                    }
                        JSONArray jsonArray = object.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object1 = (JSONObject) jsonArray.get(i);
                            String id = object1.getString("id");
                            String title = object1.getString("title");
                            String subject = object1.getString("subject");
                            String updateDate = object1.getString("updateDate");
                            String sendPeople = object1.getString("sendPeople");
                            String readFlag = object1.getString("readFlag");
                            String purview = object1.getString("purview");
                            if (status.equals("2")) {
                                SetDocumentListDao setDocumentListDao = new SetDocumentListDao();
                                setDocumentListDao.setDocumentList(id, title, subject, updateDate, sendPeople, readFlag, purview);
                            }
                            else{
                                arrayList = setListBean(id, title, subject, updateDate,Integer.parseInt(readFlag),purview);
                                Log.i("aaa",":"+arrayList.size());
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
    public ArrayList<ListBeanDocument> getArrayList(){
        return arrayList;
    }

    public ArrayList<ListBeanDocument> setListBean(String id,String title,String subject,String updateDate,
                                                   int read,String purview){
        ListBeanDocument listBean = new ListBeanDocument(id,title,subject,updateDate,read,purview);
        arrayList.add(listBean);
        return arrayList;
    }


}



