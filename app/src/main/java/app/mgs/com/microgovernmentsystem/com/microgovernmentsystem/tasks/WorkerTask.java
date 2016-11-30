//package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tasks;
//
//import android.annotation.TargetApi;
//import android.os.AsyncTask;
//import android.os.Build;
//import android.util.Log;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.BufferedReader;
//import java.io.DataOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.net.URLEncoder;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.Map;
//import java.util.Set;
//
//import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.SetNoticeDao;
//
///**
// * Created by dingyi on 2016-08-18.
// */
//public class WorkerTask extends AsyncTask<HashMap<String,String>, Integer, InputStream> {
//
//    private AsyncWork asyncWork;
//    private String httpAddress;
//
//    public interface AsyncWork{
//        void preExecute();
//        void postWork(InputStream inputStream);
//    }
//
//    public WorkerTask(AsyncWork asyncWork,String httpAddress) {
//        this.asyncWork = asyncWork;
//        this.httpAddress = httpAddress;
//    }
//
//    @Override
//    protected void onPreExecute() {
//        asyncWork.preExecute();
//    }
//
//    @Override
//    protected void onPostExecute(InputStream inputStream) {
//        asyncWork.postWork(inputStream);
//    }
//
//    @TargetApi(Build.VERSION_CODES.KITKAT)
//    @Override
//    protected InputStream doInBackground(HashMap... map) {
//        HttpURLConnection conn;
//        try {
////            String id = params[0];
////            String uid = params[1];
////            String timeStamp = params[2];
////            String mac = params[3];
////            String sign = params[4];
//            String address = httpAddress;
//            URL url = new URL(address);
//            conn = (HttpURLConnection) url.openConnection();//获得已注册用户的账号密码
//            conn.setRequestMethod("POST");
//            conn.setDoOutput(true);
//            conn.setUseCaches(false);
//            conn.setInstanceFollowRedirects(true);
//            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
//            conn.setConnectTimeout(20000);
//            conn.setReadTimeout(20000);
//            conn.connect();
//            DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());
//            Map<String,String> maps = new HashMap();
//
//            maps = map[0];
//            map[0].put("111","111");
//            map[0].put("222","222");
//            map[0].put("333","333");
//            Set set = map[0].keySet();
//            for(Iterator iter = set.iterator(); iter.hasNext();)
//            {
//                String key = (String)iter.next();
//                String value = (String)map[0].get(key);
//                System.out.println(key+"===="+value);
//            }
//            maps.put(map[1].keySet())
//
//            String m =  map[1].toString();
//            String content = hashMaps. + URLEncoder.encode(uid, "UTF-8");
//            content += "&timestamp=" + URLEncoder.encode(timeStamp, "UTF-8");
//            content += "&mac=" + URLEncoder.encode(mac, "UTF-8");
//            content += "&sign=" + URLEncoder.encode(sign, "UTF-8");
//            dataOutputStream.writeBytes(content);
//            Log.i("content2",content);
//            dataOutputStream.flush();
//            dataOutputStream.close();
//            //监听网络连接是否超时
//            int code = conn.getResponseCode();
//            Log.i("code2",""+code);
//            InputStream inputStream = conn.getInputStream();
//            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
//            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//            String line = bufferedReader.readLine();
//            Log.i("line2:",line);
//            while (line != null) {
//                JSONObject object = new JSONObject(line);
//                int errorCode = object.getInt("error_code");
//                if (errorCode==0) {
//                    JSONObject object1 = object.getJSONObject("data");
//                    String recordid = object1.getString("RecordID");
//                    String title = object1.getString("Subject");
//                    String sender = object1.getString("SendPeople");
//                    String time = object1.getString("UpdateTime");
//                    String organizelist = object1.getString("Organize_list");
//                    String peoplelist = object1.getString("People_list");
//                    String isreturn = object1.getString("IsReturn");
//                    String sendmessage = object1.getString("IsSendNote");
//
//                    JSONObject object2 = object1.getJSONObject("Attachment");
//                    String name = object2.getString("Attachment_Name");
//                    String aurl = object2.getString("Attachment_Url");
//                    String size = object2.getString("Attachment_Size");
//                    SetNoticeDao setNoticeDao= new SetNoticeDao();
//                    setNoticeDao.daoNoticeOperation(recordid,title,sender,time,organizelist,peoplelist,isreturn,
//                            sendmessage);
//                    setNoticeDao.daoAttachment(name,aurl,size);
//                    return 0;//0为通过验证
//                } else if (errorCode==201){
//                    return 1;//uid无效
//                } else if (errorCode==202){
//                    return 2;//MAC地址错误
//                } else if (errorCode==203){
//                    return 3;//3为错误的文章id
//                } else {
//                    return -2;//-2为未知错误
//                }
//            }
//            bufferedReader.close();
//            conn.disconnect();
//        } catch (IOException e) {
//            e.printStackTrace();
//            return -1;
//        } catch (JSONException e) {
//            e.printStackTrace();
//            return -1;
//        }
//        return -1;//-1通常为网络错误
//    }
//
//}
