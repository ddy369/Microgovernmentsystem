package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import app.mgs.com.microgovernmentsystem.R;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.MemorandumTagActivity;


public class CalendarFragment extends Fragment {

    private MaterialCalendarView calendarView;
    private FloatingActionButton mFloatingActionButton;
    public TextView tvToday;

    //一些全局变量
    public int mYear;
    public int mMonth;
    public int mDay;

    private int f;
    String mtitle = null;//用来暂时存放标题
    String mcontent = null;//用来暂时存放正文

    Intent intent = new Intent();
    java.util.Calendar c = java.util.Calendar.getInstance();//获取当前日期

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar,container,false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getIntentValues();

        initViews();

        setOnClickListener();
    }

    private void initViews(){
        mFloatingActionButton = (FloatingActionButton) getActivity().findViewById(R.id.frg_calendar_fab);
        calendarView = (MaterialCalendarView) getActivity().findViewById(R.id.frg_calendar_cv);
        tvToday = (TextView) getActivity().findViewById(R.id.act_memorandum_tv_today);

        mYear = c.get(java.util.Calendar.YEAR);
        mMonth = c.get(java.util.Calendar.MONTH)+1;
        mDay = c.get(java.util.Calendar.DAY_OF_MONTH);

        calendarView.setSelectedDate(c);//让当天被选中
    }

    private void setOnClickListener(){
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int day = calendarView.getSelectedDate().getDay();
                int month = calendarView.getSelectedDate().getMonth();
                int year = calendarView.getSelectedDate().getYear();
                intent.setClass(getActivity(),MemorandumTagActivity.class);
                intent.putExtra("day",day);
                intent.putExtra("month",month+1);
                intent.putExtra("year",year);
                if (f!=0){
                    intent.putExtra("id",-1);
                    intent.putExtra("title",mtitle);
                    intent.putExtra("content",mcontent);
                }
                startActivity(intent);
//                getActivity().finish();
            }
        });

        tvToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendarView.setCurrentDate(c);
                calendarView.setSelectedDate(c);//让当天被选中
            }
        });
    }

    //获取跳转时传过来的值
    private void getIntentValues(){
        Intent intent = getActivity().getIntent();
        f = intent.getIntExtra("import",0);
        if (f!=0){
            mtitle = intent.getStringExtra("mtvTitle");
            mcontent = intent.getStringExtra("mtvContent");
        }
    }
}
