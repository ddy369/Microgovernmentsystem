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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.LoginActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.NoticeListActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.Dao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.NoticeListRecordIdDao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.SetNoticeListDao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.ListBean;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.NoticeListDatahelper;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Values;

public class NoticeListWorkerTask extends AsyncTask<String, Integer, Integer> {

    Dao dao = new Dao(NoticeListActivity.activity,null, Values.database_version);

    public ArrayList<ListBean> arrayList = new ArrayList<>();

    //开启一个异步任务进行联网登录的处理

    public AsyncWork asyncWork;

    public interface AsyncWork{
        void preExcute();
        void postWork(Integer integer);
    }

    public NoticeListWorkerTask(AsyncWork asyncWork) {
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
            String status = params[5];
            String address = Values.ServerAddress+"/Information";
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
            if (!status.equals("0")){
                content += "&status=" + URLEncoder.encode(status, "UTF-8");
            }
            dataOutputStream.writeBytes(content);
            Log.i("content1",content);
            dataOutputStream.flush();
            dataOutputStream.close();
            //监听网络连接是否超时
            int code = conn.getResponseCode();
            Log.i("code1",""+code);
            InputStream inputStream = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            Log.i("line1:",line);
            while (line != null) {
                JSONObject object = new JSONObject(line);
                int errorCode = object.getInt("error_code");
                if (object.isNull("data")){
                    return 3;//3为没有更多数据的情况
                }else if (errorCode==0) {
                    /**
                     * 先清空一下列表，然后重新获取最新15条以保持OA端和APP一致
                     */
                    if (pageNum.equals("1")&&status.equals("0")){
                        SQLiteDatabase db = dao.getWritableDatabase();
                        db.delete("NOTICELISTTABLE", null, null);
                    }
                    //
                     JSONArray jsonArray = object.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++){
                        JSONObject object1 = (JSONObject) jsonArray.get(i);
                        String recordId = object1.getString("RecordID");
                        String department = object1.getString("Organize");
                        String sendTime = object1.getString("UpdateTime");
                        String title = object1.getString("Subject");
                        String isRead = object1.getString("IsRead");
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE);
                        Date date = sdf.parse(sendTime);
                        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE).format(date);
                        if (status.equals("0")){
                            SetNoticeListDao setNoticeListDao = new SetNoticeListDao();
                            setNoticeListDao.setNoticeList(recordId,department,time,title,isRead);
                        }else {
                            int read;
                            if (isRead.equals("true")){
                                read = 1;
                            }else {
                                read = 0;
                            }
                            arrayList = setListBean(title,department,sendTime,recordId,read);
                            Log.i("aaa",":"+arrayList.size());
                        }
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
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;//-1通常为网络错误
    }

    public int getPageSize() {
        return 15;
    }

    public ArrayList<ListBean> getArrayList(){
        return arrayList;
    }

    public ArrayList<ListBean> setListBean(String title,
                                           String department,
                                           String time,
                                           String id,
                                           int read){
        ListBean listBean = new ListBean();
        listBean.setTitle(title);
        listBean.setDepartment(department);
        listBean.setTime(time);
        listBean.setId(id);
        listBean.setRead(read);
        arrayList.add(listBean);
        return arrayList;
    }

}
