package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.fragments;


import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import app.mgs.com.microgovernmentsystem.R;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.DepartmentInformationListActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.DocumentListActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.MagazineListActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.MemorandumActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.NoticeListActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.PublicInformationListActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.SettingActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.adapters.GovernmentAffairsListViewAdapter;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.GetUserInformationDao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.ListBeanMenu;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.operations.ExitLogOperation;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.CalculateListViewTool;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.UnreadMessageNum;

public class GovernmentAffairsFragment extends Fragment {

    public TextView mtvName;
    public TextView mtvPosition;
    public TextView mtvWelcome;
    public RelativeLayout mrlNotice;
    public RelativeLayout mrlDocument;
    public RelativeLayout mrlMagazine;
    public RelativeLayout mrlPublicInformation;
    public RelativeLayout mrlDepartmentInformation;
    public ImageButton mibtMenu;
    public ListView listView;
    private View contentView;
    public Button noticeButton;
    public TextView noticenum;
    public TextView documentnum;
    public TextView magazinenum;
    public TextView publicinformationnum;
    public TextView departmentinformationnum;
    public TextView [] tvs=new TextView[5];
    public Button add;
    public PopupWindow popupWindow = new PopupWindow();
    private boolean isOuter = false;//监听弹窗是否处于弹出状态

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg)
        {
            if(msg.what==0)
            {
                int[] nums = new int[5];
                nums[0]= UnreadMessageNum.getNoticenum();
                nums[1]= UnreadMessageNum.getDocumentnum();
                nums[2]= UnreadMessageNum.getMagazinenum();
                nums[3]= UnreadMessageNum.getPublicinformationnum();
                nums[4]= UnreadMessageNum.getDepartmentinformationnum();
                for(int i=0;i<5;i++)
                {
                    if(nums[i]>0)
                    {
                        String s=String.valueOf(nums[i]);
                        if(nums[i]<10)
                            s=" "+s+" ";
                        if(nums[i]>99)
                            s="99+";
                        tvs[i].setText(s);
                        tvs[i].setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        tvs[i].setVisibility(View.INVISIBLE);
                    }
                }


            }
        }
    };

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_governmentaffairs,container,false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();//初始化控件

        setOnClickListener();

        String date = getDate();//设置日期

        String weekday = getWeekday();//;设置周几

        setWelcomeText(date,weekday);//将得到的日期给页面上的tv赋值

        setUserInformationText();//得到用户信息并给tv赋值

        setOnClickListener();//设置点击监听事件
        UnreadMessageNum.setHandler(mHandler);
        mHandler.sendEmptyMessageDelayed(0,0);
    }

    private void initView(){
        mtvName = (TextView) getActivity().findViewById(R.id.frg_ga_ll_welcome_tv_name);
        mtvPosition = (TextView) getActivity().findViewById(R.id.frg_ga_ll_welcome_tv_position);
        mtvWelcome = (TextView) getActivity().findViewById(R.id.frg_ga_ll_welcome_tv_welcome);
        mrlNotice = (RelativeLayout) getActivity().findViewById(R.id.frg_ga_rl_notice);
        mrlDocument = (RelativeLayout) getActivity().findViewById(R.id.frg_ga_rl_document);
        mrlMagazine = (RelativeLayout) getActivity().findViewById(R.id.frg_ga_rl_magazine);
        mrlDepartmentInformation = (RelativeLayout) getActivity().findViewById(R.id.frg_ga_rl_department_information);
        mrlPublicInformation = (RelativeLayout) getActivity().findViewById(R.id.frg_ga_rl_public_information);
        mibtMenu = (ImageButton) getActivity().findViewById(R.id.frg_ga_ibt_menu);

        noticenum=(TextView) getActivity().findViewById(R.id.noticenum);
        documentnum=(TextView) getActivity().findViewById(R.id.docunmentnum);
        magazinenum=(TextView) getActivity().findViewById(R.id.magazinenum);
        publicinformationnum=(TextView) getActivity().findViewById(R.id.publicinformationnum);
        departmentinformationnum=(TextView) getActivity().findViewById(R.id.departmentinformationnum);
        tvs[0]=noticenum;
        tvs[1]=documentnum;
        tvs[2]=magazinenum;
        tvs[3]=publicinformationnum;
        tvs[4]=departmentinformationnum;

      //  noticeButton=(Button)getActivity().findViewById(R.id.noticeButton);
    }

    @SuppressLint("SetTextI18n")
    void setWelcomeText(String date, String weekday){
        mtvWelcome.setText("欢迎使用高新微政务  "+date+weekday);
    }

    void setUserInformationText(){
        GetUserInformationDao guid = new GetUserInformationDao();
        mtvName.setText(guid.getUserName());
        mtvPosition.setText(guid.getPosition());
    }

    private void setOnClickListener(){
        final Intent intent = new Intent();
        mibtMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isOuter){
                    isOuter = false;
                    popupWindow.dismiss();
                }else {
                    showPopupWindow();//显示自定义弹窗
                    isOuter = true;
                }
            }
        });

        mrlNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.setClass(getActivity(),NoticeListActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        mrlDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.setClass(getActivity(),DocumentListActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        mrlMagazine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.setClass(getActivity(),MagazineListActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        mrlPublicInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.setClass(getActivity(),PublicInformationListActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        mrlDepartmentInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.setClass(getActivity(),DepartmentInformationListActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }

    /**
     * 自定义弹窗
     */
    private void showPopupWindow(){

        initListView();

        GovernmentAffairsListViewAdapter listViewAdapter = new GovernmentAffairsListViewAdapter(getActivity());
        listViewAdapter.setListBeanMenus(arrayListBeans());
        listView.setAdapter(listViewAdapter);
        listViewAdapter.notifyDataSetChanged();
        int H = getTotalHeightofListView(listView);

        popupWindow.setContentView(contentView);
        popupWindow.setHeight(H);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                isOuter = false;
            }
        });
        popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_ga_menu_24dp));
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        //显示popwindow
        popupWindow.showAsDropDown(getActivity().findViewById(R.id.frg_ga_ibt_menu),0,0);
    }

    private void initListView(){
        final RelativeLayout rlAdpForLvGa;
        rlAdpForLvGa = (RelativeLayout) getActivity().findViewById(R.id.adp_for_lv_ga_rl);

        contentView = LayoutInflater.from(getActivity()).inflate(R.layout.listview_governmentaffairs,rlAdpForLvGa,false);
        listView = (ListView) contentView.findViewById(R.id.lv_ga_listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                if (i == 0){
                    //启动备忘录
                    intent.setClass(getActivity(),MemorandumActivity.class);
                    intent.putExtra("import",0);
                    startActivity(intent);
                    popupWindow.dismiss();
                    finish();
                }else if (i == 1){
                    intent.setClass(getActivity(),SettingActivity.class);
                    popupWindow.dismiss();
                    startActivity(intent);
                    finish();
                }else if (i == 2){
                    popupWindow.dismiss();
                    ExitLogOperation operation = new ExitLogOperation();
                    operation.exitLog(getActivity());
                }else{
                    popupWindow.dismiss();
                    finish();
                }
            }
        });

    }

    private void finish(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MainFaceActivityOnCreate",0);
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putInt("MainFaceActivityOnCreate",0);
        editor.apply();
        getActivity().finish();
    }

    /**
     * 往adapter中添加数据
     * 返回数组
     */
    public ArrayList<ListBeanMenu> arrayListBeans(){
        ArrayList<ListBeanMenu> listBeanMenus = new ArrayList<>();
        ListBeanMenu listBeanMenu = new ListBeanMenu();
        listBeanMenu.setListViewItem("备忘录");
        listBeanMenus.add(listBeanMenu);
        ListBeanMenu listBeanMenu1 = new ListBeanMenu();
        listBeanMenu1.setListViewItem("设置");
        listBeanMenus.add(listBeanMenu1);
        ListBeanMenu listBeanMenu2 = new ListBeanMenu();
        listBeanMenu2.setListViewItem("注销");
        listBeanMenus.add(listBeanMenu2);
        ListBeanMenu listBeanMenu3 = new ListBeanMenu();
        listBeanMenu3.setListViewItem("退出");
        listBeanMenus.add(listBeanMenu3);
        return listBeanMenus;
    }

    /**
     * 取listview的高度
     */
    public static int getTotalHeightofListView(ListView listView){
        CalculateListViewTool calculateListViewTool = new CalculateListViewTool(listView);
        return calculateListViewTool.CalculateHeight()+45;
    }

    /**
     * 获取日期
     */
    private String getDate(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINESE);
        Date curDate = new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }

    /**
     * 获取周几
     */
    private String getWeekday(){
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String mWdy = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));
        if("1".equals(mWdy)){
            mWdy ="星期日";
        }else if("2".equals(mWdy)){
            mWdy ="星期一";
        }else if("3".equals(mWdy)){
            mWdy ="星期二";
        }else if("4".equals(mWdy)){
            mWdy ="星期三";
        }else if("5".equals(mWdy)){
            mWdy ="星期四";
        }else if("6".equals(mWdy)){
            mWdy ="星期五";
        }else if("7".equals(mWdy)){
            mWdy ="星期六";
        }
        return mWdy;
    }
    @Override
    public void onResume() {
        super.onResume();
        //更新未读消息数
        UnreadMessageNum.updateunreadnum();

    }

}
