package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import app.mgs.com.microgovernmentsystem.R;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.Dao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.operations.PublicInformationOperation;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.PublicInformationDatahelper;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Values;

import static android.os.Environment.getExternalStorageDirectory;


public class PublicInformationActivity extends BaseActivity {

    public static PublicInformationActivity activity = null;
    Context mcontext = this;

    PublicInformationOperation publicInformationOperation = new PublicInformationOperation(mcontext);

    private TextView medttitle;
    private TextView medtName;
    private TextView medtField;
    private TextView medtTime;
    private TextView medtDense;
    private TextView medtVideo;
    private TextView medtPicture;
    private ImageButton mibtReturn;
    private WebView webView;
    private ProgressBar progressBar;
    DownloadManager downManager ;
    private DownLoadCompleteReceiver receiver;
    long idPro;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle();//先设置主题

        setContentView(R.layout.activity_public_information);

        downContent();

        initView();//初始化控件

        setOnClickListener();//监听事件

        setOnCreate();


        setContent();//设置页面内容
        activity = this;
        setPublicInformationRead();//更改阅读情况
    }
    private void setContent(){
        publicInformationOperation.SetPublicInformation(getIntentValues(), new PublicInformationOperation.TaskWork() {
            @Override
            public void onPostWork() {
                PublicInformationDatahelper datahelper = new PublicInformationDatahelper();//从数据库中取出信息放入页面中
                datahelper.getPublicInformation(medttitle,medtName,medtTime,medtDense,medtField,medtVideo,medtPicture,webView);
                datahelper.recordReadStatus(getIntentValues());
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
//            Intent intent = new Intent();
//            intent.setClass(PublicInformationActivity.this,PublicInformationListActivity.class);
//            startActivity(intent);
            finishActivity();
        }
        return super.onKeyDown(keyCode, event);
    }
    /**
     * 设置阅读情况，点击此条信息时，数据库数据置为1
     */
    private void setPublicInformationRead(){
        PublicInformationDatahelper datahelper = new PublicInformationDatahelper();
        datahelper.setRead(getIntentValues());
    }

    private void initView(){
        medtField = (TextView) findViewById(R.id.act_public_information_field);
        medttitle = (TextView) findViewById(R.id.act_public_information_tv_title);
        medtName = (TextView) findViewById(R.id.act_public_information_tv_name);
        //medtContent = (TextView) findViewById(R.id.act_public_information_tv_content);
        medtTime = (TextView) findViewById(R.id.act_public_information_tv_time);
        mibtReturn = (ImageButton) findViewById(R.id.act_public_information_ibt_return);
        medtDense = (TextView) findViewById(R.id.act_public_information_tv_dense);
        medtVideo = (TextView) findViewById(R.id.act_public_information_tv_video);
        medtPicture = (TextView) findViewById(R.id.act_public_information_tv_picture);
        webView = (WebView) findViewById(R.id.public_information_webView);
    }

    private void setOnClickListener(){
        mibtReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent();
//                intent.setClass(PublicInformationActivity.this,PublicInformationListActivity.class);
//                startActivity(intent);
                finishActivity();
            }
        });
    }

    /**
     * 在创建act前设置字体大小
     */
    void setStyle(){
        SharedPreferences sharedPreferences = getSharedPreferences("TextMode",0);
        int TextMode = sharedPreferences.getInt("TextMode",1);

        if(TextMode==0){
            this.setTheme(R.style.Theme_Small);
        }else if(TextMode==1){
            this.setTheme(R.style.Theme_Standard);
        }else if(TextMode==2){
            this.setTheme(R.style.Theme_Large);
        }else if (TextMode==3){
            this.setTheme(R.style.Theme_Larger);
        }else {
            this.setTheme(R.style.Theme_Largest);
        }
    }

    public void setOnCreate(){
        SharedPreferences sharedPreferences = getSharedPreferences("PublicInformationActivityOnCreate", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putInt("PublicInformationActivityOnCreate",1);
        editor.apply();
    }

    private void finishActivity(){
        SharedPreferences sharedPreferences = getSharedPreferences("PublicInformationActivityOnCreate", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putInt("PublicInformationActivityOnCreate",0);
        editor.apply();
        finish();
    }

    private String getIntentValues(){
        Intent intent = getIntent();
        return intent.getStringExtra("id");
    }

    public void downContent(){
//        String filePath = "/storage/emulated/0/gsDownload/"+getIntentValues()+".html";
        String filePath =  getExternalStorageDirectory()+"/gsDownload/"+getIntentValues()+".html";
        File fileCheck = new File(filePath);
        if(!fileCheck.exists()) {
            //        DeleteFile deleteFile = new DeleteFile(filePath);
//        deleteFile.deleteFile();
            File folder = Environment.getExternalStoragePublicDirectory("gsDownload");
            if (!folder.exists() || !folder.isDirectory()) {
                folder.mkdirs();
            }
            downManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            progressBar = (ProgressBar) (findViewById(R.id.downProgress));
            IntentFilter filter = new IntentFilter();
            filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            filter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);
            receiver = new DownLoadCompleteReceiver();
            registerReceiver(receiver, filter);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(Values.ServerAddress+"/PublicDownload/" + getIntentValues()));
            request.setDestinationInExternalPublicDir("gsDownload", getIntentValues() + ".html");
            request.setAllowedOverRoaming(false);
            idPro = downManager.enqueue(request);
            ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
            ses.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    queryTaskByIdandUpdateView(idPro);
                }
            }, 0, 2, TimeUnit.SECONDS);
        }
    }
    private void queryTaskByIdandUpdateView(long id){
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(id);
        Cursor cursor= downManager.query(query);
        String size="0";
        String sizeTotal="0";
        if(cursor.moveToNext()){
            size= cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
            sizeTotal = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
        }
        cursor.close();
        progressBar.setMax(Integer.valueOf(sizeTotal));
        progressBar.setProgress(Integer.valueOf(size));

    }

    private class DownLoadCompleteReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(intent.getAction()))
            {
                setContent();
            }
        }
    }

}
