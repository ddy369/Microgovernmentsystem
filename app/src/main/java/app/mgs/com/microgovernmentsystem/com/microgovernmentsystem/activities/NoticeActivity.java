package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import app.mgs.com.microgovernmentsystem.R;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.adapters.AttachmentAdapter;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.adapters.NoticeMenuAdapter;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.Dao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.ListBeanAttachment;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.ListBeanMenu;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.operations.NoticeOperation;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.CalculateListViewTool;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.FileDownload;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.NoticeDatahelper;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Values;


public class NoticeActivity extends BaseActivity {

    public static NoticeActivity activity = null;

    public PopupWindow popupWindow = new PopupWindow();
    Context mcontext = this;
    NoticeOperation operation = new NoticeOperation(mcontext);//联网获取信息存入数据库
    private boolean isOuter = false;//监听弹窗是否处于弹出状态
    private TextView mtvTitle;
    private ImageButton imageButton ;
    private TextView  mtvdownload;
    private TextView mtvSender;
    private TextView mtvSendTime;
    private TextView mtvDepartment;
    private TextView mtvReceiver;
    private TextView mtvMessage;
    private TextView mtvReceipt;
    private ListView Attachment;
    private WebView mtvContent;
    private ImageButton mibtMenu;
    private ImageButton mibtReturn;
    private List<ListBeanAttachment> Attachmentlist=new ArrayList<>();
    private AttachmentAdapter adapter;
    private String Contenthtml;

    //全局变量
    private int flag = 0;//用来判断是由哪个页面跳转而来
    private String recordId;
    //downlaod
    private ProgressBar progressBar;
    DownloadManager downManager ;
    private DownLoadCompleteReceiver receiver;
    long idPro;

    /**
     * 取listview的高度
     */
    public static int getTotalHeightofListView(ListView listView) {
        CalculateListViewTool calculateListViewTool = new CalculateListViewTool(listView);
        return calculateListViewTool.CalculateHeight() + 45;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle();

        setContentView(R.layout.activity_notice);

        setOnCreate();

        activity = this;

        initView();//初始化控件

        initlistview();

        setOnClickListener();//监听事件

        getIntentValues();

        setContent();//设置页面内容

        setNoticeRead();//连接数据库更改阅读情况
    }

    private void initView() {
        mtvDepartment = (TextView) findViewById(R.id.act_notice_tv_department);
        mtvMessage = (TextView) findViewById(R.id.act_notice_tv_message);
        mtvReceiver = (TextView) findViewById(R.id.act_notice_tv_receiver);
        mtvReceipt = (TextView) findViewById(R.id.act_notice_tv_receipt);
        mtvTitle = (TextView) findViewById(R.id.act_notice_tv_title);
        mtvSender = (TextView) findViewById(R.id.act_notice_tv_sender);
        mtvSendTime = (TextView) findViewById(R.id.act_notice_tv_send_time);
        Attachment = (ListView) findViewById(R.id.attachmentListView);
        mtvContent = (WebView) findViewById(R.id.act_notice_tv_content);
        mibtMenu = (ImageButton) findViewById(R.id.act_notice_ibt_menu);
        mibtReturn = (ImageButton) findViewById(R.id.act_notice_ibt_return);
        imageButton=(ImageButton) findViewById(R.id.act_notice_ibt_download_attachment);
        mtvdownload=(TextView) findViewById(R.id.act_notice_tv_download_attachment);


    }

