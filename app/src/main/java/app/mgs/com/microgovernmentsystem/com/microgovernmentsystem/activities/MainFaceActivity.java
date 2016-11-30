package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import app.mgs.com.microgovernmentsystem.R;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.GetUserInformationDao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.fragments.FindFragment;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.fragments.GovernmentAffairsFragment;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.fragments.MailListFragment;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.fragments.MeFragment;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.MyReceiver;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.UnreadMessageNum;
import cn.jpush.android.api.JPushInterface;

public class MainFaceActivity extends BaseActivity implements View.OnClickListener{

    public static MainFaceActivity activity = null;

    private long mExitTime;

    Toast toast = null;

    private ImageButton mibtGovernmentAffairs;
    private ImageButton mibtMailList;
    private  ImageButton mibtFind;
    private ImageButton mibtMe;

    private GovernmentAffairsFragment governmentAffairsFragment;
    private MailListFragment mailListFragment;
    private FindFragment findFragment;
    private MeFragment meFragment;
    //用来记录当前显示的fragment
    private Fragment fg;

    private android.app.FragmentManager fragmentManager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyReceiver.setAct(this);
        setStyle();//先设置主题

        setContentView(R.layout.activity_main_face);

        initViews();

        setOnCreate();

        activity = this;

        fragmentManager = getFragmentManager();

        int i = getIntentValues();

        setTabSelection(i);
        GetUserInformationDao getUserInformationDao = new GetUserInformationDao();
        Log.d("JPush",getUserInformationDao.getUid());
     //   JPushInterface.setAlias(this,"",null);
        JPushInterface.setAlias(this,getUserInformationDao.getUid(),null);

    }

    private int getIntentValues(){
        Intent intent = getIntent();
        return intent.getIntExtra("select",0);
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

    private void initViews(){
        mibtGovernmentAffairs = (ImageButton) findViewById(R.id.act_main_face_government_affairs);
        mibtMailList = (ImageButton) findViewById(R.id.act_main_face_mail_list);
        mibtFind = (ImageButton) findViewById(R.id.act_main_face_find);
        mibtMe = (ImageButton) findViewById(R.id.act_main_face_me);

        mibtGovernmentAffairs.setOnClickListener(this);
        mibtMailList.setOnClickListener(this);
        mibtFind.setOnClickListener(this);
        mibtMe.setOnClickListener(this);
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.act_main_face_government_affairs:
                setTabSelection(0);
                break;
            case R.id.act_main_face_mail_list:
                setTabSelection(1);
                break;
            case R.id.act_main_face_find:
                setTabSelection(2);
                break;
            case R.id.act_main_face_me:
                setTabSelection(3);
                break;
            default:
                break;
        }
    }

    public void setOnCreate(){
        SharedPreferences sharedPreferences = getSharedPreferences("MainFaceActivityOnCreate", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putInt("MainFaceActivityOnCreate",1);
        editor.apply();
    }

    /**
     * 根据传入的index参数来设置选中的tab页。
     *
     */
    private void setTabSelection(int index)
    {
        // 开启一个Fragment事务
        android.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);
        switch (index)
        {
            case 0:
                // 当点击了消息tab时，改变控件的图片和文字颜色
                mibtGovernmentAffairs.setImageResource(R.drawable.ic_ga_affairs_24dp);
                mibtMe.setImageResource(R.drawable.ic_ga_me_24dp);
                mibtFind.setImageResource(R.drawable.ic_ga_find_24dp);
                mibtMailList.setImageResource(R.drawable.ic_ga_mail_list_24dp);
                fg=governmentAffairsFragment;
                if (governmentAffairsFragment == null)
                {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
                    governmentAffairsFragment = new GovernmentAffairsFragment();
                    transaction.add(R.id.act_main_face_content, governmentAffairsFragment);
                } else
                {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    transaction.show(governmentAffairsFragment);
                }
                break;
            case 1:
                // 当点击了消息tab时，改变控件的图片和文字颜色
                mibtMailList.setImageResource(R.drawable.ic_ga_mail_list1_24dp);
                mibtGovernmentAffairs.setImageResource(R.drawable.ic_ga_affairs1_24dp);
                mibtMe.setImageResource(R.drawable.ic_ga_me_24dp);
                mibtFind.setImageResource(R.drawable.ic_ga_find_24dp);

                if (mailListFragment == null)
                {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
                    mailListFragment = new MailListFragment();
                    transaction.add(R.id.act_main_face_content, mailListFragment);
                } else
                {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    transaction.show(mailListFragment);
                }
                fg=mailListFragment;
                break;
            case 2:
                //当点击了消息tab时，改变控件的图片和文字颜色
                mibtFind.setImageResource(R.drawable.ic_ga_find1_24dp);
                mibtGovernmentAffairs.setImageResource(R.drawable.ic_ga_affairs1_24dp);
                mibtMe.setImageResource(R.drawable.ic_ga_me_24dp);
                mibtMailList.setImageResource(R.drawable.ic_ga_mail_list_24dp);
                fg=findFragment;
                if (findFragment == null)
                {
                    // 如果NewsFragment为空，则创建一个并添加到界面上
                    findFragment = new FindFragment();
                    transaction.add(R.id.act_main_face_content, findFragment);
                } else
                {
                    // 如果NewsFragment不为空，则直接将它显示出来
                    transaction.show(findFragment);
                }
                break;
            case 3:
                // 当点击了消息tab时，改变控件的图片和文字颜色
                mibtMe.setImageResource(R.drawable.ic_ga_me1_24dp);
                mibtGovernmentAffairs.setImageResource(R.drawable.ic_ga_affairs1_24dp);
                mibtFind.setImageResource(R.drawable.ic_ga_find_24dp);
                mibtMailList.setImageResource(R.drawable.ic_ga_mail_list_24dp);
                fg=meFragment;
                if (meFragment == null)
                {
                    // 如果SettingFragment为空，则创建一个并添加到界面上
                    meFragment = new MeFragment();
                    transaction.add(R.id.act_main_face_content, meFragment);
                } else
                {
                    // 如果SettingFragment不为空，则直接将它显示出来
                    transaction.show(meFragment);
                }
                break;
        }
        transaction.commit();
    }

    /**
     * 将所有的Fragment都置为隐藏状态。
     *
     * @param transaction
     *            用于对Fragment执行操作的事务
     */
    private void hideFragments(android.app.FragmentTransaction transaction)
    {
        if (governmentAffairsFragment != null)
        {
            transaction.hide(governmentAffairsFragment);
        }
        if (mailListFragment != null)
        {
            transaction.hide(mailListFragment);
        }
        if (findFragment != null)
        {
            transaction.hide(findFragment);
        }
        if (meFragment != null)
        {
            transaction.hide(meFragment);
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (fg instanceof MailListFragment) {
            ((MailListFragment) fg).onKeyDown(keyCode, event);
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.i("onKeyDown", "sure to exit");
            if ((System.currentTimeMillis() - mExitTime) > 2000) {// 如果两次按键时间间隔大于2000毫秒，则不退出
                toast = Toast.makeText(this, "再次点击退出高新微政务", Toast.LENGTH_SHORT);
                toast.show();
                mExitTime = System.currentTimeMillis();// 更新mExitTime
            } else {
                toast.cancel();
                this.finish();
//                System.exit(0);// 否则退出程序
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onResume() {
        super.onResume();
        UnreadMessageNum.cancelNotification();
    }

}
