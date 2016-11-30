package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;

import app.mgs.com.microgovernmentsystem.R;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.GetUserInformationDao;
import sun.misc.BASE64Encoder;
/**
 * Created by Administrator on 2016/10/11.
 */
public class UploadFile {
    static private ProgressDialog progressDialog;
    static public String upfile(String url, final Handler handler)
    {
        String iomemory;
        File file = new File(url);
        final String ioname=file.getName();
        /*try {
            iomemory=encodeBase64File(file);
        }catch (Exception e)
        {
            e.printStackTrace();
            return "-1";
        }*/
        final RequestParams params = new RequestParams(Values.ServerAddress+"/AttactTest");
        params.addBodyParameter("uid",getUid());
        params.addBodyParameter("mac",getMac());
        params.addBodyParameter("timestamp",getTimeStamp());
        params.addBodyParameter("sign",getSign(ioname));
        params.addBodyParameter("file",new File(url));
        params.addBodyParameter("ioname",ioname);
        params.setCharset("UTF-8");
        params.setMultipart(true);
        x.http().post(params, new Callback.ProgressCallback<String>() {
            @Override
            public void onSuccess(String response) {
                progressDialog.dismiss();
                Log.d("http", "onSuccess: "+response);
                try {
                    // 在这里根据返回内容执行具体的逻辑
                    if (response != null) {
                        JSONObject object = new JSONObject(response);
                        int errorCode = object.getInt("error_code");
                        if (errorCode == 0) {
                            Message message = new Message();
                            message.what = 98;
                            message.obj=object.getString("data");;
                            handler.sendMessage(message);
                        } else if (errorCode == 201) {
                            Datahelper.showToast("uid无效,请重新登录");
                        } else if (errorCode == 202) {
                            Datahelper.showToast("MAC地址错误");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    //  return "上传失败";
                }


            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                progressDialog.dismiss();
                Message message = new Message();
                message.what = 99;
                handler.sendMessage(message);
            }
            @Override
            public void onCancelled(CancelledException cex) {
                progressDialog.dismiss();
                progressDialog.dismiss();
                Message message = new Message();
                message.what = 99;
                handler.sendMessage(message);
            }

            @Override
            public void onFinished() {
                progressDialog.dismiss();
            }
            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                Log.d("JAVA", "current：" + current + "，total：" + total);

                if (!isUploading) {
                    progressDialog.setProgress((int) (((int) current / (float) total) * 100));
                }
            }
            @Override
            public void onWaiting() {
            }
            @Override
            public void onStarted() {

                progressDialog = new ProgressDialog(ActivityCollector.getlast());

               // progressDialog.setTitle("请稍等...");

                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setTitle("正在上传附件");
                //设置ProgressDialog 提示信息
                progressDialog.setMessage(ioname+"...");
                //设置ProgressDialog 标题图标
                progressDialog.setIcon(R.drawable.ic_launcher);
                //设置ProgressDialog的最大进度
                progressDialog.setMax(100);
                //设置ProgressDialog 的一个Button
                progressDialog.setButton("取消", new ProgressDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog.dismiss();
                    }
                });

                progressDialog.setCancelable(true);

                progressDialog.setCanceledOnTouchOutside(false);

                progressDialog.show();
            }
        });






       /* new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    URLEncoder.encode(content, "UTF-8");
                    URL url = new URL();
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url
                            .openConnection();
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setUseCaches(false);
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setConnectTimeout(6 * 1000);
                    httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
                    httpURLConnection.setRequestProperty("Charset", "UTF-8");

                    DataOutputStream dos = new DataOutputStream(httpURLConnection
                            .getOutputStream());


                    //获取文件总大小
                    // 8192  8K
                    int count = 0;
                    int num = 0;
                    int length = content.length();
                    while (count < length) {
                        int size = length - count > 8192 ? 8192 : length - count;
                        dos.write(content.getBytes(), count, size);
                        count = count + size;
                        if (count * 100 / length > num) {
                            num++;
                            Message message = new Message();
                            message.what = 99;
                            message.obj = num;
                            handler.sendMessage(message);
                        }
                    }
                    ;
                    dos.flush();
                    InputStream is = httpURLConnection.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is, "utf-8");
                    BufferedReader br = new BufferedReader(isr);
                    @SuppressWarnings("unused")
                    String response = br.readLine();
                    Log.d("line", "upfile: " + response);
                    dos.close();
                    is.close();

                    try {
                        // 在这里根据返回内容执行具体的逻辑
                        if (response != null) {
                            JSONObject object = new JSONObject(response);
                            int errorCode = object.getInt("error_code");
                            if (errorCode == 0) {
                                JSONObject object1 = object.getJSONObject("data");
                                if (object1.getString("result").equals("true")) {
                                 //   return object1.getString("id");

                                } else
                                {

                                }
                                 //   return "上传失败";
                            } else if (errorCode == 201) {
                                Datahelper.showToast("uid无效,请重新登录");
                            } else if (errorCode == 202) {
                                Datahelper.showToast("MAC地址错误");
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                      //  return "上传失败";
                    }


                    //   return "上传成功";
                } catch (Exception e) {
                    e.printStackTrace();
                //    return "上传失败";
                }
            //    return "上传失败";
            }
        });*/
        return "";
    }
    /**
     * <p>将文件转成base64 字符串</p>
     * @param file 文件
     * @return
     * @throws Exception
     */
    public static String encodeBase64File(File file) throws Exception {

        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int)file.length()];
        inputFile.read(buffer);
        inputFile.close();
        return new BASE64Encoder().encode(buffer);
    }

//需要先将文件转化为byte[]，再转成Base64 String
private static String getMac() {
    return Settings.Secure.getString(Datahelper.getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
}

    private static String getTimeStamp() {
        //获取时间戳
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE);
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        return formatter.format(curDate);
    }

    private static  String getUid() {
        GetUserInformationDao getUserInformationDao = new GetUserInformationDao();
        return getUserInformationDao.getUid();
    }

    private static String getSign(String ioname) {
        HashMap<String, String> map = new HashMap<>();
        map.put("mac", getMac());

        map.put("ioname", ioname);
        map.put("timestamp", getTimeStamp());
        map.put("uid", getUid());
        GetSignTool getSignTool = new GetSignTool();
        return getSignTool.getSign(map);
        //获取签名值
//        return md5Tool.getMDSStr("govoa"+"mac"+getMac()+"password"+getPassword()+"timestamp"+getTimeStamp()
//                +"username"+account+"govoa");
    }
}
