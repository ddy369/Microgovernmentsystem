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
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.SetMagazineDao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.operations.MagazineListOperation;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.operations.MagazineOperation;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.MagazineDataHelper;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Values;

import static android.os.Environment.getExternalStorageDirectory;


public class MagazineActivity extends BaseActivity {

    public static MagazineActivity activity = null;
    Context mcontext = this;
    private TextView mtvTitle;
    private TextView mtvIssue;
    private TextView mtvDepartment;
    private TextView mtvDate;
    private WebView mwebView;
    private ImageButton mibtReturn;
    //downlaod
    private ProgressBar progressBar;
    DownloadManager downManager ;
    private DownLoadCompleteReceiver receiver;
    long idPro;
    private String[] str = null;

    MagazineOperation magazineOperation = new MagazineOperation(mcontext);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle();

        setContentView(R.layout.activity_magazine);

        initView();

        setContent();

        setOnClickListener();

        activity = this;

        setOnCreate();

        downContent();
    }


    private void setContent(){
                MagazineDataHelper datahelper = new MagazineDataHelper();
                datahelper.getMagazine(mcontext,getIntentValues(),mtvTitle, mtvIssue, mtvDepartment,
                        mtvDate,mwebView,getIntentValuesType(),getIntentValuesSysId(),getIntentValuesYear_no());
                datahelper.recordReadStatus(mcontext,getIntentValues());
    }
    private String getIntentValues(){
        Intent intent = getIntent();
        return intent.getStringExtra("id");
    }
    private String getIntentValuesType(){
        Intent intent = getIntent();
        return intent.getStringExtra("contentType");
    }
    private String getIntentValuesSysId(){
        Intent intent = getIntent();
        return intent.getStringExtra("sysId");
    }
    private String getIntentValuesYear_no(){
        Intent intent = getIntent();
        return intent.getStringExtra("year_no");
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
//            Intent intent = new Intent();
//            intent.setClass(MagazineActivity.this,MagazineListActivity.class);
//            startActivity(intent);
            finishActivity();
        }
        return super.onKeyDown(keyCode, event);
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
        }else if (TextMode==3){
            this.setTheme(R.style.Theme_Larger);
        }else {
            this.setTheme(R.style.Theme_Largest);
        }
    }

    private void initView(){
        mtvTitle = (TextView) findViewById(R.id.act_magazine_tv_title);
        mtvIssue = (TextView) findViewById(R.id.act_magazine_tv_issue);
        mtvDepartment = (TextView) findViewById(R.id.act_magazine_tv_department);
        mtvDate = (TextView) findViewById(R.id.act_magazine_tv_date);
        mibtReturn = (ImageButton) findViewById(R.id.act_magazine_ibt_return);
        mwebView = (WebView)findViewById(R.id.magazine_webView);
    }

    private void setOnClickListener(){
        mibtReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent();
//                intent.setClass(MagazineActivity.this,MagazineListActivity.class);
//                startActivity(intent);
                finishActivity();
            }
        });
    }

    public void setOnCreate(){
        SharedPreferences sharedPreferences = getSharedPreferences("MagazineActivityOnCreate", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putInt("MagazineActivityOnCreate",1);
        editor.apply();
    }

    private void finishActivity(){
        SharedPreferences sharedPreferences = getSharedPreferences("MagazineActivityOnCreate", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putInt("MagazineActivityOnCreate",0);
        editor.apply();
        if(receiver !=null ){
            unregisterReceiver(receiver);}
        finish();
    }

    public void downContent(){
        String idList = "";
        if(getIntentValuesType().length() < 1 ) {
            SetMagazineDao setMagazineDao = new SetMagazineDao();
            idList = setMagazineDao.getID(getIntentValues(), mcontext);
        }else{
            idList = getIntentValuesSysId();
        }
        String[] sss = idList.split(",");//根据“,”区分
            for(String s : sss){
//                String filePath = "/storage/emulated/0/gsDownload/"+s+".html";
                String filePath = getExternalStorageDirectory()+"/gsDownload/"+s+".html";
                File fileCheck = new File(filePath);
                if(!fileCheck.exists()) {
                File folder = Environment.getExternalStoragePublicDirectory("gsDownload");
                if (!folder.exists() || !folder.isDirectory()) {
                    folder.mkdirs();
                }
                downManager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
                progressBar = (ProgressBar) (findViewById(R.id.downProgress));
                IntentFilter filter = new IntentFilter();
                filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
                receiver = new DownLoadCompleteReceiver();
                registerReceiver(receiver, filter);
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(Values.ServerAddress+"/DownMagazine/"+s));
                request.setDestinationInExternalPublicDir("gsDownload", s+".html");
                request.setAllowedOverRoaming(false);
                idPro= downManager.enqueue(request);
                ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
                ses.scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        queryTaskByIdandUpdateView(idPro);
                    }
                }, 0, 2, TimeUnit.SECONDS);
            }
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
