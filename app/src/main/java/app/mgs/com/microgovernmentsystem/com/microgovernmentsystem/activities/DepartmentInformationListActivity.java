package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;


import java.util.ArrayList;

import app.mgs.com.microgovernmentsystem.R;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.adapters.DepartmentInformationListAdapter;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.adapters.DepartmentInformationListMenuAdapter;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.adapters.DepartmentInformationListSelectAllAdapter;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.adapters.DepartmentInformationListSelectDepartmentAdapter;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.Dao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.ListBean;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.ListBeanMenu;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.ListBeanSelect;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.operations.DepartmentInformationListOperation;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.CalculateListViewTool;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.DepartmentInformationListDataHelper;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.NestedListView;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.UnreadMessageNum;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Values;

public class DepartmentInformationListActivity extends BaseActivity{

    Context mcontext = this;
    public static DepartmentInformationListActivity activity = null;

    //selectAll
    private ArrayList<ListBeanSelect> mListBeanSelectsAll;
    private ArrayList<ListBean> mListBean = new ArrayList<>();
    private ArrayList<ListBean> mListBeans;
    private ArrayList<ListBean> mData= new ArrayList<>();
    private ListView listViewAll;
    private NestedListView nestedListViewAll;
    private NestedListView nestedListViewDepart;
    private View contentViewAll;
    private int prePositionAll;
    private ListView mlvDepartmentInformation;
    private String preSelectAll="全部";
    private String preDepart="全部";
    private String prePosition="111";
    private int titlelength = 0;

    //selectDepartment
    private ArrayList<ListBeanSelect> mListBeanSelectsDepartment;
    private ListView listViewDepartment;
    private ListBeanSelect depart;
    private ListBeanSelect title;
    private View contentViewDepartment;
    private int prePositionDepartment;

    private ImageButton mibtMenu;
    private ImageButton mibtSelectDepartment;
    private ImageButton mibtSelectAll;
    private ImageButton mibtReturn;
    private RelativeLayout mrlRL;
    private EditText medtSearch;
    //一些全局变量
    private int pageNum = 1;
    private int isLoading = 0;
    private int LastItem;//用来记录listview最后一行记录的位置

    private boolean isOuterMenu = false;//监听菜单弹窗是否处于弹出状态
    private boolean isOuterSelectAll = false;
    private boolean isOuterSelectDepartment = false;

    public PopupWindow popupWindowMenu = new PopupWindow();
    public PopupWindow popupWindowSelectAll = new PopupWindow();
    public PopupWindow popupWindowSelectDepartment = new PopupWindow();

