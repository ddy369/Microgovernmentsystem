package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities;

import android.content.ContentValues;
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

import com.baidu.platform.comapi.map.H;

import java.util.ArrayList;

import app.mgs.com.microgovernmentsystem.R;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.adapters.PublicInformationListAdapter;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.adapters.PublicInformationListMenuAdapter;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.adapters.PublicInformationListSelectAdapter;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.Dao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.ListBean;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.ListBeanMenu;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.ListBeanSelect;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.operations.PublicInformationListOperation;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.CalculateListViewTool;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.NestedListView;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.PublicInformationListDatahelper;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.UnreadMessageNum;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Values;


public class PublicInformationListActivity extends BaseActivity {

    public static PublicInformationListActivity activity = null;
    Context mcontext = this;

    private ArrayList<ListBeanSelect> mListBeanSelects;
    private ArrayList<ListBean> mListBean;
    private ArrayList<ListBean> mData= new ArrayList<>();

    private ListView listView;
    private ListView mlvPublicInformation;
    private NestedListView nestedListView;
    private View contentView;
    private int prePosition;
    private String prePositionId ="111";
    private String preTitle="全部";
    //一些全局变量
    private int pageNum = 1;
    private int isLoading = 0;

    private boolean isOuterMenu = false;//监听菜单弹窗是否处于弹出状态
    private boolean isOuterSelect = false;//监听筛选弹窗是否处于弹出状态
    private int LastItem;//用来记录listview最后一行记录的位置
    public PopupWindow popupWindowMenu = new PopupWindow();
    public PopupWindow popupWindowSelect = new PopupWindow();



    private SwipeRefreshLayout swipeRefreshLayout;

    private ListView lvSelect;
    private ImageButton mibtReturn;
    private ImageButton mibtMenu;
    private ImageButton mibtSelect;
    private EditText medtSearch;
    //    private Button mbtTest;
    private RelativeLayout mrlRL;//为了获取屏幕宽度而设置

