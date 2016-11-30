package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
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
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.SetDepartmentInformationDao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.operations.DepartmentInformationOperation;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.DepartmentInformationDataHelper;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.FileDownload;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Values;

import static android.os.Environment.getExternalStorageDirectory;

/**
 * Created by Administrator on 2016/8/27.
 */
public class DepartmentInformationActivity extends BaseActivity {

    public static DepartmentInformationActivity activity = null;
    Context mcontext = this;


    private ImageButton mibtReturn;
    private TextView mtvdepartName;
    private TextView mtvName;
    private TextView mtvheadtitle;
    private TextView mtvtoolbartitle;
    private TextView mtvTime;
    private TextView mtvdepAttachment;
    private WebView webView;
    private ProgressBar progressBar;
    DownloadManager downManager ;
    private DownLoadCompleteReceiver receiver;
    long idPro;


    DepartmentInformationOperation departmentInformationOperation = new DepartmentInformationOperation(mcontext);



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle();//先设置主题
        setContentView(R.layout.activity_department_information);
        initView();//初始化控件
        downContent();
        setOnClickListener();//点击监听事件
        setPublicInformationRead();//更改阅读情况
        activity = this;
        setOnCreate();
        setContent();


    }
    //初始化控件
    private void initView(){
        mibtReturn = (ImageButton) findViewById(R.id.act_department_information_ibt_return);
        mtvdepartName = (TextView) findViewById(R.id.act_department_information_tv_department);
        mtvName = (TextView) findViewById(R.id.act_department_information_tv_name);
        mtvheadtitle = (TextView) findViewById(R.id.act_department_information_tv_title);
        mtvtoolbartitle = (TextView) findViewById(R.id.act_department_information_tv_toolbar);
        mtvTime = (TextView) findViewById(R.id.act_department_information_tv_time);
        mtvdepAttachment = (TextView) findViewById(R.id.act_department_information_tv_attachment);
        webView = (WebView) findViewById(R.id.deo_information_webView);
    }
    /**
     * 设置阅读情况，点击此条信息时，数据库数据置为1
     */
    private void setPublicInformationRead(){
        DepartmentInformationDataHelper datahelper = new DepartmentInformationDataHelper();
        datahelper.setRead(getIntentValuesId());
    }
    private void setContent(){
        departmentInformationOperation.SetDepartmentInformation(getIntentValuesId(), new DepartmentInformationOperation.TaskWork() {
            @Override
            public void onPostWork() {
                DepartmentInformationDataHelper datahelper = new DepartmentInformationDataHelper();//从数据库中取出信息放入页面中
                datahelper.getPublicInformation(mtvheadtitle,mtvName,mtvTime,mtvdepartName,mtvdepAttachment,webView,getIntentValuesId());
                datahelper.setToolTitle(getIntentValuesId(),mtvtoolbartitle);
                datahelper.recordReadStatus(getIntentValuesId());
            }
        });
    }
    //点击事件
    private void setOnClickListener(){
        mtvdepAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetDepartmentInformationDao departmentInformationDao = new SetDepartmentInformationDao();
                String s = departmentInformationDao.getName(getIntentValuesId());
                File file=new File(Environment.getExternalStoragePublicDirectory(Values.cachepath)+"/"+s);
                FileDownload.setContext(DepartmentInformationActivity.this);
                if(file.exists()) {
                    FileDownload.openfile(file);
                }else {

                    FileDownload.openordownlodfile(file, Values.ServerAddress+"/DepFileDown/" + getIntentValuesId());
                }
            }
        });

        mibtReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent();
//                intent.setClass(DepartmentInformationActivity.this,DepartmentInformationListActivity.class);
//                startActivity(intent);
                finishActivity();
            }
        });
    }

    private String getIntentValuesId(){
        Intent intent = getIntent();
        return intent.getStringExtra("id");
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
//            Intent intent = new Intent();
//            intent.setClass(DepartmentInformationActivity.this,DepartmentInformationListActivity.class);
//            startActivity(intent);
            finishActivity();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void setOnCreate(){
        SharedPreferences sharedPreferences = getSharedPreferences("DepartmentInformationActivityOnCreate", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putInt("DepartmentInformationActivityOnCreate",1);
        editor.apply();
    }

    private void finishActivity(){
        SharedPreferences sharedPreferences = getSharedPreferences("DepartmentInformationActivityOnCreate", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putInt("DepartmentInformationActivityOnCreate",0);
        editor.apply();
        finish();
    }

    /**
     * 在创建act前设置字体大小
     */
    private void setStyle(){
        SharedPreferences sharedPreferences = getSharedPreferences("TextMode",0);
        int TextMode = sharedPreferences.getInt("TextMode",1);

        if(TextMode==0){
            this.setTheme(R.style.Theme_Small);
        }else if(TextMode==1){
            this.setTheme(R.style.Theme_Standard);
        }else if(TextMode==2){
            this.setTheme(R.style.Theme_Large);
        }else {
            this.setTheme(R.style.Theme_Larger);
        }
    }

    public void downContent(){
//        String filePaths = "/storage/emulated/0/gsDownload/"+getIntentValuesId()+".html";
//        Log.i("qqqq",filePaths);
        String filePath = getExternalStorageDirectory()+"/gsDownload/"+getIntentValuesId()+".html";
        File fileCheck = new File(filePath);
        if(!fileCheck.exists()) {
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
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(Values.ServerAddress+"/DepDownload/" + getIntentValuesId()));
            request.setDestinationInExternalPublicDir("gsDownload", getIntentValuesId() + ".html");
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
