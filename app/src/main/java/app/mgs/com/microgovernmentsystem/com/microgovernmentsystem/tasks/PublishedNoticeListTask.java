package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tasks;

import android.annotation.TargetApi;
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

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.NoticeListRecordIdDao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.SetNoticeListDao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.SetPublishedNoticeListDao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Values;

public class PublishedNoticeListTask extends AsyncTask<String, Integer, Integer> {
    //开启一个异步任务进行联网登录的处理

    private AsyncWork asyncWork;

    public interface AsyncWork{
        void preExcute();
        void postWork(Integer integer);
    }

    public PublishedNoticeListTask(AsyncWork asyncWork) {
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
//            String recordId = params[2];
            String sign = params[2];
            String timeStamp = params[3];
            String pageNum = params[4];
            //TODO:
            String address = Values.ServerAddress+"/SelfInfo";
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
//            content += "&recordid=" + URLEncoder.encode(recordId, "UTF-8");
            content += "&mac=" + URLEncoder.encode(mac, "UTF-8");
            content += "&sign=" + URLEncoder.encode(sign, "UTF-8");
            content += "&timestamp=" + URLEncoder.encode(timeStamp, "UTF-8");
//            if (getRecordId()!=null){
//                content += "&recordid=" + URLEncoder.encode(getRecordId(), "UTF-8");
//            }
            dataOutputStream.writeBytes(content);
            Log.i("contenta",content);
            dataOutputStream.flush();
            dataOutputStream.close();
            //监听网络连接是否超时
            int code = conn.getResponseCode();
            Log.i("codea",""+code);
            InputStream inputStream = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            Log.i("linea:",line);
            while (line != null) {
                JSONObject object = new JSONObject(line);
                int errorCode = object.getInt("error_code");
                if (object.isNull("data")){
                    return 3;//3为没有更多数据的情况
                }else if (errorCode==0) {
                    JSONArray jsonArray = object.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++){
                        JSONObject object1 = (JSONObject) jsonArray.get(i);
                        String recordId = object1.getString("RecordID");
                        String department = object1.getString("Organize");
                        String sendTime = object1.getString("UpdateTime");
                        String title = object1.getString("Subject");
                        SetPublishedNoticeListDao setNoticeListDao = new SetPublishedNoticeListDao();
                        setNoticeListDao.setPublishedNoticeList(recordId,department,sendTime,title);
                    }
                    return 0;//0为通过验证
                } else if (errorCode==201){
                    return 1;//uid无效
                } else if (errorCode==202){
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
}
