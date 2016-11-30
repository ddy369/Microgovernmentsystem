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

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.SetPublicInformationDao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.SetPublicInformationListDao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Values;

/**
 * Created by Administrator on 2016/8/26.
 */
public class PublicInformationWorkerTask extends AsyncTask<String, Integer, Integer> {
    //开启一个异步任务进行联网登录的处理

    private AsyncWork asyncWork;

    public interface AsyncWork{
        void preExcute();
        void postWork(Integer integer);
    }

    public PublicInformationWorkerTask(AsyncWork asyncWork) {
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
            String id = params[0];
            String uid = params[1];
            String timeStamp = params[2];
            String mac = params[3];
            String sign = params[4];
            String address = Values.ServerAddress+"/PublicInformation/"+id;
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
            content += "&mac=" + URLEncoder.encode(mac, "UTF-8");
            content += "&timestamp=" + URLEncoder.encode(timeStamp, "UTF-8");
            content += "&sign=" + URLEncoder.encode(sign, "UTF-8");
            dataOutputStream.writeBytes(content);
            Log.i("contenting1",content);
            dataOutputStream.flush();
            dataOutputStream.close();
            //监听网络连接是否超时
            int code = conn.getResponseCode();
            Log.i("code2",""+code);
            InputStream inputStream = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            Log.i("lineing2:",line);
            while (line != null) {
                JSONObject object = new JSONObject(line);
                int errorCode = object.getInt("error_code");
                if (errorCode==0) {
                        JSONObject object1 = object.getJSONObject("data");
                        String pubid = object1.getString("id");
                        String field = object1.getString("field");
                        Log.i("code2ing",""+field);
                        String infoPeople = object1.getString("infoPeople");
                        String infoDate = object1.getString("infoDate");
                        String infotitle = object1.getString("infotitle");
                        Log.i("code2ing",""+infotitle);
                        String infocContent = object1.getString("infocContent");
                        String infoSecret = object1.getString("infoSecret");
                        String infoVideoAddress = object1.getString("infoVideoAddress");
                        String infoPictureFile = object1.getString("infoPictureFile");
                        SetPublicInformationDao setPublicInformationDao = new SetPublicInformationDao();
                        setPublicInformationDao.daoPublicInformationOperation(pubid,infoPeople,infoDate,field,infotitle,infocContent,infoSecret,infoVideoAddress,infoPictureFile);
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