    private View footbar;//listview的底部加载布局与控件
    PublicInformationListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        setStyle();

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_public_information_list);

        initView();



        setOnClickListener();
        set_eSearch_TextChanged();
        setOnCreate();

        activity = this;
        //先加载本地的数据，然后联网获取到数据后刷新列表
        //mListBean = arrayListBeans(0);
        mListBean = publicInformationListArrayListBeans("",2);
        getmData(mData);
        //Log.i("mDatasize","publicInformationListArrayListBeans"+mData.size());
        setListView();
        //异步任务接口，把listview传入，联网获取到数据之后刷新listview
        final PublicInformationListOperation publicInformationListOperation = new PublicInformationListOperation(mcontext,"全部");
        publicInformationListOperation.Set1();
        publicInformationListOperation.setPublicInformationList(pageNum, new
                PublicInformationListOperation.TaskWork() {
                    @Override
                    public void onPostWork() {
                        //mListBean = arrayListBeans(0);
                        mListBean = publicInformationListArrayListBeans("",2);
                        getmData(mData);
                        //Log.i("mDatasize","publicInformationListArrayListBeans(\"\",2)"+mData.size());
                        adapter.setListBeans(mListBean);
                        adapter.notifyDataSetChanged();
                        mListBeanSelects = publicInformationListOperation.getListBeanType();
                        //mListBeanSelects = arrayListBeansSelect();
                    }
                    @Override
                    public void onPostWorkFoot() {
                    }
                });
    }
    @Override
    protected void onStart() {
        super.onStart();

//        mListBeanSelects.get(prePosition).setStatus(false);
//        prePosition = 0;
//        mListBeanSelects.get(prePosition).setStatus(true);
        int length = mListBean.size();
        for(int i = 0;i< length; ++i){
            if(mListBean.get(i).getId().equals(prePositionId))
            {
                mListBean.get(i).setRead(1);
            }
        }
        getmData(mData);
        adapter.setListBeans(mListBean);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent();
            intent.setClass(PublicInformationListActivity.this,MainFaceActivity.class);
            startActivity(intent);
            finishActivity();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initView(){
        mibtReturn = (ImageButton) findViewById(R.id.act_public_information_list_ibt_return);
        mibtMenu = (ImageButton) findViewById(R.id.act_public_information_list_ibt_menu);
        mibtSelect = (ImageButton) findViewById(R.id.act_public_information_list_ibt_select);
        medtSearch = (EditText) findViewById(R.id.act_public_information_list_edt_search);
        mrlRL = (RelativeLayout) findViewById(R.id.act_public_information_list_rl);
        mlvPublicInformation = (ListView) findViewById(R.id.act_public_information_list_listview);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.act_public_information_list_swipe_container);
    }

    public void setOnCreate(){
        SharedPreferences sharedPreferences = getSharedPreferences("PublicInformationListActivityOnCreate",
                0);
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putInt("PublicInformationListActivityOnCreate",1);
        editor.apply();
    }

    private void finishActivity(){
        SharedPreferences sharedPreferences = getSharedPreferences("PublicInformationListActivityOnCreate",
                0);
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putInt("PublicInformationListActivityOnCreate",0);
        editor.apply();
        finish();
    }

    /**
     * 联网获取列表信息+下拉刷新控件+底部动态加载
     */
    private void setListView(){
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        footbar = inflater.inflate(R.layout.listview_footbar, null);
        footbar.setVisibility(View.INVISIBLE);
        mlvPublicInformation.addFooterView(footbar,null,false);
        adapter = new PublicInformationListAdapter(mcontext);
        getmData(mListBean);
        adapter.setListBeans(mListBean);
        mlvPublicInformation.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        mlvPublicInformation.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                if (LastItem == adapter.getCount() && i == AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
                    footbar.setVisibility(View.VISIBLE);
                    //如果列表滑到最底端，开始请求加载数据
                    if(isLoading == 0){
                        isLoading = 1;
                        final PublicInformationListDatahelper datahelper = new PublicInformationListDatahelper();
                        pageNum = (int) (Math.ceil((double)(mListBean.size()) / 15) + 1);
                        if (pageNum == 0){
                            pageNum++;
                        }
                        final PublicInformationListOperation publicInformationListOperation = new PublicInformationListOperation(mcontext,preTitle);
                        publicInformationListOperation.setPublicInformationList(pageNum, new
                                PublicInformationListOperation.TaskWork() {
                                    @Override
                                    public void onPostWork() {
                                        if(!preTitle.equals("全部")) {
                                            mListBean.addAll(publicInformationListOperation.getListBean());
                                            getReadLocal(mListBean);
                                            adapter.setListBeans(mListBean);
                                            adapter.notifyDataSetChanged();
                                        }else{
                                            mListBean=datahelper.setListBean();
                                            setdata(String.valueOf(medtSearch.getText()),2);
                                            adapter.setListBeans(mListBean);
                                            adapter.notifyDataSetChanged();
                                        }
                                        getmData(mData);
                                    }
                                    public void onPostWorkFoot() {
                                        footbar.setVisibility(View.INVISIBLE);
                                    }
                                });
                        isLoading = 0;
                    }else {
                        Toast.makeText(mcontext, "正在加载，请稍候...", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                LastItem = i + i1 - 1;
            }
        });

        mlvPublicInformation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent();
                prePositionId = mData.get(position).getId();
                intent.putExtra("id",mData.get(position).getId());//获取当前记录的id，传到publicinformation
                Log.e("id ", "onItemClick: "+mData.get(position).getId() );
                intent.setClass(PublicInformationListActivity.this,PublicInformationActivity.class);
                startActivity(intent);
//                finishActivity();
            }
        });

        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light,
                android.R.color.holo_orange_light, android.R.color.holo_green_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageNum = 1;
                final PublicInformationListOperation publicInformationListOperation = new PublicInformationListOperation(mcontext,preTitle);
                if(preTitle.equals("全部")) {
                    publicInformationListOperation.Set1();
                }
                publicInformationListOperation.setPublicInformationList(pageNum, new
                        PublicInformationListOperation.TaskWork() {
                            @Override
                            public void onPostWork() {
                                //mListBean = arrayListBeans(0);
                                // adapter.setListBeans(mListBean);
                                if(!preTitle.equals("全部")){
                                    mListBean = publicInformationListOperation.getListBean();
                                    getReadLocal(mListBean);
                                }else {
                                    setdata(String.valueOf(medtSearch.getText()),2);
                                }
                                adapter.setListBeans(mListBean);
                                adapter.notifyDataSetChanged();
                                //mListBeanSelects.get(prePosition).setStatus(false);
                                getmData(mData);
//                                int position2 =0;
//                                mListBeanSelects.get(position2).setStatus(true);
//                                mListBeanSelects.get(prePosition).setStatus(false);
//                                prePosition = position2;
//                                mListBeanSelects = arrayListBeansSelect();
                                footbar.setVisibility(View.INVISIBLE);
                                swipeRefreshLayout.setRefreshing(false);
                            }
                            @Override
                            public void onPostWorkFoot() {
                            }
                        });
                }
        });
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

        mibtSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isOuterSelect){
                    popupWindowSelect.dismiss();
                    isOuterSelect = false;
                }else {
                    //mListBeanSelects = arrayListBeansSelect();
                    showPopupWindowSelect();//显示自定义弹窗
                    isOuterSelect = true;
                }
            }
        });


        mibtReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(PublicInformationListActivity.this,MainFaceActivity.class);
                startActivity(intent);
                finishActivity();
            }
        });
    }

    /**
     * 以下为menu弹窗的代码
     */
    private void showPopupWindowMenu(){
        final ListView listView;
        final RelativeLayout rlAdpForLvPublicInformationListMenu;
        rlAdpForLvPublicInformationListMenu = (RelativeLayout) findViewById(R.id.adp_for_lv_public_information_list_menu_rl);

        View contentView = LayoutInflater.from(mcontext).inflate(R.layout.listview_public_information_list_menu,
                rlAdpForLvPublicInformationListMenu,false);
        listView = (ListView) contentView.findViewById(R.id.lv_public_information_list_menu_listview);
        PublicInformationListMenuAdapter listViewAdapter = new PublicInformationListMenuAdapter(mcontext);
        listViewAdapter.setListBeanMenus(arrayListBeansMenu());
        listView.setAdapter(listViewAdapter);
        listViewAdapter.notifyDataSetChanged();
        int H = getTotalHeightofListViewMenu(listView);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override//为弹窗列表设置点击事件
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String str = String.valueOf(medtSearch.getText());
                mListBeanSelects.get(prePosition).setStatus(false);
                prePosition = 0;
                mListBeanSelects.get(prePosition).setStatus(true);
                int i = mlvPublicInformation.getFooterViewsCount();//获取是否存在底部加载控件
                if (position == 0){
                    //显示全部数据
                    //显示全部数据需要底部加载和下拉刷新控件
                    if (i == 0){
                        //如果底部加载控件为0，则添加底部加载控件
                        mlvPublicInformation.addFooterView(footbar);
                    }
                    preTitle = "全部";
                    mlvPublicInformation.setSelection(0);
                    setdata(str,2);
                    getmData(mData);
                    adapter.notifyDataSetChanged();
                }else if (position == 1){
                    //只显示未读数据
                    //显示全部数据需要底部加载和下拉刷新控件
                    if (i == 0){
                        mlvPublicInformation.addFooterView(footbar);
                    }
                    setdata(str,0);
                    getmData(mData);
                    adapter.notifyDataSetChanged();
//                    if (i != 0){
//                        //若存在底部加载控件则去掉
//                        mlvPublicInformation.removeFooterView(footbar);
//                    }
                }else if (position == 2){
                    Log.i("position11111",""+position);
                    //只显示已读数据
                    //显示已读数据不需要底部加载和下拉刷新控件
                    setdata(str,1);
                    getmData(mData);
                    adapter.notifyDataSetChanged();
                    if (i != 0){
                        //若存在底部加载控件则去掉
                        mlvPublicInformation.removeFooterView(footbar);
                    }
                }else {
                        Log.i("position11111",""+position);
                        if (i == 0){
                            mlvPublicInformation.addFooterView(footbar);
                        }
                        PublicInformationListDatahelper datahelper = new PublicInformationListDatahelper();
                        datahelper.setAllRead();
                        setdata(str,2);
                        getmData(mData);
                        adapter.notifyDataSetChanged();
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
        popupWindowMenu.showAsDropDown(findViewById(R.id.act_public_information_list_ibt_menu),0,0);
    }

    /**
     * 取listview的高度
     */
    public static int getTotalHeightofListViewMenu(ListView listView){
        CalculateListViewTool calculateListViewTool = new CalculateListViewTool(listView);
        return calculateListViewTool.CalculateHeight()+45;
    }

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
     * 往adapter中添加数据
     * 返回数组
     */
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


    /**
     * 以下是筛选弹窗的内容
     */
    private void showPopupWindowSelect(){

        final RelativeLayout rlAdpForLvMagazineSelect;
        rlAdpForLvMagazineSelect = (RelativeLayout) findViewById(R.id.adp_for_lv_public_information_list_select_rl);

        contentView = LayoutInflater.from(mcontext).inflate(R.layout.listview_public_information_list_select,
                rlAdpForLvMagazineSelect,false);
        nestedListView = (NestedListView) contentView.findViewById(R.id.lv_public_information_list_select_listview);
        PublicInformationListSelectAdapter listViewAdapter = new PublicInformationListSelectAdapter(mcontext);
        listViewAdapter.setListBeanSelects(mListBeanSelects);
        nestedListView.setAdapter(listViewAdapter);
        listViewAdapter.notifyDataSetChanged();
        int H = getTotalHeightofListViewSelect(nestedListView);
        int W = mrlRL.getWidth();
        nestedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                pageNum=1;
                //mListBean.clear();//先要清空，不然会叠加
//                PublicInformationListDatahelper publicInformationListDatahelper= new PublicInformationListDatahelper
//                        (mListBeanSelects.get(position).getListViewItem());
//                mListBean = publicInformationListDatahelper.setListBeans();
                final PublicInformationListOperation publicInformationListOperation = new PublicInformationListOperation(mcontext,mListBeanSelects.get(position).getListViewItem());
                final String title = mListBeanSelects.get(position).getListViewItem();
                publicInformationListOperation.Set0();
                publicInformationListOperation.setPublicInformationList(pageNum, new
                        PublicInformationListOperation.TaskWork() {
                            @Override
                            public void onPostWork() {
                                preTitle = mListBeanSelects.get(prePosition).getListViewItem();
                                if(!title.equals("全部")) {
                                    mListBean = publicInformationListOperation.getListBean();
                                    getReadLocal(mListBean);
                                }else {
                                    setdata(String.valueOf(medtSearch.getText()),2);
                                }
                                adapter.setListBeans(mListBean);
                                adapter.notifyDataSetChanged();
                                //mListBeanSelects.get(prePosition).setStatus(false);
                                getmData(mData);
//                                int position2 =0;
//                                mListBeanSelects.get(position2).setStatus(true);
//                                mListBeanSelects.get(prePosition).setStatus(false);
//                                prePosition = position2;
//                                mListBeanSelects = arrayListBeansSelect();
                                footbar.setVisibility(View.INVISIBLE);
                            }
                            @Override
                            public void onPostWorkFoot() {
                            }
                        });
                isOuterSelect = false;
                popupWindowSelect.dismiss();
                int i = mlvPublicInformation.getFooterViewsCount();//获取是否存在底部加载控件
                if (i == 0) {
                        //如果底部加载控件为0，则添加底部加载控件
                        mlvPublicInformation.addFooterView(footbar);
                }
                isOuterSelect = false;
                mListBeanSelects.get(position).setStatus(true);
                mListBeanSelects.get(prePosition).setStatus(false);
                prePosition = position;
            }
        });

        popupWindowSelect.setContentView(contentView);
        popupWindowSelect.setHeight(H);
        popupWindowSelect.setWidth(W);
        popupWindowSelect.setOutsideTouchable(true);
        popupWindowSelect.setFocusable(true);
        popupWindowSelect.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                isOuterSelect = false;
            }
        });
        popupWindowSelect.setBackgroundDrawable(ContextCompat.getDrawable(mcontext,R.drawable.ic_popwindow_white));
        popupWindowSelect.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        //显示popwindow
        popupWindowSelect.showAsDropDown(findViewById(R.id.act_public_information_list_ibt_select),-155,0);
    }

    public ArrayList<ListBeanSelect> arrayListBeansSelect(){
        ArrayList<ListBeanSelect> listBeanSelects;
        PublicInformationListDatahelper datahelper = new PublicInformationListDatahelper();
        listBeanSelects=datahelper.getSelectBeans(mcontext);
        return listBeanSelects;
    }

    public ArrayList<ListBean> publicInformationListArrayListBeans(String str, int read){
        //连接数据库取出数据存入listview
        PublicInformationListDatahelper dataHelper = new PublicInformationListDatahelper(str,read);
        return dataHelper.setListBeans();
    }
    /**
     * 获得根据搜索框的数据data来从元数据筛选，筛选出来的数据放入mDataSubs里
     * @param mDataSubs
     * @param data
     */

    private void getmDataSub(ArrayList<ListBean> mDataSubs, String data)
    {
        int length = mListBean.size();
        Log.i("mDatasize","getmDataSubmListBeansize"+length);
        for(int i = 0; i < length; ++i){
            if(mListBean.get(i).getTitle().contains(data))
            {
                ListBean item = mListBean.get(i);
                mDataSubs.add(item);
            }
        }
        Log.i("mDatasize","getmDataSubmListBeansize"+length);
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
    public  void  setdata(String data,int read)
    {
        //mListBean.clear();//先要清空，不然会叠加
        mListBean = publicInformationListArrayListBeans(data,read);
        adapter.setListBeans(mListBean);

        //getmDataSub(mData, data);//获取更新数据

        adapter.notifyDataSetChanged();//更新
//        int i = mlvPublicInformation.getFooterViewsCount();//获取是否存在底部加载控件
//        if (i != 0){
//            //若存在底部加载控件则去掉
//            mlvPublicInformation.removeFooterView(footbar);
//        }
    }
    public  void  setdata(String data)
    {
        mData.clear();//先要清空，不然会叠加
        getmDataSub(mData, data);//获取更新数据
//        Log.i("mDatasize","mData"+mData.size());
//        Log.i("mDatasize","mListBean"+mListBean.size());
//        Log.i("mDatasize","mListBean"+mListBean.size());
        adapter.setListBeans(mData);
        adapter.notifyDataSetChanged();//更新
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
                setdata(String.valueOf(medtSearch.getText()));

            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        //重置未读消息数
        UnreadMessageNum.resetpublicinformationnum();
    }

    private void getReadLocal(ArrayList<ListBean> arrayList){
        Dao dao = new Dao(mcontext,null,Values.database_version);
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