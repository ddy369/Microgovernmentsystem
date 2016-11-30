package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import java.io.File;

import app.mgs.com.microgovernmentsystem.R;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.Dao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.operations.RemoveBindingOperation;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.CheckVersionUpdate;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Datahelper;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Values;
import cn.jpush.android.api.JPushInterface;


public class SettingActivity extends BaseActivity {
    private ImageButton mibtReturn;
    private ImageButton mibtAlert;
    private RelativeLayout mrlTop;
    private RelativeLayout mrlMid;
    private RelativeLayout mrlBottom;
    private RelativeLayout mrldeletecache;
    private LinearLayout mllUnderside;
    private Button mbtNewVersion;
    private TextView mtvNewVersion;
    private TextView mtvFont;
    private Dao dao = new Dao(SettingActivity.this, null, Values.database_version);
    private AlertDialog dialog;
    Context mcontext = this;
    private CheckVersionUpdate versionUpdate=new CheckVersionUpdate();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle();//先设置主题

        setContentView(R.layout.activity_setting);

        initView();

        setAlertButton();

        setOnClickListener();

        setTvFont();//设置文本框内容（当前字体大小
}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog!=null)
            dialog.dismiss();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent();
            intent.setClass(SettingActivity.this,MainFaceActivity.class);
            intent.putExtra("select",3);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setTvFont(){
        int TextMode = getSharedPreference();
        if (TextMode==0){
            mtvFont.setText("小");
        }else if (TextMode==1){
            mtvFont.setText("标准");
        }else if (TextMode==2){
            mtvFont.setText("大");
        }else if (TextMode==3){
            mtvFont.setText("超大");
        }else {
            mtvFont.setText("特大");
        }
    }

    /**
     * 在创建act前设置字体大小
     */
    void setStyle(){
        int TextMode = getSharedPreference();

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
        mibtReturn = (ImageButton) findViewById(R.id.act_setting_ibt_return);
        mrlTop = (RelativeLayout) findViewById(R.id.act_setting_rl_top);
        mrlMid = (RelativeLayout) findViewById(R.id.act_setting_rl_mid);
        mrlBottom = (RelativeLayout) findViewById(R.id.act_setting_rl_bottom);
        mbtNewVersion = (Button) findViewById(R.id.act_setting_bt_new_version);
        mtvNewVersion = (TextView) findViewById(R.id.act_setting_tv_new_version);
        try {
            mtvNewVersion.setText(versionUpdate.getVersionName());
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        mtvFont = (TextView) findViewById(R.id.act_setting_tv_font);
        mibtAlert = (ImageButton) findViewById(R.id.act_setting_ibt_message_alert);
        mllUnderside = (LinearLayout) findViewById(R.id.act_setting_ll_underside);
        mrldeletecache=(RelativeLayout) findViewById(R.id.act_setting_rl_clear);
    }

    private void setOnClickListener(){
        mbtNewVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                versionUpdate.checkVersion(new CheckVersionUpdate.callback() {
                    @Override
                    public void callback() {
                        Datahelper.showToast("当前版本已是最新版本。");
                    }
                });
            }
        });

        mrlTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int TextMode = getSharedPreference();

                final AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
                TextView tv = new TextView(mcontext);
                tv.setText("请选择字体大小");	//内容
                tv.setTextSize(22);//字体大小
                tv.setPadding(80,50,10,10);
                tv.setTextColor(0xFF0984EC);

                builder.setSingleChoiceItems( new String[]{ "小","标准","大","超大","特大" }, TextMode ,
                        new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i==0){
                                    setSharedPreferences(0);
                                    restartAct();
                                }else if (i==1){
                                    setSharedPreferences(1);
                                    restartAct();
                                }else if (i==2){
                                    setSharedPreferences(2);
                                    restartAct();
                                }else if (i==3){
                                    setSharedPreferences(3);
                                    restartAct();
                                }else if (i==4){
                                    setSharedPreferences(4);
                                    restartAct();
                                }
                            }
                        });
                builder.setCustomTitle(tv);
                builder.setNegativeButton("取消",null);
                dialog = builder.create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    private Button negativeBtn ;
                    private Button positiveBtn;
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        positiveBtn = (dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                        negativeBtn = (dialog).getButton(DialogInterface.BUTTON_NEGATIVE);
                        negativeBtn.setTextSize(18);
                        positiveBtn.setTextSize(18);
                        positiveBtn.setTextColor(0xFF0984EC);
                        negativeBtn.setTextColor(0xFF0984EC);
                    }
                });
                dialog.show();
            }
        });
        mrldeletecache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SettingActivity.this);
                builder.setMessage("清除缓存将删除所有已下载附件并清空已缓存数据，是否继续？");
                builder.setTitle("提示");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //删除已下载附件
                        File file=new File(Environment.getExternalStorageDirectory()+"/"+Values.cachepath+"/");
                        deleteAllFiles(file);
                        //清楚数据库缓存
                        SQLiteDatabase db = dao.getWritableDatabase();
                        db.delete("UserTable", null, null);
                        db.delete("NOTICELISTTABLE", null, null);
                        db.delete("RECORDIDTABLE", null, null);
                        db.delete("NOTICETABLE", null, null);
                        db.delete("NoticeAttachmentTable", null, null);
                        db.delete("DocumentTable", null, null);
                        db.delete("DocumentFile", null, null);
                        db.delete("DocumentDetail", null, null);
                        db.delete("MagazineTable", null, null);
                        db.delete("PublicInformationListTable", null, null);
                        db.delete("PublicInformationTable", null, null);
                        db.delete("DepartmentInformationListTable", null, null);
                        db.delete("DepartmentInformationTable", null, null);
                        db.delete("RemarkTable", null, null);
                        db.delete("PublishedNoticeListTable", null, null);
                        db.delete("PublishedNoticeTable", null, null);
                        db.delete("PublishedNoticeAttachmentTable", null, null);
                        db.delete("DepartmentTable", null, null);
                        db.delete("ReceiverTable", null, null);
                        db.close();
                        Datahelper.showToast("缓存清理成功");
                        //    Main.this.finish();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();

            }
        });
        mibtReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(SettingActivity.this,MainFaceActivity.class);
                intent.putExtra("select",3);
                startActivity(intent);
                finish();
            }
        });

        mrlMid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean flag = JPushInterface.isPushStopped(SettingActivity.this);
                if (flag){
                    JPushInterface.resumePush(SettingActivity.this);
                    mibtAlert.setImageDrawable(ContextCompat.getDrawable(mcontext,R.drawable.ic_open_24dp));
                }else {
                    JPushInterface.stopPush(SettingActivity.this);
                    mibtAlert.setImageDrawable(ContextCompat.getDrawable(mcontext,R.drawable.ic_close_24dp));
                }

            }
        });

        mrlBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
                TextView tv = new TextView(mcontext);
                tv.setText("解除绑定提示");	//内容
                tv.setTextSize(22);//字体大小
                tv.setPadding(80,50,10,10);
                tv.setTextColor(0xFF0984EC);
                builder.setMessage("确认解除绑定吗？");
                builder.setCustomTitle(tv);
