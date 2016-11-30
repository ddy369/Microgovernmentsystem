package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.webkit.JavascriptInterface;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;

import app.mgs.com.microgovernmentsystem.R;

/**
 * Created by Administrator on 2016/9/22.
 */
public class FileDownload {
    public static  Context  context;
    public static void setContext(Context a)
    {
        context=a;
    }
    public static void openordownlodfile(File currentPath, String url) {
        if (currentPath.exists()) {
            // File currentPath=file;
            if (currentPath.isFile())
                openfile(currentPath);
                else
                {
                    Datahelper.showToast("对不起，这不是文件！");
                }

            } else {
                //  String url = listBeanAttachment.getUrl();
                RequestParams params = new RequestParams(url);
                //自定义保存路径，Environment.getExternalStorageDirectory()：SD卡的根目录
                params.setSaveFilePath(Environment.getExternalStorageDirectory() + "/"+Values.cachepath+"/" + currentPath.getName());
                //自动为文件命名
                // params.setAutoRename(false);
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
                        openfile(currentPath);
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {
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
                        Datahelper.showToast("正在打开附件...");
                    }

                    //下载的时候不断回调的方法
                    @Override
                    public void onLoading(long total, long current, boolean isDownloading) {
                        //当前进度和文件总大小
                        Log.i("JAVA", "current：" + current + "，total：" + total);
                    }
                });
            }
        }

    private static boolean checkEndsWithInStringArray(String checkItsEnd,
                                                      String[] fileEndings) {
        for (String aEnd : fileEndings) {
            if (checkItsEnd.endsWith(aEnd))
                return true;
        }
        return false;
    }

    public static void openfile(File currentPath)
    {
        String fileName = currentPath.toString();
        Intent intent = null;
        if (checkEndsWithInStringArray(fileName, context.getResources().
                getStringArray(R.array.fileEndingImage))) {
            intent = OpenFiles.getImageFileIntent(currentPath);
            //     startActivity(intent);
        } else if (checkEndsWithInStringArray(fileName, context.getResources().
                getStringArray(R.array.fileEndingWebText))) {
            intent = OpenFiles.getHtmlFileIntent(currentPath);
            //       startActivity(intent);
        } else if (checkEndsWithInStringArray(fileName, context.getResources().
                getStringArray(R.array.fileEndingPackage))) {
            intent = OpenFiles.getApkFileIntent(currentPath);
            //       startActivity(intent);

        } else if (checkEndsWithInStringArray(fileName, context.getResources().
                getStringArray(R.array.fileEndingAudio))) {
            intent = OpenFiles.getAudioFileIntent(currentPath);
            //     startActivity(intent);
        } else if (checkEndsWithInStringArray(fileName, context.getResources().
                getStringArray(R.array.fileEndingVideo))) {
            intent = OpenFiles.getVideoFileIntent(currentPath);
            ///     startActivity(intent);
        } else if (checkEndsWithInStringArray(fileName, context.getResources().
                getStringArray(R.array.fileEndingText))) {
            intent = OpenFiles.getTextFileIntent(currentPath);
            //     startActivity(intent);
        } else if (checkEndsWithInStringArray(fileName, context.getResources().
                getStringArray(R.array.fileEndingPdf))) {
            intent = OpenFiles.getPdfFileIntent(currentPath);
            //     startActivity(intent);
        } else if (checkEndsWithInStringArray(fileName, context.getResources().
                getStringArray(R.array.fileEndingWord))) {
            intent = OpenFiles.getWordFileIntent(currentPath);
            //     startActivity(intent);
        } else if (checkEndsWithInStringArray(fileName, context.getResources().
                getStringArray(R.array.fileEndingExcel))) {
            intent = OpenFiles.getExcelFileIntent(currentPath);
            //     startActivity(intent);
        } else if (checkEndsWithInStringArray(fileName, context.getResources().
                getStringArray(R.array.fileEndingPPT))) {
            intent = OpenFiles.getPPTFileIntent(currentPath);
            //      startActivity(intent);
        } else {
            Datahelper.showToast("无法打开，请安装相应的软件！");
        }
        try {
            if (intent != null)
                context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Datahelper.showToast("没有能打开此文件的应用");
        }
    }
    @JavascriptInterface
    public void call(String num) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + num));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    @JavascriptInterface
    public void downloadforjs(String filename,String url)
    {
        Log.d("js", "downloadforjs: "+filename+url);
        File file=new File(Environment.getExternalStorageDirectory()+"/"+Values.cachepath+"/"+filename);
        openordownlodfile(file,url);
    }
}
