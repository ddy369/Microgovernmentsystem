package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import app.mgs.com.microgovernmentsystem.R;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.fragments.CalendarFragment;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.fragments.MemorandumListFragment;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.MemorandumListDatahelper;


public class MemorandumActivity extends BaseActivity {

    public static MemorandumActivity activity = null;

    Context context = this;

    private TextView mtvToday;
    private TextView mtvDelete;
    private ImageButton mibtReturn;
    private TabLayout tabLayout;
    private ListView mlvMemorandum;

    private CalendarFragment calendarFragment;
    private MemorandumListFragment memorandumListFragment;
    //用来记录当前显示的fragment
    private Fragment fg;

    private android.app.FragmentManager fragmentManager;

    //全局变量
    public int mDay;
    int f = 0;//f用来判断是否为导入日程；若为0则不做如何改动，否则跳转至导入日程

    Intent intent = new Intent();
    java.util.Calendar c = java.util.Calendar.getInstance();//获取当前日期

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memorandum);

        initViews();

        setOnClickListener();

        fragmentManager = getFragmentManager();

        activity = this;
    }

    private void initViews(){
        mtvToday = (TextView) findViewById(R.id.act_memorandum_tv_today);
        mibtReturn = (ImageButton) findViewById(R.id.act_memorandum_ibt_return);
        mtvDelete = (TextView) findViewById(R.id.act_memorandum_tv_delete);
        mlvMemorandum = (ListView)  findViewById(R.id.frg_memorandum_list_lv);

        //初始化定位到当天的控件
        mDay = c.get(java.util.Calendar.DAY_OF_MONTH);
        mtvToday.setText(String.valueOf(mDay));

        //初始化tab控件
        tabLayout = (TabLayout) findViewById(R.id.act_memorandum_tab);
        tabLayout.setTabTextColors(0xFFFFFFFF,0xFF000000);//前者为选中颜色，后者为非选中颜色
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);//让tab占满整行
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.addTab(tabLayout.newTab().setText("事件列表"));
        tabLayout.addTab(tabLayout.newTab().setText("创建事件"));
    }

    @Override
    protected void onStart() {
        super.onStart();
        //TODO:判断备忘录列表是否为空
        /**
         * 优先判断是否是导入日程的跳转，若是则直接开始选择日期并导入日程
         */
        f = getIntentValues();
        if (f==0){
            /**
             * 在数据库中寻找一下备忘录是否为空
             */
            MemorandumListDatahelper datahelper = new MemorandumListDatahelper();
            int i = datahelper.selectNum();
            if (i==0){
                setTabSelection(1);
                tabLayout.getTabAt(1).select();
            }
            else{
                setTabSelection(0);
                tabLayout.getTabAt(0).select();
            }
        }else {
            /**
             * 直接开始编辑日期
             */
            setTabSelection(1);
            tabLayout.getTabAt(1).select();
            tabLayout.setVisibility(View.GONE);
        }

    }

    private void setOnClickListener(){

        mibtReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (f==0)
                    Return();
                else
                    finish();
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setTabSelection(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    /**
     * 根据传入的index参数来设置选中的tab页。
     *
     */
    private void setTabSelection(int index)
    {
        if (index == 0){
            //如果为事件列表页，则不显示定位当天按钮，显示删除按钮
            mtvToday.setVisibility(View.GONE);
            mtvDelete.setVisibility(View.VISIBLE);
        }else {
            mtvToday.setVisibility(View.VISIBLE);
            mtvDelete.setVisibility(View.GONE);
        }
        // 开启一个Fragment事务
        android.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);
        switch (index)
        {
            case 0:
                // 改变tab的文字颜色
                if (memorandumListFragment == null)
                {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
                    memorandumListFragment = new MemorandumListFragment();
                    transaction.add(R.id.act_memorandum_ll, memorandumListFragment);
                } else {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    transaction.show(memorandumListFragment);
                }
                fg=memorandumListFragment;
                break;

            case 1:
                //改变tab的文字颜色
                fg=calendarFragment;
                if (calendarFragment == null)
                {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
                    calendarFragment = new CalendarFragment();
                    transaction.add(R.id.act_memorandum_ll, calendarFragment);
                } else {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    transaction.show(calendarFragment);
                }
                break;
        }
        transaction.commit();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            if (f==0)
                Return();
            else
                this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void Return(){
        /**
         * 判断主页面是否被创建，如果已经被创建则直接结束本页面
         */
        if (getOnCreate("MainFaceActivityOnCreate")==0){
            Intent intent = new Intent();
            intent.setClass(MemorandumActivity.this,MainFaceActivity.class);
            startActivity(intent);
            this.finish();
        }else {
            this.finish();
        }
    }

    //获取onCreate
    int getOnCreate(String s){
        SharedPreferences sharedPreferences = getSharedPreferences(s,0);
        return sharedPreferences.getInt(s,0);
    }

    /**
     * 将所有的Fragment都置为隐藏状态。
     *
     * @param transaction
     *            用于对Fragment执行操作的事务
     */
    private void hideFragments(android.app.FragmentTransaction transaction)
    {
        if (calendarFragment != null)
        {
            transaction.hide(calendarFragment);
        }
        if (memorandumListFragment != null)
        {
            transaction.hide(memorandumListFragment);
        }
    }

    //获取跳转时传过来的值
    private int getIntentValues(){
        Intent intent = getIntent();
        return intent.getIntExtra("import",0);
    }
}
