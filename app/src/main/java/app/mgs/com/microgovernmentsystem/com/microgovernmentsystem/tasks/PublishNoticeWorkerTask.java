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

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Values;

public class PublishNoticeWorkerTask extends AsyncTask<String, Integer, Integer> {
    //开启一个异步任务进行联网登录的处理

    private AsyncWork asyncWork;

    public interface AsyncWork{
        void preExcute();
        void postWork(Integer integer);
    }

    public PublishNoticeWorkerTask(AsyncWork asyncWork) {
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
            String uid = params[0];//用户id
            String peopleList = params[1];//通知人id集合
            String organizeList = params[2];//部门id集合
            String mac = params[3];//mac地址
            String sign = params[4];//
            String timeStamp = params[5];//时间戳
            String subject = params[6];//标题
            String text = params[7];//正文
            String needSendnote = params[8];//是否需要短信提醒
            String needReturn = params[9];//是否需要回执
            String type = params[10];//类型
            String docId = params[11];//文章id
            String attachment=params[12];//附件url
            String recordId = params[13];//用来标记是否为转发
            String address = "";
            Log.i("ccc",type);
            if(type.equals("2") ){
                 address = Values.ServerAddress + "/DocumentSend/";
            }else {
                 address = Values.ServerAddress + "/AddInformation/";
            }
            Log.i("url",address);
            URL url = new URL(address);
            conn = (HttpURLConnection) url.openConnection();//获得已注册用户的账号密码
            if(type == "2" ){
                conn.setRequestMethod("POST");
            }else {
                conn.setRequestMethod("POST");
            }
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
            conn.setConnectTimeout(20000);
            conn.setReadTimeout(20000);
            conn.connect();

            DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());
            //TODO：设置content值，其中附件attachment没有，接口中缺少参数:发送人
            String content = "uid=" + URLEncoder.encode(uid, "UTF-8");
            if (!peopleList.isEmpty())
                content += "&people_list=" + URLEncoder.encode(peopleList, "UTF-8");
            if (!organizeList.isEmpty())
                content += "&organize_list=" + URLEncoder.encode(organizeList, "UTF-8");
            content += "&mac=" + URLEncoder.encode(mac, "UTF-8");
            content += "&sign=" + URLEncoder.encode(sign, "UTF-8");
            content += "&timestamp=" + URLEncoder.encode(timeStamp, "UTF-8");
            content += "&subject=" + URLEncoder.encode(subject, "UTF-8");
            if (!text.isEmpty())
                content += "&content=" + URLEncoder.encode(text, "UTF-8");
            if (!attachment.isEmpty())
                content += "&attachment=" + URLEncoder.encode(attachment, "UTF-8");
            content += "&is_sendnote=" + URLEncoder.encode(needSendnote, "UTF-8");
            content += "&docId=" + URLEncoder.encode(docId, "UTF-8");
            content += "&is_return=" + URLEncoder.encode(needReturn, "UTF-8");
            if (!recordId.equals("null"))
                content += "&is_relay=" + URLEncoder.encode(recordId, "UTF-8");

            dataOutputStream.writeBytes(content);
            Log.i("content4",content);
            dataOutputStream.flush();
            dataOutputStream.close();
            //监听网络连接是否超时
            int code = conn.getResponseCode();
            Log.i("code4",""+code);
            InputStream inputStream = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            Log.i("line4:",line);
            while (line != null) {
                JSONObject object = new JSONObject(line);
                int errorCode = object.getInt("error_code");
                if (errorCode==0) {
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
