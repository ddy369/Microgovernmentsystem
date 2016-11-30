package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tasks;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

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

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.LogOperationDao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Values;

public class LoginQueryWorkerTask extends AsyncTask<String, Integer, Integer>{

    //开启一个异步任务进行联网登录的处理

    private AsyncWork asyncWork;

    public interface AsyncWork{
        void preExcute();
        void postWork(Integer integer);
    }

    public LoginQueryWorkerTask(AsyncWork asyncWork) {
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
            String account = params[0];
            String password = params[1];
            String timeStamp = params[2];
            String mac = params[3];
            String sign = params[4];

            String address = Values.ServerAddress+"/Login";
            URL url = new URL(address);
            conn = (HttpURLConnection) url.openConnection();//获得已注册用户的账号密码
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            conn.setConnectTimeout(8000);
            conn.setReadTimeout(8000);
            conn.connect();

            DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());
            String content = "username=" + URLEncoder.encode(account, "UTF-8");
            content += "&password=" + URLEncoder.encode(password, "UTF-8");
            content += "&timestamp=" + URLEncoder.encode(timeStamp, "UTF-8");
            content += "&mac=" + URLEncoder.encode(mac, "UTF-8");
            content += "&sign=" + URLEncoder.encode(sign, "UTF-8");
            dataOutputStream.writeBytes(content);
            Log.i("content",content);
            dataOutputStream.flush();
            dataOutputStream.close();
            //监听网络连接是否超时
            int code = conn.getResponseCode();
            Log.e("code",""+code);
            InputStream inputStream = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            Log.e("line:",line);
            while (line != null) {
                JSONObject object = new JSONObject(line);
                int errorCode = object.getInt("error_code");
                if (errorCode==0) {
                    LogOperationDao logDaoOperation = new LogOperationDao();
                    JSONObject object1 = new JSONObject(object.getString("data"));
                    String name = object1.getString("name");
                    String position = object1.getString("headship");
                    String uid = object1.getString("uid");
                    Log.i("uid:",uid);
                    logDaoOperation.daoOperation(account,password,name,position,mac,uid);
                    return 0;//0为通过验证
                } else if (errorCode==101){
                    return 1;//101为密码错误
                } else if (errorCode==102){
                    return 2;//102为mac错误x
                } else if (errorCode==103){
                    return 3;//103为已提交审核
                } else if (errorCode==104){
                    return 4;//104为正在审核
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
}
