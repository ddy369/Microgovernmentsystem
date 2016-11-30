package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tasks;


import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Values;

public class RemoveBindingWorkerTask extends AsyncTask<String, Integer, Integer> {

    private AsyncWork asyncWork;

    public interface AsyncWork {
        void preExcute();

        void postWork(Integer integer);
    }

    public RemoveBindingWorkerTask(AsyncWork asyncWork) {
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

    @Override
    protected Integer doInBackground(String... params) {
        HttpURLConnection conn;
        String address = Values.ServerAddress+"/LoginDelete";
        try {
            String account = params[0];
            String password = params[1];
            String mac = params[2];
            String timestamp = params[3];
            String sign = params[4];
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
            content += "&mac=" + URLEncoder.encode(mac, "UTF-8");
            content += "&timestamp=" + URLEncoder.encode(timestamp, "UTF-8");
            content += "&sign=" + URLEncoder.encode(sign, "UTF-8");
            dataOutputStream.writeBytes(content);
            Log.i("content", content);
            dataOutputStream.flush();
            dataOutputStream.close();
            //监听网络连接是否超时
            int code = conn.getResponseCode();
            Log.i("code", "" + code);
            InputStream inputStream = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            Log.i("line:", line);
            while (line != null) {
                JSONObject object = new JSONObject(line);
                int errorCode = object.getInt("error_code");
                if (errorCode == 0){
                    return 0;//0为成功解除绑定
                }else if (errorCode == 101){
                    return 1;//1为账号密码错误
                }else if (errorCode == 102){
                    return 2;//2为错误的mac地址
                }else if (errorCode == 103){
                    return 3;//3为用户未绑定账号
                }else {
                    return -2;//-2为未知错误
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return -1;
        } catch (ProtocolException e) {
            e.printStackTrace();
            return -1;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return -1;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        } catch (JSONException e) {
            e.printStackTrace();
            return -1;
        }
        return -1;//-1为网络错误
    }
}
