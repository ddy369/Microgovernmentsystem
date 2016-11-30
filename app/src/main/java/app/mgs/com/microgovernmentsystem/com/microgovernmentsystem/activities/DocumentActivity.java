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
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import app.mgs.com.microgovernmentsystem.R;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.SetDocumentDao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.operations.DocumentOperation;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.DocumentDataHelper;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.FileDownload;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Values;

public class DocumentActivity extends BaseActivity {

    public static DocumentActivity activity = null;
    Context mcontext = this;

    private TextView mtvDocumentTitle;
    private TextView mtvNumber;
    private TextView mtvSender;
    private TextView mtvCC;
    private TextView mtvSignDate;
    private TextView mtvPrintDate;
    private TextView mtvTitle;
    private TextView mtvDense;
    private TextView mtvUrgency;
    private TextView mtvText;
    private TextView mtvAttachment;
    private TextView mtvRemarks;
    private TextView mtvExplain;
    private ImageButton mibtReturn;
    private ImageButton mibWrite;

    //downlaod
    private ProgressBar progressBar;
    DownloadManager downManager ;
    private DownLoadCompleteReceiver receiver;
    long idPro;


    DocumentOperation documentOperation = new DocumentOperation(mcontext);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle();

        setContentView(R.layout.activity_document);

        initView();

        setOnClickListener();

        activity = this;

        setOnCreate();

