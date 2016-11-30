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
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.SetMagazineDao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Values;

/**
 * Created by Administrator on 2016-08-22.
 */
public class MagazineWorkerTask extends AsyncTask<String, Integer, Integer>{

        private AsyncWork asyncWork;

        public interface AsyncWork{
            void preExecute();
            void postWork(Integer integer);
        }

        public MagazineWorkerTask(AsyncWork asyncWork) {
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
            String id = params[0];
            String uid = params[1];
            String timeStamp = params[2];
            String mac = params[3];
            String sign = params[4];
            String address = Values.ServerAddress+"/Magazine/"+id;
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
            content += "&timestamp=" + URLEncoder.encode(timeStamp, "UTF-8");
            content += "&mac=" + URLEncoder.encode(mac, "UTF-8");
            content += "&sign=" + URLEncoder.encode(sign, "UTF-8");
            dataOutputStream.writeBytes(content);
            Log.i("content2",content);
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
                if (errorCode==0) {
                    JSONObject jsonObject = object.getJSONObject("data");
                    String[] param = new String[6];
                    param[0] = jsonObject.getString("id");
                    param[1] = jsonObject.getString("mainTitle");
                    param[2] = jsonObject.getString("magNumber");
                    param[3] = jsonObject.getString("magDepartment");
                    param[4] = jsonObject.getString("magDate");
                    param[5] = jsonObject.getString("magContent");
                    SetMagazineDao setMagazineDao = new SetMagazineDao();
                    setMagazineDao.doMangazineOperation(param);
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
        }
        return -1;//-1通常为网络错误
    }
    }

