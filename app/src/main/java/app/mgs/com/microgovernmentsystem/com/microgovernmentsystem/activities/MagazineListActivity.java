package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
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
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.adapters.MagazineListAdapter;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.adapters.MagazineListMenuAdapter;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.adapters.MagazineListSelectAdapter;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.ListBean;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.ListBeanMagazine;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.ListBeanMenu;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.ListBeanSelect;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.operations.MagazineListOperation;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.CalculateListViewTool;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.MagazineDataHelper;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.MagazineListDataHelper;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.NestedListView;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.UnreadMessageNum;


public class MagazineListActivity extends BaseActivity {

    public static MagazineListActivity activity = null;
    Context mcontext = this;
    private ArrayList<ListBeanSelect> mListBeanSelects =null;
    private ListView lvSelect;
    private NestedListView nestedListView;
    private View contentView;
    private int prePosition;
    private boolean isOuterMenu = false;//监听菜单弹窗是否处于弹出状态
    private boolean isOuterSelect = false;//监听筛选弹窗是否处于弹出状态
    public PopupWindow popupWindowMenu = new PopupWindow();
    public PopupWindow popupWindowSelect = new PopupWindow();

    private ImageButton mibtMenu;
    private EditText metSearch;
    private ImageButton mibtSelect;
    private ImageButton mibtReturn;
    private RelativeLayout mrlRL;//为了获取屏幕宽度而设置

    MagazineListAdapter magazineListAdapter;
    MagazineListOperation magazineListOperation = new MagazineListOperation(mcontext);
    private View footbar;//listview的底部加载布局与控件
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<ListBeanMagazine> mListBean = null;
    private int pageNum = 1;
    private int isLoading = 0;
    private int LastItem;//用来记录listview最后一行记录的位置
    private ListView lvMagazine;
    private String magType ="";
    private int pageRead = 1 ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle();

        setContentView(R.layout.activity_magazine_list);

        initView();


        setOnClickListener();

        setOnCreate();

        activity = this;

        set_eSearch_TextChanged();

        //异步任务接口，把listview传入，联网获取到数据之后刷新listview
        magazineListOperation.setMagazineList(pageNum,magType ,new MagazineListOperation.TaskWork() {
            @Override
            public void onPostWork() {
                mListBean = magazineArrayListBeans("",2);
                magazineListAdapter.setListBeans(mListBean);
                magazineListAdapter.notifyDataSetChanged();
                mListBeanSelects = arrayListBeansSelect();
            }

            @Override
            public void onPostWorkFoot() {
            }
        });