        setContent();
    }
    private void setContent() {
        SetDocumentDao setDocumentDao = new SetDocumentDao();
        int i = setDocumentDao.selectCount(getIntentValues(),getIntentValuesPurview());
        int j = setDocumentDao.selectCount(getIntentValues());
        if (i == 1 && j == 1) {
            DocumentDataHelper datahelper = new DocumentDataHelper();//从数据库中取出信息放入页面中
            datahelper.getDocument(
                    mtvDocumentTitle,
                    mtvNumber,
                    mtvSender,
                    mtvCC,
                    mtvSignDate,
                    mtvPrintDate,
                    mtvTitle,
                    mtvDense,
                    mtvUrgency,
                    mtvText,
                    mtvAttachment,
                    mtvRemarks,
                    mtvExplain, getIntentValues());
        } else {
            documentOperation.SetDocument(getIntentValues(), new DocumentOperation.TaskWork() {
                @Override
                public void onPostWork() {
                    DocumentDataHelper datahelper = new DocumentDataHelper();//从数据库中取出信息放入页面中
                    datahelper.getDocument(
                            mtvDocumentTitle,
                            mtvNumber,
                            mtvSender,
                            mtvCC,
                            mtvSignDate,
                            mtvPrintDate,
                            mtvTitle,
                            mtvDense,
                            mtvUrgency,
                            mtvText,
                            mtvAttachment,
                            mtvRemarks,
                            mtvExplain, getIntentValues());
                }
            });
        }
    }



    private String getIntentValues(){
        Intent intent = getIntent();
        return intent.getStringExtra("id");
    }

    private String getIntentValuesPurview(){
        Intent intent = getIntent();
        return intent.getStringExtra("purview");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
//            Intent intent = new Intent();
//            intent.setClass(DocumentActivity.this,DocumentListActivity.class);
//            startActivity(intent);
            finishActivity();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void setOnCreate(){
        SharedPreferences sharedPreferences = getSharedPreferences("DocumentActivityOnCreate", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putInt("DocumentActivityOnCreate",1);
        editor.apply();
    }

    private void finishActivity(){
        SharedPreferences sharedPreferences = getSharedPreferences("DocumentActivityOnCreate", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putInt("DocumentActivityOnCreate",0);
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

    private void initView(){
//        map.put(mtvDocumentTitle=(TextView) findViewById(R.id.act_document_tv_document_title),"mainTitle");
//        map.put(mtvSender = (TextView) findViewById(R.id.act_document_tv_sender),"docPeople");
//        map.put(mtvCC = (TextView) findViewById(R.id.act_document_tv_CC),"signPeople");
//        map.put( mtvPrintDate = (TextView) findViewById(R.id.act_document_tv_print_date),"docPrintDate");
//        map.put( mtvTitle = (TextView) findViewById(R.id.act_document_tv_title),"title");
//        map.put( mtvDense = (TextView) findViewById(R.id.act_document_tv_dense),"docSecret");
//        map.put( mtvUrgency = (TextView) findViewById(R.id.act_document_tv_urgency),"docUrgent");
//        mtvText = (Button) findViewById(R.id.act_document_tv_text);
//        mtvAttachment = (Button) findViewById(R.id.act_document_tv_attachment);
//        map.put( mtvRemarks = (TextView) findViewById(R.id.act_document_tv_remarks),"docMark");
//        map.put( mtvExplain = (TextView) findViewById(R.id.act_document_tv_explain),"docExplain");
        mibtReturn = (ImageButton) findViewById(R.id.act_document_ibt_return);
        mtvDocumentTitle=(TextView) findViewById(R.id.act_document_tv_document_title);
        mtvNumber = (TextView) findViewById(R.id.act_document_tv_number);
        mtvSender = (TextView) findViewById(R.id.act_document_tv_sender);
        mtvCC = (TextView) findViewById(R.id.act_document_tv_CC);
        mtvSignDate = (TextView) findViewById(R.id.act_document_tv_sign_date);
        mtvPrintDate = (TextView) findViewById(R.id.act_document_tv_print_date);
        mtvTitle = (TextView) findViewById(R.id.act_document_tv_title);
        mtvDense = (TextView) findViewById(R.id.act_document_tv_dense);
        mtvUrgency = (TextView) findViewById(R.id.act_document_tv_urgency);
        mtvText = (TextView) findViewById(R.id.act_document_tv_text);
        mtvAttachment = (TextView) findViewById(R.id.act_document_tv_attachment);
        mtvRemarks = (TextView) findViewById(R.id.act_document_tv_remarks);
        mtvExplain = (TextView) findViewById(R.id.act_document_tv_explain);
        mibWrite = (ImageButton) findViewById(R.id.act_document_list_ibt_write);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setOnClickListener(){
        mibWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("kindofpublish",2);
                intent.putExtra("docId",getIntentValues());
                intent.setClass(DocumentActivity.this,PublishNoticeActivity.class);
                startActivity(intent);
            }
        });

        mtvText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetDocumentDao setDocumentDao = new SetDocumentDao();
                String[] str = setDocumentDao.attachment(getIntentValues());
                File file=new File(Environment.getExternalStorageDirectory()+"/"+Values.cachepath+"/"+str[2]);
                FileDownload.setContext(DocumentActivity.this);
                if(file.exists()) {
                    FileDownload.openfile(file);
                }else {
                    FileDownload.openordownlodfile(file, Values.ServerAddress+"/DocContentDown/" + getIntentValues());
                }

//                File folder = Environment.getExternalStoragePublicDirectory("gsDownload");
//                if (!folder.exists() || !folder.isDirectory()) {
//                    folder.mkdirs();
//                }
//                downManager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
//                progressBar = (ProgressBar) (findViewById(R.id.downProgress));
//                IntentFilter filter = new IntentFilter();
//                filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
//                filter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);
//                receiver = new DownLoadCompleteReceiver();
//                registerReceiver(receiver, filter);
//                DownloadManager.Request request = new DownloadManager.Request(Uri.parse();
//                request.setDestinationInExternalPublicDir("gsDownload", getIntentValues()+".doc");
//                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION);
//                request.setTitle("公文正文下载");
//                request.setDescription("正在下载");
//                request.setAllowedOverRoaming(false);
//                idPro= downManager.enqueue(request);
//                ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
//                ses.scheduleAtFixedRate(new Runnable() {
//                    @Override
//                    public void run() {
//                        queryTaskByIdandUpdateView(idPro);
//                    }
//                }, 0, 2, TimeUnit.SECONDS);
            }
        });

        mtvAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DocumentDataHelper documentDataHelper = new DocumentDataHelper();

              /*  File file=new File(Environment.getExternalStorageDirectory()+"/myapp/"+getIntentValues());
                FileDownload.setContext(DocumentActivity.this);
                //openordownlodfile接受两个参数，第一个File，第二个Url，如果file存在则打开file，不存在则下载url指向的文件并把它命名为file的名字。
                FileDownload.openordownlodfile(file,documentDataHelper.getURL(getIntentValues()));*/
                File folder = Environment.getExternalStoragePublicDirectory("gsDownload");
                if (!folder.exists() || !folder.isDirectory()) {
                    folder.mkdirs();
                }
                downManager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
                progressBar = (ProgressBar) (findViewById(R.id.downProgress));
                IntentFilter filter = new IntentFilter();
                filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
                filter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);
                receiver = new DownLoadCompleteReceiver();
                registerReceiver(receiver, filter);
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse("http://"+documentDataHelper.getURL(getIntentValues())));
                request.setDestinationInExternalPublicDir("gsDownload", "sssss.xls");
                //request.setAllowedNetworkTypes(DownloadManager.Request.VISIBILITY_HIDDEN);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                request.setTitle("公文附件下载");
                request.setDescription("正在下载");
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
        });



        mibtReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(receiver !=null ){
                unregisterReceiver(receiver);}
                finishActivity();
            }
        });
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
            if(intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)){
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                Toast.makeText(DocumentActivity.this, "编号："+id+"的下载任务已经完成！", Toast.LENGTH_SHORT).show();
            }else if(intent.getAction().equals(DownloadManager.ACTION_NOTIFICATION_CLICKED)){
                Toast.makeText(DocumentActivity.this, "注意", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
