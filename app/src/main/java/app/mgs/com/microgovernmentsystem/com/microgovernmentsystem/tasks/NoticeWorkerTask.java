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

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.SetNoticeDao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Values;

public class NoticeWorkerTask extends AsyncTask<String, Integer, Integer> {

    //开启一个异步任务进行联网登录的处理

    private AsyncWork asyncWork;

    public interface AsyncWork{
        void preExcute();
        void postWork(Integer integer);
    }

    public NoticeWorkerTask(AsyncWork asyncWork) {
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
            String address = Values.ServerAddress+"/Information/"+id;
            Log.i("content2",id);
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
            //String line = bufferedReader.readLine();
            String line="";
            StringBuilder response = new StringBuilder();
            String tem;
            while ((tem = bufferedReader.readLine()) != null) {
                Log.d("http",tem );
                response.append(tem);
            }
            line=response.toString();

            while (line != null) {
                Log.i("line2:",line);
                JSONObject object = new JSONObject(line);
                int errorCode = object.getInt("error_code");
                if (errorCode==0) {
                    JSONObject object1 = object.getJSONObject("data");
                    String recordid = object1.getString("RecordID");
                    String title = object1.getString("Subject");
                    String sender = object1.getString("SendPeople");
                    String time = object1.getString("UpdateTime");
                    String organizelist = object1.getString("Organize_list");
                    String peoplelist = object1.getString("People_list");
                    String isreturn = object1.getString("IsReturn");
                    String sendmessage = object1.getString("IsSendNote");
                    String  content1=object1.getString("Content");
                    SetNoticeDao setNoticeDao= new SetNoticeDao();
                    setNoticeDao.daoNoticeOperation(recordid,title,sender,time,organizelist,peoplelist,isreturn,
                            sendmessage,content1);
                    //适应多个附件列表
                    if (object1.getJSONArray("Attachment")!=null) {
                        JSONArray jsonArray = object1.getJSONArray("Attachment");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object2 = (JSONObject) jsonArray.get(i);
                            String name = object2.getString("Attachment_Name");
                            String aurl = object2.getString("Attachment_Url");
                            String size = object2.getString("Attachment_Size");
                            float i1 = Float.parseFloat(size);
                            if (i1 > 1024) {
                                i1 = i1 / 1024;
                                size = "(" + String.valueOf(i1).substring(0, min(String.valueOf(i1).indexOf(".") + 3, String.valueOf(i1).length())) + "K)";
                            }
                            if (i1 > 1024) {
                                i1 = i1 / 1024;
                                size = "(" + String.valueOf(i1).substring(0, min(String.valueOf(i1).indexOf(".") + 3, String.valueOf(i1).length())) + "M)";
                            }
                            if (i1 > 1024) {
                                i1 = i1 / 1024;
                                size = "(" + String.valueOf(i1).substring(0, min(String.valueOf(i1).indexOf(".") + 3, String.valueOf(i1).length())) + "G)";
                            }
                            setNoticeDao.daoAttachment(name, aurl, size, recordid);
                        }
                    }
                    return 0;

/*
                    JSONObject object2 = object1.getJSONObject("Attachment");
                    String name = object2.getString("Attachment_Name");
                    String aurl = object2.getString("Attachment_Url");
                    String size = object2.getString("Attachment_Size");

                    setNoticeDao.daoAttachment(name,aurl,size,recordid);*/
                 //   return 0;//0为通过验证
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
         //   return -1;
        } catch (JSONException e) {
            e.printStackTrace();
         //   return -1;
        }
        return 0;
    }
    private  int min(int a,int b)
    {
        if(a>b)
            return b;
        else
            return a;
    }

}