        setListView();

    }

    @Override
    protected  void onStart(){
        super.onStart();
        mListBean = magazineArrayListBeans("",2);
//        magazineListAdapter.setListBeans(mListBean);
//        magazineListAdapter.notifyDataSetChanged();
        if(magType.length()<1) {
            setData(String.valueOf(metSearch.getText()), 2);
            isLoading = 0;
            footbar.setVisibility(View.INVISIBLE);
        }
        else{
            pageRead = 1;
            magazineListOperation.setMagazineList(pageRead,magType ,new MagazineListOperation.TaskWork() {
                @Override
                public void onPostWork() {
                    mListBean.clear();//先要清空，不然会叠加
                    mListBean=magazineListOperation.getAL();
                    magazineListAdapter.setListBeans(mListBean);
                    magazineListAdapter.notifyDataSetChanged();
                    mListBeanSelects = arrayListBeansSelect();
                }
                public void onPostWorkFoot() {
                    footbar.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    public ArrayList<ListBeanMagazine> magazineArrayListBeans(String str,int read){
        //连接数据库取出数据存入listview
        MagazineListDataHelper dataHelper = new MagazineListDataHelper(str,read);
        return dataHelper.setListBeansMagazine();
    }

    /**
     * 联网获取列表信息+下拉刷新控件+底部动态加载
     */
    private void setListView(){
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        footbar = inflater.inflate(R.layout.listview_footbar, null);
        footbar.setClickable(false);
        footbar.setVisibility(View.INVISIBLE);
        int count = lvMagazine.getFooterViewsCount();
        if (count == 0){
            lvMagazine.addFooterView(footbar,null,false);
        }
        magazineListAdapter = new MagazineListAdapter(mcontext);
        magazineListAdapter.setListBeans(mListBean);
        lvMagazine.setAdapter(magazineListAdapter);
        magazineListAdapter.notifyDataSetChanged();

        lvMagazine.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                footbar.setVisibility(View.VISIBLE);
                if (LastItem == magazineListAdapter.getCount() && i == AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
                    //如果列表滑到最底端，开始请求加载数据
                    if(isLoading == 0){
                        isLoading = 1;
                        int pn = 1;
                        if(magType.length()<1) {
                            MagazineListDataHelper magazineListDataHelper = new MagazineListDataHelper();
                            pageNum = (int) (Math.ceil((double)magazineListDataHelper.selectCount() / 15) + 1);
                            if (pageNum == 0){
                                pageNum++;
                            }
                            pn = pageNum;
                        }else{
                            pageRead = (int) (Math.ceil((double)mListBean.size() / 15) + 1);
                            if (pageRead == 0){
                                pageRead++;
                            }
                            pn = pageRead;
                        }
                        if(magType.length()<1) {
                            magazineListOperation.setMagazineList(pn, magType, new MagazineListOperation.TaskWork() {
                                @Override
                                public void onPostWork() {

                                    setData(String.valueOf(metSearch.getText()), 2);
                                    isLoading = 0;
                                    footbar.setVisibility(View.INVISIBLE);

//
                                    int position1 = 0;
                                    mListBeanSelects.get(position1).setStatus(true);
                                    mListBeanSelects.get(prePosition).setStatus(false);
                                    prePosition = position1;
                                    mListBeanSelects = arrayListBeansSelect();
                                }
                                public void onPostWorkFoot() {
                                    footbar.setVisibility(View.INVISIBLE);
                                }
                            });
                        }else{
                            magazineListOperation.setMagazineList(pageRead,magType ,new MagazineListOperation.TaskWork() {
                                @Override
                                public void onPostWork() {
                                    mListBean.addAll(magazineListOperation.getAL());
                                    magazineListAdapter.setListBeans(mListBean);
                                    magazineListAdapter.notifyDataSetChanged();
                                    mListBeanSelects = arrayListBeansSelect();
                                    footbar.setVisibility(View.INVISIBLE);
                                }
                                public void onPostWorkFoot() {
                                    footbar.setVisibility(View.INVISIBLE);
                                }
                            });
                            isLoading = 0;
                        }
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

        lvMagazine.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent();
                String id = mListBean.get(position).getId();
                String contentType = "";
                intent.putExtra("id",id);//获取当前记录的记录号，传到Magazine
                MagazineDataHelper magazineListDataHelper = new MagazineDataHelper();
                if(magazineListDataHelper.selectCount(id) == 0 ){
                    contentType = mListBean.get(position).getTitle()+","+mListBean.get(position).getSubject()+","+mListBean.get(position).getSendPeople()+","+mListBean.get(position).getUpdateDate();
                }
                intent.putExtra("contentType",contentType);
                intent.putExtra("sysId",mListBean.get(position).getSysId());
                intent.putExtra("year_no",mListBean.get(position).getYear_no());
                intent.setClass(MagazineListActivity.this,MagazineActivity.class);
                startActivity(intent);
            }
        });

        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light,
                android.R.color.holo_orange_light, android.R.color.holo_green_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageNum = 1;
                if(magType.length()<1){
                    magazineListOperation.setMagazineList(pageNum,magType, new MagazineListOperation.TaskWork() {
                        @Override
                        public void onPostWork() {

                            setData(String.valueOf(metSearch.getText()),2);
                            lvMagazine.setSelection(0);
                            swipeRefreshLayout.setRefreshing(false);


                            int position2 =0;
                            mListBeanSelects.get(position2).setStatus(true);
                            mListBeanSelects.get(prePosition).setStatus(false);
                            prePosition = position2;
                            mListBeanSelects = arrayListBeansSelect();
                        }
                        public void onPostWorkFoot() {
                            footbar.setVisibility(View.INVISIBLE);
                        }
                    });
                }else{
                    magazineListOperation.setMagazineList(pageRead,magType ,new MagazineListOperation.TaskWork() {
                        @Override
                        public void onPostWork() {
                            mListBean=magazineListOperation.getAL();
                            magazineListAdapter.setListBeans(mListBean);
                            magazineListAdapter.notifyDataSetChanged();
                            mListBeanSelects = arrayListBeansSelect();
                            lvMagazine.setSelection(0);
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        public void onPostWorkFoot() {
                            footbar.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        });
        ;
    }

    /**
     * 设置搜索框的文本更改时的监听器
     */
    private void set_eSearch_TextChanged() {
        metSearch.addTextChangedListener(new TextWatcher() {

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
                 * 因为我们要做的就是，在文本框改变的同时，我们的listview的数据也进行相应的变动，并且如一的显示在界面上。
                 * 所以这里我们就需要加上数据的修改的动作了。
                 */

                if(magType.length()<1){
                setData(String.valueOf(metSearch.getText()),2);
                }else{
                    ArrayList<ListBeanMagazine> listBeanNew = new ArrayList<>();
                    for(ListBeanMagazine listBeanMagazine : mListBean){
                        if(listBeanMagazine.getSubject().contains(metSearch.getText())){
                            listBeanNew.add(listBeanMagazine);
                        }
                    }
                    magazineListAdapter.setListBeans(listBeanNew);
                    magazineListAdapter.notifyDataSetChanged();
                    mListBeanSelects = arrayListBeansSelect();
                    lvMagazine.setSelection(0);
                    swipeRefreshLayout.setRefreshing(false);


                }
            }
        });
    }

    public  void  setData(String data,int read)
    {
        mListBean.clear();//先要清空，不然会叠加
        mListBean = magazineArrayListBeans(data,read);
        magazineListAdapter.setListBeans(mListBean);
        magazineListAdapter.notifyDataSetChanged();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent();
            intent.setClass(MagazineListActivity.this,MainFaceActivity.class);
            startActivity(intent);
            finishActivity();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void setOnCreate(){
        SharedPreferences sharedPreferences = getSharedPreferences("MagazineListActivityOnCreate", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putInt("MagazineListActivityOnCreate",1);
        editor.apply();
    }

    private void finishActivity(){
        SharedPreferences sharedPreferences = getSharedPreferences("MagazineListActivityOnCreate", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putInt("MagazineListActivityOnCreate",0);
        editor.apply();
        finish();
    }

    private void initView(){
        mibtMenu = (ImageButton) findViewById(R.id.act_magazine_list_ibt_menu);
        metSearch = (EditText) findViewById(R.id.act_magazine_list_edt_search);
        mibtSelect = (ImageButton) findViewById(R.id.act_magazine_list_ibt_select);
        mibtReturn = (ImageButton) findViewById(R.id.act_magazine_list_ibt_return);
        mrlRL = (RelativeLayout) findViewById(R.id.act_magazine_list_rl);
        lvMagazine = (ListView) findViewById(R.id.act_magazine_list_listview);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.act_magazine_list_swipe_container);
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
                    showPopupWindowSelect();//显示自定义弹窗
                    isOuterSelect = true;
                }
            }
        });
        mibtReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(MagazineListActivity.this,MainFaceActivity.class);
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
        final RelativeLayout rlAdpForLvMagazineMenu;
        rlAdpForLvMagazineMenu = (RelativeLayout) findViewById(R.id.adp_for_lv_magazine_list_menu_rl);
        View contentView = LayoutInflater.from(mcontext).inflate(R.layout.listview_magazine_list_menu,
                rlAdpForLvMagazineMenu,false);
        listView = (ListView) contentView.findViewById(R.id.lv_magazine_list_menu_listview);
        MagazineListMenuAdapter listViewAdapter = new MagazineListMenuAdapter(mcontext);
        listViewAdapter.setListBeanMenus(arrayListBeansMenu());
        listView.setAdapter(listViewAdapter);
        listViewAdapter.notifyDataSetChanged();
        int H = getTotalHeightofListViewMenu(listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String str = String.valueOf(metSearch.getText());
                int i = lvMagazine.getFooterViewsCount();//获取是否存在底部加载控件
                if (position == 0){
                    //显示全部数据
                    //显示全部数据需要底部加载和下拉刷新控件
                    if (i == 0){
                        //如果底部加载控件为0，则添加底部加载控件
                        lvMagazine.addFooterView(footbar,null,false);
                    }
                    magazineListOperation.setMagazineList(pageNum,"" ,new MagazineListOperation.TaskWork() {
                        @Override
                        public void onPostWork() {
                            mListBean = magazineArrayListBeans("",2);
                            magazineListAdapter.setListBeans(mListBean);
                            magazineListAdapter.notifyDataSetChanged();
                            mListBeanSelects = arrayListBeansSelect();
                        }
                        public void onPostWorkFoot() {
                            footbar.setVisibility(View.INVISIBLE);
                        }
                    });
                }else if (position == 1){
                    //只显示未读数据
                    //显示全部数据需要底部加载和下拉刷新控件
                    if (i == 0){
                        lvMagazine.addFooterView(footbar,null,false);
                    }
                    setData(str,0);
                }else if (position == 2){
                    //只显示已读数据
                    //显示已读数据不需要底部加载和下拉刷新控件
                    setData(str,1);
                    if (i != 0){
                        //若存在底部加载控件则去掉
                        lvMagazine.removeFooterView(footbar);
                    }
                }else {
                    if (i == 0){
                        lvMagazine.addFooterView(footbar);
                    }
                    MagazineListDataHelper magazineListDataHelper = new MagazineListDataHelper();
                    magazineListDataHelper.setAllRead();
                    setData(str,2);
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
        popupWindowMenu.showAsDropDown(findViewById(R.id.act_magazine_list_ibt_menu),0,0);
    }

    /**
     * 取listview的高度
     */
    public static int getTotalHeightofListViewMenu(ListView listView){
        CalculateListViewTool calculateListViewTool = new CalculateListViewTool(listView);
        return calculateListViewTool.CalculateHeight()+45;
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
        rlAdpForLvMagazineSelect = (RelativeLayout) findViewById(R.id.adp_for_lv_magazine_list_select_rl);

        contentView = LayoutInflater.from(mcontext).inflate(R.layout.listview_magazine_list_select,
                rlAdpForLvMagazineSelect,false);
        nestedListView = (NestedListView) contentView.findViewById(R.id.lv_magazine_list_select_listview);
        MagazineListSelectAdapter listViewAdapter = new MagazineListSelectAdapter(mcontext);
        listViewAdapter.setListBeanSelects(mListBeanSelects);
        nestedListView.setAdapter(listViewAdapter);
        listViewAdapter.notifyDataSetChanged();
        int H = getTotalHeightofListViewSelect(nestedListView);
        int W = mrlRL.getWidth();
        nestedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                mListBean.clear();//先要清空，不然会叠加
                 magType = mListBeanSelects.get(position).getListViewItem();
                if(magType.equals("全部")){
                    magType = "";
                    magazineListOperation.setMagazineList(pageNum,magType, new MagazineListOperation.TaskWork() {
                        @Override
                        public void onPostWork() {

                            setData(String.valueOf(metSearch.getText()),2);
                            lvMagazine.setSelection(0);
                            swipeRefreshLayout.setRefreshing(false);


                            int position2 =0;
                            mListBeanSelects.get(position2).setStatus(true);
                            mListBeanSelects.get(prePosition).setStatus(false);
                            prePosition = position2;
                            mListBeanSelects = arrayListBeansSelect();
                        }
                        public void onPostWorkFoot() {
                            footbar.setVisibility(View.INVISIBLE);
                        }
                    });
                }
                else {
                    //final MagazineListOperation magazineListOperations = new MagazineListOperation(mcontext,magType);
                    magazineListOperation.setMagazineList(pageRead, magType, new MagazineListOperation.TaskWork() {
                        @Override
                        public void onPostWork() {
                            mListBean = magazineListOperation.getAL();
                            magazineListAdapter.setListBeans(mListBean);
                            magazineListAdapter.notifyDataSetChanged();
                            mListBeanSelects = arrayListBeansSelect();

                        }

                        public void onPostWorkFoot() {
                            footbar.setVisibility(View.INVISIBLE);
                        }
                    });
                }
                isOuterSelect = false;
                popupWindowSelect.dismiss();
                int i = lvMagazine.getFooterViewsCount();//获取是否存在底部加载控件
                if (i == 0) {
                    //如果底部加载控件为0，则添加底部加载控件
                    lvMagazine.addFooterView(footbar);
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
        popupWindowSelect.showAsDropDown(findViewById(R.id.act_magazine_list_ibt_select),-155,0);
    }


    public ArrayList<ListBeanSelect> arrayListBeansSelect(){
        ArrayList<ListBeanSelect> listBeanSelects;
//        listBeanSelects.add(new ListBeanSelect("全部",true));
        listBeanSelects = magazineListOperation.getSelect();
//        MagazineListDataHelper magazineListDataHelper = new MagazineListDataHelper();
//        listBeanSelects=magazineListDataHelper.getSelectBeans(mcontext);
        return listBeanSelects;
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
    @Override
    protected void onResume() {
        super.onResume();
        //重置未读消息数
        UnreadMessageNum.resetmagazinenum();
    }
}