//                builder.setTitle("解除绑定提示");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        JPushInterface.setAlias(mcontext,"",null);
                        EMClient.getInstance().logout(true, new EMCallBack() {

                            @Override
                            public void onSuccess() {
                                // TODO Auto-generated method stub
                            }

                            @Override
                            public void onProgress(int progress, String status) {
                                // TODO Auto-generated method stub

                            }

                            @Override
                            public void onError(int code, String message) {
                                // TODO Auto-generated method stub

                            }
                        });
                        //删除已下载附件
                        File file=new File(Environment.getExternalStorageDirectory()+"/"+Values.cachepath+"/");
                        deleteAllFiles(file);
                        //清楚数据库缓存
                        SQLiteDatabase db = dao.getWritableDatabase();
                        db.delete("UserTable", null, null);
                        db.delete("NOTICELISTTABLE", null, null);
                        db.delete("RECORDIDTABLE", null, null);
                        db.delete("NOTICETABLE", null, null);
                        db.delete("NoticeAttachmentTable", null, null);
                        db.delete("DocumentTable", null, null);
                        db.delete("DocumentDetail", null, null);
                        db.delete("MagazineTable", null, null);
                        db.delete("PublicInformationListTable", null, null);
                        db.delete("PublicInformationTable", null, null);
                        db.delete("DepartmentInformationListTable", null, null);
                        db.delete("DepartmentInformationTable", null, null);
                        db.delete("RemarkTable", null, null);
                        db.delete("PublishedNoticeListTable", null, null);
                        db.delete("PublishedNoticeTable", null, null);
                        db.delete("PublishedNoticeAttachmentTable", null, null);
                        db.delete("DepartmentTable", null, null);
                        db.delete("ReceiverTable", null, null);
                        db.delete("ReadStatusTable",null,null);
                        db.close();
                        RemoveBindingOperation operation = new RemoveBindingOperation(mcontext);
                        operation.Remove(mllUnderside);
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                dialog = builder.create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    private Button negativeBtn ;
                    private Button positiveBtn;
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        positiveBtn = (dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                        negativeBtn = (dialog).getButton(DialogInterface.BUTTON_NEGATIVE);
                        negativeBtn.setTextSize(18);
                        positiveBtn.setTextSize(18);
                        positiveBtn.setTextColor(0xFF0984EC);
                        negativeBtn.setTextColor(0xFF0984EC);
                    }
                });
                dialog.show();
            }
        });
    }

    private void setAlertButton(){
        boolean flag = JPushInterface.isPushStopped(SettingActivity.this);
        if (!flag){
          //  JPushInterface.resumePush(SettingActivity.this);
            mibtAlert.setImageDrawable(ContextCompat.getDrawable(mcontext,R.drawable.ic_open_24dp));
        }else {
          //  JPushInterface.stopPush(SettingActivity.this);
            mibtAlert.setImageDrawable(ContextCompat.getDrawable(mcontext,R.drawable.ic_close_24dp));
        }
    }

    private int getSharedPreference(){
        SharedPreferences sharedPreferences = getSharedPreferences("TextMode",MODE_PRIVATE);
        return sharedPreferences.getInt("TextMode",1);
    }

    private void setSharedPreferences(int TextMode){
        SharedPreferences sharedPreferences = getSharedPreferences("TextMode", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putInt("TextMode",TextMode);
        editor.apply();
    }

    private void restartAct(){
        //restart act
        finishActivity();
        Intent intent = new Intent();
        intent.setClass(SettingActivity.this,SettingActivity.class);
        startActivity(intent);
        finish();
    }

    private void finishActivity(){
        int PublishNoticeActivityOnCreate = getOnCreate("PublishNoticeActivityOnCreate");
        int NoticeActivityOnCreate = getOnCreate("NoticeActivityOnCreate");
        int MainFaceActivityOnCreate = getOnCreate("MainFaceActivityOnCreate");
        int DocumentListActivityOnCreate = getOnCreate("DocumentListActivityOnCreate");
        int NoticeListActivityOnCreate = getOnCreate("NoticeListActivityOnCreate");
        int DocumentActivityOnCreate = getOnCreate("DocumentActivityOnCreate");
        int MagazineListActivityOnCreate = getOnCreate("MagazineListActivityOnCreate");
        int MagazineActivityOnCreate = getOnCreate("MagazineActivityOnCreate");
        int PublicInformationListActivityOnCreate = getOnCreate("PublicInformationListActivityOnCreate");
        int PublicInformationActivityOnCreate = getOnCreate("PublicInformationActivityOnCreate");
        int PublishNoticeChoiceDepartmentActivityOnCreate = getOnCreate("PublishNoticeChoiceDepartmentActivityOnCreate");
        int ImportContactsActivityOnCreate = getOnCreate("ImportContactsActivityOnCreate");
        int PublishNoticeChoiceReceiverActivityOnCreate = getOnCreate("PublishNoticeChoiceReceiverActivityOnCreate");
        int FeedBackActivityOnCreate = getOnCreate("FeedBackActivityOnCreate");
        int ContactsInformationActivityOnCreate = getOnCreate("ContactsInformationActivityOnCreate");
        int PublishedNoticeListActivityOnCreate = getOnCreate("PublishedNoticeListActivityOnCreate");
        int AboutActivityOnCreate = getOnCreate("AboutActivityOnCreate");

        if (AboutActivityOnCreate==1){
            AboutActivity.activity.finish();
        }
        if (PublishedNoticeListActivityOnCreate==1){
            PublishedNoticeListActivity.activity.finish();
        }
        if (ContactsInformationActivityOnCreate==1){
            ContactsInformationActivity.activity.finish();
        }
        if (FeedBackActivityOnCreate==1){
            FeedbackActivity.activity.finish();
        }
        if (PublishNoticeChoiceReceiverActivityOnCreate==1){
            PublishNoticeChoiceReceiverActivity.activity.finish();
        }
        if (ImportContactsActivityOnCreate==1){
            ImportContactsActivity.activity.finish();
        }
        if (PublishNoticeActivityOnCreate==1){
            PublishNoticeActivity.activity.finish();
        }
        if (NoticeActivityOnCreate==1){
            NoticeActivity.activity.finish();
        }
        if (MainFaceActivityOnCreate==1){
            MainFaceActivity.activity.finish();
        }
        if(DocumentListActivityOnCreate == 1){
            DocumentListActivity.activity.finish();
        }
        if (NoticeListActivityOnCreate==1){
            NoticeListActivity.activity.finish();
        }
        if (DocumentActivityOnCreate==1){
            DocumentActivity.activity.finish();
        }
        if (MagazineListActivityOnCreate==1){
            MagazineListActivity.activity.finish();
        }
        if (MagazineActivityOnCreate==1){
            MagazineActivity.activity.finish();
        }
        if (PublicInformationListActivityOnCreate==1){
            PublicInformationListActivity.activity.finish();
        }
        if (PublicInformationActivityOnCreate==1){
            PublicInformationActivity.activity.finish();
        }
        if (PublishNoticeChoiceDepartmentActivityOnCreate==1){
            PublishNoticeChoiceDepartmentActivity.activity.finish();
        }
        setOnCreate("PublishNoticeActivityOnCreate");
        setOnCreate("NoticeActivityOnCreate");
        setOnCreate("MainFaceActivityOnCreate");
        setOnCreate("DocumentListActivityOnCreate");
        setOnCreate("NoticeListActivityOnCreate");
        setOnCreate("DocumentActivityOnCreate");
        setOnCreate("MagazineListActivityOnCreate");
        setOnCreate("MagazineActivityOnCreate");
        setOnCreate("PublicInformationListActivityOnCreate");
        setOnCreate("PublicInformationActivityOnCreate");
        setOnCreate("PublishNoticeChoiceDepartmentActivityOnCreate");
        setOnCreate("ImportContactsActivityOnCreate");
        setOnCreate("PublishNoticeChoiceReceiverActivityOnCreate");
        setOnCreate("FeedbackActivityOnCreate");
        setOnCreate("ContactsInformationActivityOnCreate");
        setOnCreate("PublishedNoticeListActivityOnCreate");
        setOnCreate("AboutActivityOnCreate");
    }

    //获取onCreate
    int getOnCreate(String s){
        SharedPreferences sharedPreferences = getSharedPreferences(s,0);
        return sharedPreferences.getInt(s,0);
    }

    //    改回onCreate
    void setOnCreate(String s){
        SharedPreferences sharedPreferences = getSharedPreferences(s, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putInt(s,0);
        editor.apply();
    }
    private void deleteAllFiles(File root) {
        File files[] = root.listFiles();
        if (files != null)
            for (File f : files) {
                if (f.isDirectory()) { // 判断是否为文件夹
                    deleteAllFiles(f);
                    try {
                        f.delete();
                    } catch (Exception e) {
                    }
                } else {
                    if (f.exists()) { // 判断是否存在
                        deleteAllFiles(f);
                        try {
                            f.delete();
                        } catch (Exception e) {
                        }
                    }
                }
            }
    }
}