    private SwipeRefreshLayout swipeRefreshLayout;
    DepartmentInformationListAdapter departmentInformationListAdapter;
    private View footbar;//listview的底部加载布局与控件


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setStyle();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department_information_list);
        initView();
        activity = this;

        setOnClickListener();
        setOnCreate();
        set_eSearch_TextChanged();
        //先加载本地的数据，然后联网获取到数据后刷新列表
        mListBean = arrayListBeans(0);
        getmData(mData);
        setListView();
        //异步任务接口，把listview传入，联网获取到数据之后刷新listview
        final DepartmentInformationListOperation departmentInformationListOperation1 = new DepartmentInformationListOperation(mcontext,preDepart,preSelectAll);
        departmentInformationListOperation1.Set1();
        departmentInformationListOperation1.setDepartmentInformationList(pageNum, new
                DepartmentInformationListOperation.TaskWork() {
                    @Override
                    public void onPostWork() {
                        mListBean = arrayListBeans(0);
                        getmData(mData);
                        departmentInformationListAdapter.setListBeans(mListBean);
                        departmentInformationListAdapter.notifyDataSetChanged();
                        DepartmentInformationListSelectAllAdapter departmentInformationListSelectAllAdapter = new
                                DepartmentInformationListSelectAllAdapter(mcontext);
                        departmentInformationListSelectAllAdapter.notifyDataSetChanged();
                        mListBeanSelectsDepartment = departmentInformationListOperation1.getListBeenName();
                        mListBeanSelectsAll = departmentInformationListOperation1.getListBeenType();
                    }
                    @Override
                    public void onPostWorkFoot() {
                    }
                });
    }


    @Override
    protected void onStart() {
        super.onStart();
        int length = mListBean.size();
        for(int i = 0;i< length; ++i){
            if(mListBean.get(i).getId().equals(prePosition))
            {
                mListBean.get(i).setRead(1);
            }
        }
        getmData(mData);
        departmentInformationListAdapter.setListBeans(mListBean);
        departmentInformationListAdapter.notifyDataSetChanged();
//        DepartmentInformationListSelectAllAdapter departmentInformationListSelectAllAdapter = new
//                DepartmentInformationListSelectAllAdapter(mcontext);
//        departmentInformationListSelectAllAdapter.notifyDataSetChanged();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent();
            intent.setClass(DepartmentInformationListActivity.this,MainFaceActivity.class);
            startActivity(intent);
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
        medtSearch = (EditText) findViewById(R.id.act_department_information_list_edt_search);
        mibtMenu = (ImageButton) findViewById(R.id.act_department_information_list_ibt_menu);
        mibtSelectDepartment = (ImageButton) findViewById(R.id.act_department_information_list_ibt_select_department);
        mibtSelectAll = (ImageButton) findViewById(R.id.act_department_information_list_ibt_select_all);
        mibtReturn = (ImageButton) findViewById(R.id.act_department_information_list_ibt_return);
        mrlRL = (RelativeLayout) findViewById(R.id.act_department_information_list_rl);
        //mbtTest = (Button) findViewById(R.id.act_department_information_list_bt_test);
        mlvDepartmentInformation = (ListView) findViewById(R.id.act_department_information_list_listview);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.act_department_information_list_swipe_container);
    }

    public void setOnCreate(){
        SharedPreferences sharedPreferences = getSharedPreferences("DepartmentInformationListActivityOnCreate",
                0);
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putInt("DepartmentInformationListActivityOnCreate",1);
        editor.apply();
    }
    private void finishActivity(){
        SharedPreferences sharedPreferences = getSharedPreferences("DepartmentInformationListActivityOnCreate",
                0);
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putInt("DepartmentInformationListActivityOnCreate",0);
        editor.apply();
        finish();
    }
    public ArrayList<ListBean> arrayListBeans(int i){
        //连接数据库取出数据存入listview
        String search =String.valueOf(medtSearch.getText());
        DepartmentInformationListDataHelper datahelper = new DepartmentInformationListDataHelper();
        ArrayList<ListBean> arrayList = null;
        if (i == 0){
            //连接数据库取出全部数据存入listview
            arrayList = datahelper.setListBeans(search);
        }else if (i==2){
            //连接数据库取出已读数据
            arrayList = datahelper.setListBeansRead(search);
        }else if (i==1) {
            //连接数据库取出未读数据
            arrayList = datahelper.setListBeansNoRead(search);
        }
//        }else if (i==3){
//            //连接数据库取出活动资讯
//            arrayList = datahelper.setListBeansDepartment(search);
//        }
        return arrayList;
    }
    /**
     * 联网获取列表信息+下拉刷新控件+底部动态加载
     */
    private void setListView(){
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        footbar = inflater.inflate(R.layout.listview_footbar, null);
        footbar.setVisibility(View.INVISIBLE);
        mlvDepartmentInformation.addFooterView(footbar,null,false);
        departmentInformationListAdapter = new DepartmentInformationListAdapter(mcontext);
        getmData(mData);
        departmentInformationListAdapter.setListBeans(mListBean);
        mlvDepartmentInformation.setAdapter(departmentInformationListAdapter);
        departmentInformationListAdapter.notifyDataSetChanged();

        mlvDepartmentInformation.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                if (LastItem == departmentInformationListAdapter.getCount() && i ==
                        AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
                    footbar.setVisibility(View.VISIBLE);
                    //如果列表滑到最底端，开始请求加载数据
                    if(isLoading == 0) {
                        final DepartmentInformationListOperation operation = new DepartmentInformationListOperation(mcontext,preDepart,preSelectAll);
                        isLoading = 1;
                        DepartmentInformationListDataHelper datahelper = new DepartmentInformationListDataHelper();
                        pageNum = (int) (Math.ceil((double)(mListBean.size()) / 15) + 1);
                        if (pageNum == 0) {
                            pageNum++;
                        }
                        operation.Set0();
                        operation.setDepartmentInformationList(pageNum, new
                                DepartmentInformationListOperation.TaskWork() {
                                    @Override
                                    public void onPostWork() {
                                        String title = mListBeanSelectsAll.get(prePositionAll).getListViewItem();
                                        String depart = mListBeanSelectsDepartment.get(prePositionDepartment).getListViewItem();
                                        if(title.equals("全部")&&depart.equals("全部")){
                                            mListBean = arrayListBeans(0);
                                            departmentInformationListAdapter.setListBeans(mListBean);
                                            departmentInformationListAdapter.notifyDataSetChanged();
                                        }else{
                                            mListBean.addAll(operation.getListBeen());
                                            getReadLocal(mListBean);
                                            departmentInformationListAdapter.setListBeans(mListBean);
                                            departmentInformationListAdapter.notifyDataSetChanged();
                                        }
                                            getmData(mData);
                                    }
                                    public void onPostWorkFoot() {
                                        footbar.setVisibility(View.INVISIBLE);
                                    }
                                });
                        isLoading = 0;
                    } else {
                        Toast.makeText(mcontext, "正在加载，请稍候...", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                LastItem = i + i1 - 1;
            }
        });

        mlvDepartmentInformation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent();
                prePosition = mData.get(position).getId();
                intent.putExtra("id",mData.get(position).getId());//获取当前记录的id，传到department information
                Log.e("id ", "onItemClick: "+mData.get(position).getId());
                intent.setClass(DepartmentInformationListActivity.this,DepartmentInformationActivity.class);
                startActivity(intent);
//                finishActivity();
            }
        });
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light,
                android.R.color.holo_orange_light, android.R.color.holo_green_light);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                int i = mlvDepartmentInformation.getFooterViewsCount();//获取是否存在底部加载控件
                if (i == 0){
                    mlvDepartmentInformation.addFooterView(footbar);
                }
                pageNum = 1;
                final DepartmentInformationListOperation departmentInformationListOperation2 = new DepartmentInformationListOperation(mcontext,preDepart,preSelectAll);
                if(preDepart.equals("全部")) {
                    departmentInformationListOperation2.Set1();
                }
                departmentInformationListOperation2.setDepartmentInformationList(pageNum, new
                        DepartmentInformationListOperation.TaskWork() {
                            @Override
                            public void onPostWork() {
                                //mListBean = arrayListBeans(0);
                                if(preDepart.equals("全部")){
                                    mListBean = arrayListBeans(0);
                                    departmentInformationListAdapter.setListBeans(mListBean);
                                    departmentInformationListAdapter.notifyDataSetChanged();
                                }else {
                                    mListBean = departmentInformationListOperation2.getListBeen();
                                    getReadLocal(mListBean);
                                    departmentInformationListAdapter.setListBeans(mListBean);
                                    departmentInformationListAdapter.notifyDataSetChanged();
                                }
                                getmData(mData);
                                swipeRefreshLayout.setRefreshing(false);
                                //footbar.setVisibility(View.INVISIBLE);
                                //重置筛选框
//                                int position2 = 0;
//                                mListBeanSelectsDepartment.get(position2).setStatus(true);
//                                mListBeanSelectsDepartment.get(prePositionDepartment).setStatus(false);
//                                prePositionDepartment = position2;
//                                //mListBeanSelectsDepartment = arrayListBeansSelectDepartment();
//
//                                mListBeanSelectsAll.get(position2).setStatus(true);
//                                mListBeanSelectsAll.get(prePositionAll).setStatus(false);
//                                prePositionAll = position2;
                                //mListBeanSelectsAll = arrayListBeansSelectAll();
                            }
                            @Override
                            public void onPostWorkFoot() {
                            }
                        });
            }
        });
    }

    private void setOnClickListener(){
        final Intent intent = new Intent();

        mibtMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isOuterMenu){
                    popupWindowMenu.dismiss();
                    isOuterMenu = false;
                }else {
                    showPopupWindowMenu();//显示自定义弹窗
                    isOuterMenu = true;
                }
            }
        });

        mibtSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isOuterSelectAll){
                    popupWindowSelectAll.dismiss();
                    isOuterSelectAll = false;
                }else {
//                    mListBeanSelectsAll = arrayListBeansSelectAll();
                    showPopupWindowSelectAll();//显示自定义弹窗
                    nestedListViewAll.setSelection(prePositionAll);
                    isOuterSelectAll = true;
                }
            }
        });

        mibtSelectDepartment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isOuterSelectDepartment){
                    popupWindowSelectDepartment.dismiss();
                    isOuterSelectDepartment = false;
                }else {
//                    mListBeanSelectsDepartment = arrayListBeansSelectDepartment();
                    showPopupWindowSelectDepartment();//显示自定义弹窗
                    nestedListViewDepart.setSelection(prePositionDepartment);
                    isOuterSelectDepartment = true;
                }
            }
        });
        mibtReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.setClass(DepartmentInformationListActivity.this,MainFaceActivity.class);
                startActivity(intent);
                finishActivity();
            }
        });
    }

    /**
     * menu弹窗的设置
     */
    private void showPopupWindowMenu(){
        final ListView listView;
        final RelativeLayout rlAdpForLvDepartmentInformationListMenu;
        rlAdpForLvDepartmentInformationListMenu = (RelativeLayout) findViewById(R.id.adp_for_lv_department_information_list_menu_rl);

        View contentView = LayoutInflater.from(mcontext).inflate(R.layout.listview_department_information_list_menu,
                rlAdpForLvDepartmentInformationListMenu,false);
        listView = (ListView) contentView.findViewById(R.id.lv_department_information_list_menu_listview);
        DepartmentInformationListMenuAdapter listViewAdapter = new DepartmentInformationListMenuAdapter(mcontext);
        listViewAdapter.setListBeanMenus(arrayListBeansMenu());
        listView.setAdapter(listViewAdapter);
        listViewAdapter.notifyDataSetChanged();
        int H = getTotalHeightofListViewMenu(listView);

        //为弹窗列表设置点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                int i = mlvDepartmentInformation.getFooterViewsCount();//获取是否存在底部加载控件
                mListBeanSelectsDepartment.get(prePositionDepartment).setStatus(false);
                prePositionDepartment = 0;
                mListBeanSelectsDepartment.get(prePositionDepartment).setStatus(true);
                mListBeanSelectsAll.get(prePositionAll).setStatus(false);
                prePositionAll = 0;
//                int length = mListBeanSelectsDepartment.size();
//                for(int k=0;k<length;k++){
//                    if(k == 0) {
//                        mListBeanSelectsDepartment.get(0).setStatus(true);
//                    }else{
//                        mListBeanSelectsDepartment.get(i).setStatus(false);
//                    }
//                }
                ArrayList<ListBeanSelect> aaa = new ArrayList<ListBeanSelect>();
                aaa.add(new ListBeanSelect("全部", true));
                mListBeanSelectsAll = aaa;
                if (position == 0){
                    //显示全部数据
                    //显示全部数据需要底部加载和下拉刷新控件
                    if (i == 0){
                        //如果底部加载控件为0，则添加底部加载控件
                        mlvDepartmentInformation.addFooterView(footbar);
                    }
                    preDepart = "全部";
                    preSelectAll = "全部";
                    mListBean = arrayListBeans(0);
                    getmData(mData);
                    departmentInformationListAdapter.setListBeans(mListBean);
                    departmentInformationListAdapter.notifyDataSetChanged();
                }else if (position == 1){
                    //只显示未读数据
                    //显示全部数据需要底部加载和下拉刷新控件
                    if (i == 0){
                        mlvDepartmentInformation.addFooterView(footbar);
                    }
                    mListBean = arrayListBeans(1);
                    getmData(mData);
                    departmentInformationListAdapter.setListBeans(mListBean);
                    departmentInformationListAdapter.notifyDataSetChanged();
                }else if (position == 2){
                    //只显示已读数据
                    //显示已读数据不需要底部加载和下拉刷新控件
                    mListBean = arrayListBeans(2);
                    getmData(mData);
                    departmentInformationListAdapter.setListBeans(mListBean);
                    departmentInformationListAdapter.notifyDataSetChanged();
                    if (i != 0){
                        //若存在底部加载控件则去掉
                        mlvDepartmentInformation.removeFooterView(footbar);
                    }
                }else {
                    if (i == 0){
                        mlvDepartmentInformation.addFooterView(footbar);
                    }
                    DepartmentInformationListDataHelper datahelper = new DepartmentInformationListDataHelper();
                    datahelper.setAllRead();
                    mListBean = arrayListBeans(0);
                    getmData(mData);
                    departmentInformationListAdapter.setListBeans(mListBean);
                    departmentInformationListAdapter.notifyDataSetChanged();
                }
            }
        });

        popupWindowMenu.setContentView(contentView);
        popupWindowMenu.setHeight(H);
        popupWindowMenu.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindowMenu.setOutsideTouchable(true);
        popupWindowMenu.setFocusable(true);
        popupWindowMenu.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                isOuterMenu = false;
            }
        });
        popupWindowMenu.setBackgroundDrawable(ContextCompat.getDrawable(mcontext,R.drawable.ic_ga_menu_24dp));
        popupWindowMenu.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        //显示popwindow
        popupWindowMenu.showAsDropDown(findViewById(R.id.act_department_information_list_ibt_menu),0,0);
    }


    public ArrayList<ListBeanMenu> arrayListBeansMenu(){
        ArrayList<ListBeanMenu> listBeanMenus = new ArrayList<>();
        ListBeanMenu listBeanMenu = new ListBeanMenu();
        listBeanMenu.setListViewItem("全部");
        listBeanMenus.add(listBeanMenu);
        ListBeanMenu listBeanMenu1 = new ListBeanMenu();
        listBeanMenu1.setListViewItem("未读");
        listBeanMenus.add(listBeanMenu1);
        ListBeanMenu listBeanMenu2 = new ListBeanMenu();
        listBeanMenu2.setListViewItem("已读");
        listBeanMenus.add(listBeanMenu2);
        ListBeanMenu listBeanMenu3 = new ListBeanMenu();
        listBeanMenu3.setListViewItem("全标为已读");
        listBeanMenus.add(listBeanMenu3);
        return listBeanMenus;
    }


    public static int getTotalHeightofListViewMenu(ListView listView){
        CalculateListViewTool calculateListViewTool = new CalculateListViewTool(listView);
        return calculateListViewTool.CalculateHeight()+45;
    }


    /**
     * selectAll弹窗的设置
     */
    private void showPopupWindowSelectAll(){

        //initListViewAll();
        final RelativeLayout rlAdpForLvDepartmentInformationListSelectAll;
        rlAdpForLvDepartmentInformationListSelectAll = (RelativeLayout)
                findViewById(R.id.adp_for_lv_department_information_list_select_all_rl);

        contentViewAll = LayoutInflater.from(mcontext).inflate
                (R.layout.listview_department_information_list_select_all,
                        rlAdpForLvDepartmentInformationListSelectAll,false);
        nestedListViewAll = (NestedListView) contentViewAll.findViewById(R.id.lv_department_information_list_select_all_listview);
        //nestedListViewAll.setSelection(prePositionAll);
        DepartmentInformationListSelectAllAdapter listViewAdapter = new DepartmentInformationListSelectAllAdapter(mcontext);
        listViewAdapter.setListBeanSelects(mListBeanSelectsAll);
        nestedListViewAll.setAdapter(listViewAdapter);
        listViewAdapter.notifyDataSetChanged();
        int H = getTotalHeightofListViewSelect(nestedListViewAll);
        int W = mrlRL.getWidth();
        nestedListViewAll.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //mListBean.clear();//先要清空，不然会叠加
                pageNum = 1;
                preSelectAll =mListBeanSelectsAll.get(position).getListViewItem();
//                if(preDepart.equals("全部")){
//                    preDepart ="";
//                }
                depart = mListBeanSelectsDepartment.get(prePositionDepartment);
                Log.i("00000000000","predepart"+preDepart);
//                DepartmentInformationListDataHelper departmentInformationListDataHelper= new DepartmentInformationListDataHelper
//                        (preDepart,mListBeanSelectsAll.get(position).getListViewItem(),String.valueOf(medtSearch.getText()),1);
//                mListBean = departmentInformationListDataHelper.setListBeansDepartmentSort();
                final DepartmentInformationListOperation operation = new DepartmentInformationListOperation(mcontext,preDepart,preSelectAll);
                operation.Set0();
                operation.setDepartmentInformationList(pageNum, new
                        DepartmentInformationListOperation.TaskWork(){
                            @Override
                            public void onPostWork() {
                                if(preSelectAll.equals("全部")&&depart.getListViewItem().equals("全部")){
                                    mListBean = arrayListBeans(0);
                                    departmentInformationListAdapter.setListBeans(mListBean);
                                    departmentInformationListAdapter.notifyDataSetChanged();
                                }else {
                                    mListBean = operation.getListBeen();
                                    getReadLocal(mListBean);
                                    departmentInformationListAdapter.setListBeans(mListBean);
                                    departmentInformationListAdapter.notifyDataSetChanged();
                                }
                                    getmData(mData);
                            }
                            @Override
                            public void onPostWorkFoot() {
                            }
                        });
                preSelectAll = "全部";
                isOuterSelectAll = false;
                popupWindowSelectAll.dismiss();
                isOuterSelectAll = false;
                //获取当前点击的item位置并更新上一次点击的item位置以设置文本和图片
                mListBeanSelectsAll.get(position).setStatus(true);
                mListBeanSelectsAll.get(prePositionAll).setStatus(false);
                prePositionAll = position;
                int i = mlvDepartmentInformation.getFooterViewsCount();//获取是否存在底部加载控件
                if (i == 0){
                        //如果底部加载控件为0，则添加底部加载控件
                        mlvDepartmentInformation.addFooterView(footbar);
                }
            }
        });
        popupWindowSelectAll.setContentView(contentViewAll);
        popupWindowSelectAll.setHeight(H);
        popupWindowSelectAll.setWidth(W);
        popupWindowSelectAll.setOutsideTouchable(true);
        popupWindowSelectAll.setFocusable(true);
        popupWindowSelectAll.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                isOuterSelectAll = false;
            }
        });
        popupWindowSelectAll.setBackgroundDrawable(ContextCompat.getDrawable(mcontext,R.drawable.ic_popwindow_white));
        popupWindowSelectAll.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        //显示popwindow
        popupWindowSelectAll.showAsDropDown(findViewById(R.id.act_department_information_list_ibt_select_all),-155,0);
    }
    /**
     * selectDepartment弹窗的设置
     */
    private void showPopupWindowSelectDepartment(){

        initListViewDepartment();

        DepartmentInformationListSelectDepartmentAdapter listViewAdapter
                = new DepartmentInformationListSelectDepartmentAdapter(mcontext);
        listViewAdapter.setListBeanSelects(mListBeanSelectsDepartment);
        nestedListViewDepart.setAdapter(listViewAdapter);
        listViewAdapter.notifyDataSetChanged();
        int H = getTotalHeightofListViewSelect(nestedListViewDepart);
        int W = mrlRL.getWidth();



        popupWindowSelectDepartment.setContentView(contentViewDepartment);
        popupWindowSelectDepartment.setHeight(H);
        popupWindowSelectDepartment.setWidth(W);
        popupWindowSelectDepartment.setOutsideTouchable(true);
        popupWindowSelectDepartment.setFocusable(true);
        popupWindowSelectDepartment.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                isOuterSelectDepartment = false;
            }
        });
        popupWindowSelectDepartment.setBackgroundDrawable(ContextCompat.getDrawable(mcontext,R.drawable.ic_popwindow_white));
        popupWindowSelectDepartment.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        //显示popwindow
        popupWindowSelectDepartment.showAsDropDown(findViewById(R.id.act_department_information_list_ibt_select_department),155,0);
    }

    private void initListViewDepartment(){
        final RelativeLayout rlAdpForLvDepartmentInformationListSelectDepartment;
        rlAdpForLvDepartmentInformationListSelectDepartment = (RelativeLayout)
                findViewById(R.id.adp_for_lv_department_information_list_select_department_rl);

        contentViewDepartment = LayoutInflater.from(mcontext).inflate
                (R.layout.listview_department_information_list_select_department,
                        rlAdpForLvDepartmentInformationListSelectDepartment,false);
        nestedListViewDepart = (NestedListView) contentViewDepartment.findViewById
                (R.id.lv_department_information_list_select_department_listview);
        //nestedListViewDepart.setSelection(prePositionDepartment);
        nestedListViewDepart.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //TODO:根据点击的位置变化listView内容
//                mListBean.clear();//先要清空，不然会叠加
                preDepart=mListBeanSelectsDepartment.get(position).getListViewItem();
                pageNum=1;
                final DepartmentInformationListOperation operation = new DepartmentInformationListOperation(mcontext,preDepart,preSelectAll);
                operation.Set0();
                operation.setDepartmentInformationList(pageNum, new
                        DepartmentInformationListOperation.TaskWork(){
                            @Override
                            public void onPostWork() {
//                                if(preDepart.equals("全部")){
//                                    preSelectAll="全部";
//                                }
                                if(preDepart.equals("全部")){
                                    mListBean = arrayListBeans(0);
                                    departmentInformationListAdapter.setListBeans(mListBean);
                                    departmentInformationListAdapter.notifyDataSetChanged();
                                    mListBeanSelectsAll.get(prePositionAll).setStatus(false);
                                    mListBeanSelectsAll.get(0).setStatus(true);
                                    mListBeanSelectsAll = operation.getListBeenType();
                                    prePositionAll = 0;
                                }else{
//                                    if(!preSelectAll.equals("全部"))
//                                    {mListBeanSelectsAll.get(prePositionAll).setStatus(false);}
                                    mListBeanSelectsAll.get(prePositionAll).setStatus(false);
                                    mListBeanSelectsAll.get(0).setStatus(true);
                                    prePositionAll = 0;
                                    mListBeanSelectsAll = operation.getListBeenType();
                                    titlelength=mListBeanSelectsAll.size();
                                    mListBean = operation.getListBeen();
                                    getReadLocal(mListBean);
                                    departmentInformationListAdapter.setListBeans(mListBean);
                                    departmentInformationListAdapter.notifyDataSetChanged();
                                }
                                getmData(mData);
                            }
                            @Override
                            public void onPostWorkFoot() {
                            }
                        });
                //title=mListBeanSelectsAll.get(prePositionAll);
                isOuterSelectDepartment = false;
                popupWindowSelectDepartment.dismiss();
                isOuterSelectDepartment = false;
                //获取当前点击的item位置并更新上一次点击的item位置以设置文本和图片
                mListBeanSelectsDepartment.get(position).setStatus(true);
                mListBeanSelectsDepartment.get(prePositionDepartment).setStatus(false);
                prePositionDepartment = position;
                int i = mlvDepartmentInformation.getFooterViewsCount();//获取是否存在底部加载控件
                if (i == 0){
                        //如果底部加载控件为0，则添加底部加载控件
                        mlvDepartmentInformation.addFooterView(footbar);
                }
            }
        });
    }


    /**
     * 两个select弹窗公用
     */
    public int getTotalHeightofListViewSelect(ListView listView){
        WindowManager manager = this.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        int height = outMetrics.heightPixels/2;
        CalculateListViewTool calculateListViewTool = new CalculateListViewTool(listView);
        if (calculateListViewTool.CalculateHeight() > height)
            return height;
        else
            return calculateListViewTool.CalculateHeight();
    }
    /**
     * 获得根据搜索框的数据data来从元数据筛选，筛选出来的数据放入mDataSubs里
     * @param mDataSubs
     * @param data
     */

    private void getmDataSub(ArrayList<ListBean> mDataSubs, String data)
    {
        int length = mListBean.size();
        for(int i = 0; i < length; ++i){
            if(mListBean.get(i).getSubject().contains(data))
            {
                ListBean item = mListBean.get(i);
                mDataSubs.add(item);
            }
        }
    }

    /**
     * 获得元数据 并初始化mDatas
     * @param mDatas
     */

    private void getmData(ArrayList<ListBean> mDatas)
    {
        ListBean item;
        mDatas.clear();
        for(ListBean user:mListBean)
        {
            item = user;
            mDatas.add(item);
        }
    }
    public  void  setData(String data)
    {
        mData.clear();//先要清空，不然会叠加
        getmDataSub(mData, data);//获取更新数据
//        Log.i("mDatasize","mData"+mData.size());
//        Log.i("mDatasize","mListBean"+mListBean.size());
//        Log.i("mDatasize","mListBean"+mListBean.size());
        departmentInformationListAdapter.setListBeans(mData);
        departmentInformationListAdapter.notifyDataSetChanged();//更新
    }

    public ArrayList<ListBean> DepartmentInformationListArrayListBeans(String str1,String str2,String str3,int read){
        //连接数据库取出数据存入listview
        DepartmentInformationListDataHelper dataHelper = new DepartmentInformationListDataHelper(str1,str2,str3,read);
        return dataHelper.setListBeansDepartmentSort();
    }
    public  void  setdata(String str1,String str2,String data,int read)
    {
        mListBean.clear();//先要清空，不然会叠加
        if(read==2) {
            mListBean = DepartmentInformationListArrayListBeans("全部","全部",data, read);//获取更新数据
        } else if(read==1){
            mListBean = DepartmentInformationListArrayListBeans(str1,str2,data,read);
        }
        departmentInformationListAdapter.setListBeans(mListBean);
        departmentInformationListAdapter.notifyDataSetChanged();//更新
    }
    /**
     * 设置搜索框的文本更改时的监听器
     */
    private void set_eSearch_TextChanged() {
        medtSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
                //这个应该是在改变的时候会做的动作吧，具体还没用到过。
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub
                //这是文本框改变之前会执行的动作
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                /**这是文本框改变之后 会执行的动作
                 * 因为我们要做的就是，在文本框改变的同时，我们的listview的数据也进行相应的变动，并且如一的
                 显示在界面上。
                 * 所以这里我们就需要加上数据的修改的动作了。
                 */
                setData( String.valueOf(medtSearch.getText()));
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        //重置未读消息数
        UnreadMessageNum.resetdepartmentinformationnum();
    }
    private void getReadLocal(ArrayList<ListBean> arrayList){
        Dao dao = new Dao(mcontext,null, Values.database_version);
        SQLiteDatabase dbRead = dao.getReadableDatabase();
        int length = arrayList.size();
        for(int i=0;i<length;i++){
            String id = arrayList.get(i).getId();
            Cursor cursor = dbRead.rawQuery("select * from ReadStatusTable where id = ?",new String[]{id});
            if(cursor.getCount()!=0){
                arrayList.get(i).setRead(1);
            }
            cursor.close();
        }
    }
}