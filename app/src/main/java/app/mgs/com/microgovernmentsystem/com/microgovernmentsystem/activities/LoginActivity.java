package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import app.mgs.com.microgovernmentsystem.R;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.Dao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.operations.LoginOperation;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.CheckVersionUpdate;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Datahelper;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Passwordcheck;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Values;
import cn.jpush.android.api.JPushInterface;

public class LoginActivity extends BaseActivity{

    public static LoginActivity activity;
    public EditText medtAccount;
    public EditText medtPassword;
    public Button mbtLogin;
    public LinearLayout mllBottomPanel;

    Context context = this;


    public Dao dao = new Dao(context,null, Values.database_version);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        CheckVersionUpdate a=new CheckVersionUpdate();
        a.checkVersion(new CheckVersionUpdate.callback() {
            @Override
            public void callback() {

            }
        });
        initView();
        activity = this;

        getLastLog();//如果记住密码，则直接进到主页面

        initActivitys();//在启动app时将所有activity的创建代码都重置为0

        setOnClickListener();
    }

    void initView(){
        medtAccount = (EditText) findViewById(R.id.act_login_edt_account);
        medtPassword = (EditText) findViewById(R.id.act_login_edt_password);
        mbtLogin = (Button) findViewById(R.id.act_login_bt_log);
        mllBottomPanel = (LinearLayout) findViewById(R.id.act_login_ll_underside);
    }

    void setOnClickListener(){
        mbtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String account = medtAccount.getText().toString();
                final String password = medtPassword.getText().toString();
               if(Passwordcheck.check(password))
//                if(true)
                {
                    LoginOperation loginOperation = new LoginOperation(account, password, context);
                    loginOperation.Login(mllBottomPanel);
                    if (!(account.isEmpty() || password.isEmpty()))
                        loginemc();
                }
                else
                {
                    Datahelper.showToast("您的密码不符合安全强度要求，不允许手机端登录。");
                }
            }
        });
    }

    private void getLastLog(){
        SQLiteDatabase readableDatabase = dao.getReadableDatabase();
        String[] arg = {String.valueOf("1")};
        Cursor cursor = readableDatabase.query("LOGINTABLE", new String[]{"lastlogin"}, "lastlogin=?", arg, null, null, null);
        if (cursor.moveToNext()){
            if (EMClient.getInstance().isLoggedInBefore()) {
                // ** 免登陆情况 加载所有本地群和会话
                // 不是必须的，不加sdk也会自动异步去加载(不会重复加载)；
                // 加上的话保证进了主页面会话和群组都已经load完毕
                long start = System.currentTimeMillis();
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
            }
            Intent intent = new Intent();
            intent.setClass(LoginActivity.this, MainFaceActivity.class);
            startActivity(intent);
            LoginActivity.this.finish();
        }
        cursor.close();
    }

    public void initActivitys(){
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

    //    改回onCreate
    public void setOnCreate(String s){
        SharedPreferences sharedPreferences = getSharedPreferences(s, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putInt(s,0);
        editor.apply();
    }

    private void loginemc() {

            EMClient.getInstance().login(medtAccount.getText().toString(), medtAccount.getText().toString(), new EMCallBack() {//回调
                @Override
                public void onSuccess() {
                    EMClient.getInstance().groupManager().loadAllGroups();
                    EMClient.getInstance().chatManager().loadAllConversations();
                    Log.e("main", "登录聊天服务器成功！");
                }

                @Override
                public void onProgress(int progress, String status) {

                }

                @Override
                public void onError(int code, String message) {

                    Log.e("main", "登录聊天服务器失败！");
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                // 调用sdk注册方法
                                EMClient.getInstance().createAccount(medtAccount.getText().toString(),medtAccount.getText().toString());
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        // 保存用户名
                                        loginemc();
                                    }

                                });
                            } catch (final HyphenateException e) {
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {// 注册失败
                                        int errorCode = e.getErrorCode();
                                        if (errorCode == EMError.NETWORK_ERROR) {
                                        //    showToast("网络异常，请检查网络！");
                                        } else if (errorCode == EMError.USER_ALREADY_EXIST) {
                                            loginemc();
                                        } else if (errorCode == EMError.USER_REG_FAILED) {
                                        //    showToast("注册失败，无权限！");
                                        } else {
                                          //  showToast("注册失败: " + e.getMessage());
                                        }
                                    }
                                });
                            }
                        }

                    }).start();
                }
            });
        }
    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }
    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

}