    private void initlistview()
    {
        adapter=new AttachmentAdapter(NoticeActivity.this,R.layout.adapter_for_listview_notice_attachmentlist,Attachmentlist);
        Attachment.setAdapter(adapter);
        Attachment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ListBeanAttachment listBeanAttachment = Attachmentlist.get(i);
                File file=new File(Environment.getExternalStorageDirectory()+"/"+Values.cachepath+"/"+listBeanAttachment.getName());
                FileDownload.setContext(NoticeActivity.this);
                if(file.exists()) {
                    FileDownload.openfile(file);
                }else {

                    FileDownload.openordownlodfile(file,Values.ServerAddress+"/download/"+ listBeanAttachment.getUrl());
                }
               /* downManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                progressBar = (ProgressBar) (findViewById(R.id.downProgress));
                IntentFilter filter = new IntentFilter();
                filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
                filter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);
                receiver = new DownLoadCompleteReceiver();
                registerReceiver(receiver, filter);
                String s = "http://"+listBeanAttachment.getUrl();
                Log.i("ssss",s);
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(s));
                request.setAllowedNetworkTypes(DownloadManager.Request.VISIBILITY_HIDDEN);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setTitle("通知附件下载");
                request.setDescription("正在下载");
                request.setAllowedOverRoaming(false);
                request.setDestinationInExternalFilesDir(NoticeActivity.activity, Environment.DIRECTORY_DOWNLOADS, "mydown");
                idPro = downManager.enqueue(request);
                ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
                ses.scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        queryTaskByIdandUpdateView(idPro);
                    }
                }, 0, 2, TimeUnit.SECONDS);*/
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
                Toast.makeText(NoticeActivity.this, "编号："+id+"的下载任务已经完成！", Toast.LENGTH_SHORT).show();
            }else if(intent.getAction().equals(DownloadManager.ACTION_NOTIFICATION_CLICKED)){
                Toast.makeText(NoticeActivity.this, "注意", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setContent() {
        //TODO:临时更改：先判断数据库表里是否有数据，如果有则直接从数据库表里取出，否则联网获取
        Dao dao = new Dao(NoticeActivity.activity, null, Values.database_version);
        SQLiteDatabase dbRead = dao.getReadableDatabase();
        Cursor cursor = dbRead.query("NOTICETABLE",null,"recordid=?",new String[]{recordId},null,null,null);
        Log.i("meng"," "+cursor.getCount());
        if (cursor.getCount()==0){
            operation.SetNotice(recordId, new NoticeOperation.TaskWork() {
                @Override
                public void onPostWork() {
                    NoticeDatahelper datahelper = new NoticeDatahelper();//从数据库中取出信息放入页面中
                    datahelper.getNotice(mtvTitle, mtvSender, mtvSendTime, mtvDepartment, mtvMessage, mtvReceiver, mtvReceipt
                            ,recordId);
                    setwebview();
                    datahelper.getAttachment(Attachmentlist,recordId,imageButton,mtvdownload);

                    setListViewHeightBasedOnChildren(Attachment);
                    adapter.notifyDataSetChanged();
                }
                @Override
                public void onbackdown()
                {
                    finishActivity();
                }
            });
        }else {
            NoticeDatahelper datahelper = new NoticeDatahelper();//从数据库中取出信息放入页面中
            datahelper.getNotice(mtvTitle, mtvSender, mtvSendTime, mtvDepartment, mtvMessage, mtvReceiver, mtvReceipt,recordId);
            setwebview();
            datahelper.getAttachment(Attachmentlist,recordId,imageButton,mtvdownload);
            setListViewHeightBasedOnChildren(Attachment);
            adapter.notifyDataSetChanged();
        }
        cursor.close();
    }

    private  void  setwebview()
    {
        NoticeDatahelper datahelper = new NoticeDatahelper();//从数据库中取出信息放入页面中
        Contenthtml=datahelper.gethtml(recordId);
        Log.d("js", "setwebview: "+Contenthtml);
        if(!Contenthtml.isEmpty()) {
            mtvContent.getSettings().setJavaScriptEnabled(true);
            // mtvContent.loadData(Contenthtml, "text/html", "UTF-8");
            FileDownload.setContext(NoticeActivity.this);
            mtvContent.addJavascriptInterface(new FileDownload(), "FileDownload");
            mtvContent.loadDataWithBaseURL(null, Contenthtml, "text/html", "utf-8", null);
         //   mtvContent.setVisibility(View.VISIBLE);






        }else
        {
            if (mtvContent != null) {
                ViewGroup parent = (ViewGroup) mtvContent.getParent();
                if (parent != null) {
                    parent.removeView(mtvContent);
                }
                mtvContent.removeAllViews();
                mtvContent.destroy();
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)mtvdownload.getLayoutParams();
                lp.addRule(RelativeLayout.BELOW,R.id.act_notice_content_label);
                mtvdownload.setLayoutParams(lp);
                lp = (RelativeLayout.LayoutParams)imageButton.getLayoutParams();
                lp.addRule(RelativeLayout.BELOW,R.id.act_notice_content_label);
                imageButton.setLayoutParams(lp);

            }
        }
    }

    private void setOnClickListener() {
        mibtMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOuter) {
                    popupWindow.dismiss();
                    isOuter = false;
                } else {
                    showPopupWindow();//显示自定义弹窗
                    isOuter = true;
                }
            }
        });

        mibtReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  Intent intent = new Intent();
                //   intent.setClass(NoticeActivity.this,NoticeListActivity.class);
                //   startActivity(intent);
                finishActivity();
            }
        });

        mtvDepartment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
                TextView tv = new TextView(mcontext);
                tv.setText("通知到部门详情");	//内容
                tv.setTextSize(22);//字体大小
                tv.setPadding(80,50,10,10);
                tv.setTextColor(0xFF0984EC);
                builder.setCustomTitle(tv);
                builder.setMessage(mtvDepartment.getText());
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.create().show();
            }
        });

        mtvReceiver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
                TextView tv = new TextView(mcontext);
                tv.setText("通知到人详情");	//内容
                tv.setTextSize(22);//字体大小
                tv.setPadding(80,50,10,10);
                tv.setTextColor(0xFF0984EC);
                builder.setCustomTitle(tv);
                builder.setMessage(mtvReceiver.getText());
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.create().show();
            }
        });
    }

    /**
     * 设置阅读情况，点击此条信息时，数据库数据置为1
     */
    private void setNoticeRead() {
        NoticeDatahelper datahelper = new NoticeDatahelper();
        datahelper.setRead(recordId);
    }

    public void setOnCreate() {
        SharedPreferences sharedPreferences = getSharedPreferences("NoticeActivityOnCreate", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putInt("NoticeActivityOnCreate", 1);
        editor.apply();
    }

    private void finishActivity() {
        SharedPreferences sharedPreferences = getSharedPreferences("NoticeActivityOnCreate", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putInt("NoticeActivityOnCreate", 0);
        editor.apply();
        finish();
    }

    /**
     * 在创建act前设置字体大小
     */
    private void setStyle() {
        SharedPreferences sharedPreferences = getSharedPreferences("TextMode", 0);
        int TextMode = sharedPreferences.getInt("TextMode", 1);

        if (TextMode == 0) {
            this.setTheme(R.style.Theme_Small);
        } else if (TextMode == 1) {
            this.setTheme(R.style.Theme_Standard);
        } else if (TextMode == 2) {
            this.setTheme(R.style.Theme_Large);
        } else if (TextMode == 3) {
            this.setTheme(R.style.Theme_Larger);
        } else {
            this.setTheme(R.style.Theme_Largest);
        }
    }

    /**
     * 自定义弹窗
     */
    private void showPopupWindow() {
        final ListView listView;
        final RelativeLayout rlAdpForLvNoticeMenu;
        rlAdpForLvNoticeMenu = (RelativeLayout) findViewById(R.id.adp_for_lv_notice_menu_rl);

        View contentView = LayoutInflater.from(mcontext).inflate(R.layout.listview_notice_menu,
                rlAdpForLvNoticeMenu, false);
        listView = (ListView) contentView.findViewById(R.id.lv_notice_menu_listview);
        NoticeMenuAdapter listViewAdapter = new NoticeMenuAdapter(mcontext);
        listViewAdapter.setListBeanMenus(arrayListBeans());
        listView.setAdapter(listViewAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    //TODO:转发通知
                    Intent intent = new Intent();
                    intent.setClass(NoticeActivity.this, PublishNoticeActivity.class);
                    intent.putExtra("mtvTitle","[转发]"+ mtvTitle.getText().toString());
                  //  intent.putExtra("mtvSender", mtvSender.getText().toString());
                  //  intent.putExtra("mtvSendTime", mtvSendTime.getText().toString());
                  //  intent.putExtra("mtvDepartment", mtvDepartment.getText().toString());
                  //  intent.putExtra("mtvMessage", mtvMessage.getText().toString());
                 //   intent.putExtra("mtvReceiver", mtvReceiver.getText().toString());
                 //   intent.putExtra("mtvReceipt", mtvReceipt.getText().toString());
                    String tem="以下为原通知\n发送人："+mtvSender.getText().toString()+
                            "\n发送时间："+mtvSendTime.getText().toString()+
                            "\n通知到部门："+mtvDepartment.getText().toString()+
                            "\n通知到人："+mtvReceiver.getText().toString()+
                            "\n短信提醒："+mtvMessage.getText().toString()+
                            "\n回执："+mtvReceipt.getText().toString();
//                    tem+"\n\n"+
                    intent.putExtra("mtvContent",  Contenthtml);
//                    intent.putExtra("attachmenturls",attachmenturls);
                    intent.putExtra("recordid", recordId);
                    startActivity(intent);

                } else if (i == 1) {
                    //TODO:复制通知
                    String attachmenturls="";

                    if(Attachmentlist.size()>0)
                    {
                        for(ListBeanAttachment a:Attachmentlist)
                        {
                            if(attachmenturls.length()<2)
                                attachmenturls=attachmenturls+a.getUrl();
                            else
                                attachmenturls=attachmenturls+","+a.getUrl();
                        }
                    }
                    Intent intent = new Intent();
                    intent.setClass(NoticeActivity.this, PublishNoticeActivity.class);
                    intent.putExtra("mtvTitle",mtvTitle.getText().toString());
                    intent.putExtra("attachmenturls",attachmenturls);
                    //  intent.putExtra("mtvSender", mtvSender.getText().toString());
                    //  intent.putExtra("mtvSendTime", mtvSendTime.getText().toString());
                    //  intent.putExtra("mtvDepartment", mtvDepartment.getText().toString());
                    //  intent.putExtra("mtvMessage", mtvMessage.getText().toString());
                    //   intent.putExtra("mtvReceiver", mtvReceiver.getText().toString());
                    //   intent.putExtra("mtvReceipt", mtvReceipt.getText().toString());
                    intent.putExtra("mtvContent", Contenthtml);
                    startActivity(intent);

                } else if (i == 2){
                    //TODO:导入日程
                    Intent intent = new Intent();
                    intent.setClass(NoticeActivity.this, MemorandumActivity.class);
                    intent.putExtra("import",1);
                    intent.putExtra("mtvTitle",mtvTitle.getText().toString());
                    intent.putExtra("mtvContent", Contenthtml);
                    startActivity(intent);
                } else {
                    //TODO:阅读情况
                    Intent intent = new Intent();
                    intent.setClass(NoticeActivity.this, ReadDetailActivity.class);
                    intent.putExtra("id",recordId);
                    startActivity(intent);

                }
            }
        });
        listViewAdapter.notifyDataSetChanged();
        int H = getTotalHeightofListView(listView);

        popupWindow.setContentView(contentView);
        popupWindow.setHeight(H);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(mcontext, R.drawable.ic_ga_menu_24dp));
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        //显示popwindow
        popupWindow.showAsDropDown(findViewById(R.id.act_notice_ibt_menu), 0, 0);
    }

    /**
     * 往adapter中添加数据
     * 返回数组
     */
    public ArrayList<ListBeanMenu> arrayListBeans() {
        ArrayList<ListBeanMenu> listBeanMenus = new ArrayList<>();
        ListBeanMenu listBeanMenu = new ListBeanMenu();
        listBeanMenu.setListViewItem("转发");
        listBeanMenus.add(listBeanMenu);
        ListBeanMenu listBeanMenu1 = new ListBeanMenu();
        listBeanMenu1.setListViewItem("复制");
        listBeanMenus.add(listBeanMenu1);
        ListBeanMenu listBeanMenu2 = new ListBeanMenu();
        listBeanMenu2.setListViewItem("导入日程");
        listBeanMenus.add(listBeanMenu2);
        ListBeanMenu listBeanMenu3 = new ListBeanMenu();
        if (flag==1){
            listBeanMenu3.setListViewItem("阅读情况");
            listBeanMenus.add(listBeanMenu3);
        }
        return listBeanMenus;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
           /* Intent intent = new Intent();
            intent.setClass(NoticeActivity.this,NoticeListActivity.class);
            startActivity(intent);*/
            finishActivity();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
    *获取通知id，加判断字段来识别通知由哪个页面跳转而来，若为通知列表则flag=0;若为已发布通知列表则flag=1
     */
    private void getIntentValues() {
        Intent intent = getIntent();
        if (intent.getStringExtra("selfFlag").equals("1"))
            flag = 1;
        else
            flag = 0;
        recordId = intent.getStringExtra("recordid");
    }
    /**
     * 动态设置ListView的高度
     * @param listView
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        if(listView == null) return;
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight()*2;
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

}
