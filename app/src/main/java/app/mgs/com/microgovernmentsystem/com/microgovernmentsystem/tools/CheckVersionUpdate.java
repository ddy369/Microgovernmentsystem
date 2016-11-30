package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;

import app.mgs.com.microgovernmentsystem.R;

/**
 * Created by Administrator on 2016/10/14.
 */
public class CheckVersionUpdate {

    private ProgressDialog progressDialog;
    public String getVersionName() throws Exception{
        //获取packagemanager的实例
        PackageManager packageManager = ActivityCollector.getlast().getPackageManager();
        //getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(ActivityCollector.getlast().getPackageName(), 0);
        return packInfo.versionName;
    }
    public void checkVersion(final callback a)
    {
        // 检测更新地址未补全
        RequestParams params = new RequestParams(Values.ServerAddress+"/AndroidUpdate");
        // params.addQueryStringParameter("username","abc");
        // params.addQueryStringParameter("password","123");
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //解析result
                try {
                    Log.d("version", "onSuccess: "+result+getVersionName());
                    JSONObject object = new JSONObject(result);
                    String versionnum=object.getString("version");
                    if(!versionnum.equals(getVersionName()))
                   {
                        showUpdataDialog(object.getString("info"),object.getString("url"));
                   }
                    else
                    {
                        a.callback();
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                }



            }
            //请求异常后的回调方法
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            }
            //主动调用取消请求的回调方法
            @Override
            public void onCancelled(CancelledException cex) {
            }
            @Override
            public void onFinished() {
            }
        });
    }
    //安装apk
    public void installApk(File file) {
        Intent intent = new Intent();
        //执行动作
        intent.setAction(Intent.ACTION_VIEW);
        //执行的数据类型
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");

        ActivityCollector.getlast().startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }
    protected void showUpdataDialog(String s,final  String url) {
        AlertDialog.Builder builer = new AlertDialog.Builder(ActivityCollector.getlast()) ;
        builer.setTitle("版本升级");
        builer.setMessage(s);
        //当点确定按钮时从服务器上下载 新的apk 然后安装
        builer.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                downLoadApk(url);
            }
        });
        //当点取消按钮时进行登录
        builer.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
             //   LoginMain();
            }
        });
        AlertDialog dialog = builer.create();
        dialog.show();
    }
    protected void downLoadApk(String url) {
        //apk下载地址
        RequestParams params = new RequestParams(url);
        //自定义保存路径，Environment.getExternalStorageDirectory()：SD卡的根目录
        params.setSaveFilePath(Environment.getExternalStorageDirectory() + "/"+Values.cachepath+"/");
        //自动为文件命名
        params.setAutoRename(true);
        //   params.setSaveFilePath();
        x.http().get(params, new Callback.ProgressCallback<File>() {
            @Override
            public void onSuccess(File currentPath) {
                        /*Intent intent = new Intent(Intent.ACTION_VIEW);
                        Uri uri = Uri.fromFile(new File(result.getPath()));
                        String fileName=result.getName();
                        String prefix=fileName.substring(fileName.lastIndexOf(".")+1);
                        intent.setDataAndType(uri,"**//*"); // 设置数据路径和类型
                        startActivity(intent);*/
                progressDialog.dismiss();
               installApk(currentPath);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                progressDialog.dismiss();
            }

            @Override
            public void onFinished() {
                //  Datahelper.showToast("开始附件...");
            }

            //网络请求之前回调
            @Override
            public void onWaiting() {
            }

            //网络请求开始的时候回调
            @Override
            public void onStarted() {
                progressDialog = new ProgressDialog(ActivityCollector.getlast());

                // progressDialog.setTitle("请稍等...");

                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setTitle("正在下载更新...");
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

            //下载的时候不断回调的方法
            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                //当前进度和文件总大小
                Log.i("JAVA", "current：" + current + "，total：" + total);
                if (isDownloading) {
                    progressDialog.setProgress((int) (((int) current / (float) total) * 100));
                }
            }
        });


    }
    public interface callback {
        void callback();
    }

}
