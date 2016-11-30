package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.HashMap;

import app.mgs.com.microgovernmentsystem.R;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.SetMemorandumDao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.AlarmReceiver;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.CompareTime;


public class MemorandumTagActivity extends BaseActivity {

    public static MemorandumTagActivity activity = null;

    private ImageButton mibtReturn;
    private ImageButton mibtSwitch;
    private TextView mtvSave;
    private RelativeLayout mrlSwitch;
    private RelativeLayout mrlSelectTime;
    private EditText medtText;
    private EditText medtTitle;
    private TextView mtvTime;

    //全局变量
    private boolean isOpen;//提醒开关是否开启的全局变量
    private int year = 0;
    private int month = 0;
    private int day = 0;
    private int hour = 8;
    private int minute = 0;

    private int id = 0;//获取id
    private String mtitle = null;
    private String mcontent = null;

    Context mContext = this;
    Intent intent = new Intent();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);

        activity = this;

        initViews();

        setOnClickListener();

        setContent();//如果由页面点击进来，则设置好内容
    }

    private void initViews(){
        mibtReturn = (ImageButton) findViewById(R.id.act_tag_ibt_return);
        mtvSave = (TextView) findViewById(R.id.act_tag_tv_save);
        mrlSwitch = (RelativeLayout) findViewById(R.id.act_tag_rl_switch);
        mibtSwitch = (ImageButton) findViewById(R.id.act_tag_ibt_switch);
        mrlSelectTime = (RelativeLayout) findViewById(R.id.act_tag_rl_select_time);
        medtText = (EditText) findViewById(R.id.act_tag_edt_text);
        mtvTime = (TextView) findViewById(R.id.act_tag_tv_time_show);
        medtTitle = (EditText) findViewById(R.id.act_tag_edt_title);
    }

    private void setOnClickListener(){
        mibtReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Return();
            }
        });

        mrlSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (id <= 0) {
                    intent = getIntent();
                    year = intent.getIntExtra("year",0);
                    month = intent.getIntExtra("month",0);
                    day = intent.getIntExtra("day",0);
                }
                Log.i("年月日"," "+year+" "+month+" "+day);
                //在点击开关之前先判定时间是否符合要求，否则让开关无法点击
                CompareTime compareTime = new CompareTime();
                int i = compareTime.compareTime(year,month,day,hour,minute);
                if (!isOpen && i==1){
                    mibtSwitch.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.ic_open_24dp));
                    isOpen = true;
                }else {
                    mibtSwitch.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.ic_close_24dp));
                    isOpen = false;
                }
            }
        });

        mrlSelectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        hour = i;
                        minute = i1;
                        mtvTime.setText(i+"时 "+i1+"分");
                        Log.i("hour and minute1"," "+i+i1);
                    }
                },8,0,true).show();
            }
        });

        mtvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = medtTitle.getText().toString();
                String content = medtText.getText().toString();

                //以下代码用来设置提醒并判断是否设置提醒是否有效
                int notice;
                CompareTime compareTime = new CompareTime();
                int i = compareTime.compareTime(year,month,day,hour,minute);
                if (isOpen && i==1){
                    notice = 1;
                }else {
                    notice = 0;
                }
                if (!title.isEmpty()){
                    SetMemorandumDao dao = new SetMemorandumDao();
                    if (id <= 0) {
                        /**
                         * 如果id为0则表示该条消息属于最新创建的，应当进行插入数据库操作
                         */
                        intent = getIntent();
                        year = intent.getIntExtra("year",0);
                        month = intent.getIntExtra("month",0);
                        day = intent.getIntExtra("day",0);
                        Log.i("chaloubuque",title+" "+content+" "+year+" "+month+" "+day+" "+hour+" "+minute+" "+notice);
                        dao.insertMemorandum(title,content,year,month,day,hour,minute,notice);
                        //获取最新一条的id来避免闹钟的覆盖问题
                        int id1 = dao.selectMemorandum();
                        if (notice == 1){
                            //设置间隔一段时间来启动闹铃提示
                            setAlarmClock(year,month,day,hour,minute,id1);
                        }
//                        intent.setClass(MemorandumTagActivity.this,MemorandumActivity.class);
//                        startActivity(intent);
                        if (id < 0){
                            MemorandumActivity.activity.finish();
                        }
                        finish();
                    }else {
                        /**
                         * 如果id变为其他值，则表示此条id不为新建，需要进行更新数据库操作
                         */
                        SetMemorandumDao setMemorandumDao = new SetMemorandumDao();
                        HashMap<String , Integer> map;
                        map = setMemorandumDao.getTime(id);
                        year = map.get("year");
                        month = map.get("month");
                        day = map.get("day");
                        dao.updateMemorandum(title,content,year,month,day,hour,minute,notice,id);
                        //获取最新一条的id来避免闹钟的覆盖问题
                        if (notice == 1){
                            //设置间隔一段时间来启动闹铃提示
                            setAlarmClock(year,month,day,hour,minute,id);
                        }
//                        intent.setClass(MemorandumTagActivity.this,MemorandumActivity.class);
//                        intent.putExtra("selectTab",1);
//                        startActivity(intent);
                        finish();
                    }
                }else {
                    Toast.makeText(mContext,"请输入标题",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 以下代码用来设置闹钟提醒
     */
    private void setAlarmClock(int year,int month,int day,int hour,int minute,int id){
        Intent intent1 = new Intent(MemorandumTagActivity.this, AlarmReceiver.class);
        intent1.putExtra("title",getTitle());
        PendingIntent sender = PendingIntent.getBroadcast(MemorandumTagActivity.this,
               id ,intent1,0);
        //获取当前时间与要提示的时间进行运算
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE,minute);
        calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.DAY_OF_MONTH,day);
        calendar.set(Calendar.MONTH,month-1);
        calendar.set(Calendar.YEAR,year);
        calendar.set(Calendar.SECOND,0);
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        manager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),sender);
    }


    /**
     * 如果由列表页面点击进来，则改为更新对应id的内容
     */
    private void setContent(){
        getIntentValues();//获取传过来的id
        if (id>0){
            //如果id大于0，则表示用户正在更改已经存在的备忘录条目
            //对界面的各个控件进行赋值
            SetMemorandumDao setMemorandumDao = new SetMemorandumDao();
            HashMap<String , String> map;
            map = setMemorandumDao.getContent(id);
            String title = map.get("title");
            String content = map.get("content");
            String time = map.get("time");
            String notice = map.get("notice");
            medtTitle.setText(title);
            medtText.setText(content);
            mtvTime.setText(time);
            if (notice.equals("t")){
                mibtSwitch.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.ic_open_24dp));
                isOpen = true;
            }else {
                mibtSwitch.setImageDrawable(ContextCompat.getDrawable(mContext,R.drawable.ic_close_24dp));
                isOpen = false;
            }

            //获取查询数据库得到的时间
            HashMap<String , Integer> map1;
            map1 = setMemorandumDao.getTime(id);
            year = map1.get("year");
            month = map1.get("month");
            day = map1.get("day");
            hour = map1.get("hour");
            minute = map1.get("minute");
            Log.i("hour and minute"," "+hour+minute);
        }else if (id < 0){
            //id=-1表示正在导入日程
            medtTitle.setText(mtitle);
            medtText.setText(mcontent);
        }
    }


    //获取跳转时传过来的值
    private void getIntentValues(){
        Intent intent = getIntent();
        id = intent.getIntExtra("id",0);
        if (id < 0){
            mtitle = intent.getStringExtra("title");
            mcontent = intent.getStringExtra("content");
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            Return();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void Return(){
//        Intent intent = new Intent();
//        intent.setClass(MemorandumTagActivity.this,MemorandumActivity.class);
//        startActivity(intent);
        this.finish();
    }
}
