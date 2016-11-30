package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
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
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.adapters.NoticeListAdapter;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.adapters.NoticeListMenuAdapter;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.ListBean;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.ListBeanMenu;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.operations.NoticeListOperation;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.operations.NoticeListSetAllReadOperation;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.CalculateListViewTool;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.NoticeDatahelper;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.NoticeListDatahelper;


public class NoticeListActivity extends BaseActivity {

    Context mcontext = this;

    public static NoticeListActivity activity = null;

    private ArrayList<ListBean> mListBean;
    private ArrayList<ListBean> mData= new ArrayList<>();

    private ImageButton mibtMenu;
    private ImageButton mibtReturn;
    private ImageButton mibtWrite;
    private EditText metSearch;
    private ListView mlvNotice;
    private SwipeRefreshLayout swipeRefreshLayout;

    NoticeListOperation noticeListOperation = new NoticeListOperation(mcontext);
    //listview的底部加载布局与控件
    private View footbar;

    //自定义的右上角弹窗
    public PopupWindow popupWindow = new PopupWindow();

    //一些全局变量
    private int pageNum = 1;
    private int pageNumRead = 1;
    private int pageNumNoRead = 1;
    private int isLoading = 0;
    private boolean isOuter = false;//监听弹窗是否处于弹出状态
    private int LastItem;//用来记录listview最后一行记录的位置
    private int mStatus = 0;//用来记录当前列表显示全部，已读或未读。0表示全部，1表示未读，2表示已读

    NoticeListAdapter adapter;//列表所使用的适配器

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle();

        setContentView(R.layout.activity_notice_list);

        initView();

        setOnClickListener();//设置点击监听事件

        setOnCreate();
        set_eSearch_TextChanged();
        setOnFocusChange();

        activity = this;

        //先加载本地的数据，然后联网获取到数据后刷新列表
         mListBean = arrayListBeans();

        setListView();

        //异步任务接口，把listview传入，联网获取到数据之后刷新listview
        noticeListOperation.setNoticeList(pageNum,mStatus, new NoticeListOperation.TaskWork() {
            @Override
            public void onPostWork() {
                mListBean = arrayListBeans();
                getmData(mData);
                adapter.setListBeans(mData);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onPostWorkFoot() {
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
//        mListBean = arrayListBeans();
//        getmData(mData);
//        setdata(metSearch.getText().toString());
//        adapter.setListBeans(mData);
//        adapter.notifyDataSetChanged();
        if (mStatus == 0){
            //显示全部数据
            mListBean = arrayListBeans();
            getmData(mData);
            setdata(metSearch.getText().toString());
            adapter.notifyDataSetChanged();
        }else if (mStatus == 1){
            //只显示未读数据
            pageNumNoRead = 1;
            noticeListOperation.setNoticeList(pageNumNoRead,mStatus, new NoticeListOperation.TaskWork() {
                @Override
                public void onPostWork() {
                    mListBean = noticeListOperation.getAL();
                    getmData(mData);
                    setdata(metSearch.getText().toString());
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onPostWorkFoot() {

                }
            });
        }else if (mStatus == 2) {
            //只显示已读数据
            pageNumNoRead = 1;
            noticeListOperation.setNoticeList(pageNumNoRead, mStatus, new NoticeListOperation.TaskWork() {
                @Override
                public void onPostWork() {
                    mListBean = noticeListOperation.getAL();
                    getmData(mData);
                    setdata(metSearch.getText().toString());
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onPostWorkFoot() {

                }
            });
        }
    }

    private void initView(){
        mibtMenu = (ImageButton) findViewById(R.id.act_notice_list_ibt_menu);
        mibtReturn = (ImageButton) findViewById(R.id.act_notice_list_ibt_return);
        mibtWrite = (ImageButton) findViewById(R.id.act_notice_list_ibt_write);
        metSearch = (EditText) findViewById(R.id.act_notice_list_edt_search);
        mlvNotice = (ListView) findViewById(R.id.act_notice_list_listview);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.act_notice_list_swipe_container);
    }

    private void setOnClickListener(){
        mibtMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isOuter){
                    popupWindow.dismiss();
                    isOuter = false;
                }else {
                    showPopupWindow();//显示自定义弹窗
                    isOuter = true;
                }
            }
        });

        mibtWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("kindofpublish",0);//若为0则表示发送通知
                intent.setClass(NoticeListActivity.this,PublishNoticeActivity.class);
                startActivity(intent);
              //  finishActivity();
            }
        });

        mibtReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(NoticeListActivity.this,MainFaceActivity.class);
                startActivity(intent);
                finishActivity();
            }
        });
    }

    /**
     * 联网获取列表信息+下拉刷新控件+底部动态加载
     */
    private void setListView(){
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());

        footbar = inflater.inflate(R.layout.listview_footbar, null);
        footbar.setVisibility(View.INVISIBLE);
        int count = mlvNotice.getFooterViewsCount();
        if (count == 0){
            mlvNotice.addFooterView(footbar,null,false);
        }
        adapter = new NoticeListAdapter(mcontext);
        getmData(mData);
        adapter.setListBeans(mData);
        mlvNotice.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        mlvNotice.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                if (LastItem == adapter.getCount() && i == AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
                    footbar.setVisibility(View.VISIBLE);
                    //如果列表滑到最底端，开始请求加载数据
                    if(isLoading == 0){
                        isLoading = 1;

                        int pn;//定义一个局部的pagenumber
                        if (mStatus==0){
                            NoticeListDatahelper datahelper = new NoticeListDatahelper();
                            pageNum = (int) (Math.ceil((double)datahelper.selectCount() / 15) + 1);
                            if (pageNum == 0){
                                pageNum++;
                            }
                            pn = pageNum;
                        }else if(mStatus==1){
                            pageNumNoRead = (int) (Math.ceil((double)mListBean.size() / 15) + 1);
                            if (pageNumNoRead == 0){
                                pageNumNoRead++;
                            }
                            pn = pageNumNoRead;
                        }else {
                            pageNumRead = (int) (Math.ceil((double)mListBean.size() / 15) + 1);
                            if (pageNumRead == 0){
                                pageNumRead++;
                            }
                            pn = pageNumRead;
                        }
                        noticeListOperation.setNoticeList(pn,mStatus, new NoticeListOperation.TaskWork() {
                            @Override
                            public void onPostWork() {
                                if (mStatus == 0){
                                    mListBean = arrayListBeans();
                                    Log.i("sizesize"," "+arrayListBeans().size());
                                }else if (mStatus == 1){
                                    mListBean.addAll(noticeListOperation.getAL());
                                }else {
                                    mListBean.addAll(noticeListOperation.getAL());
                                }
//                                adapter.setListBeans(mListBean);
                                setdata(metSearch.getText().toString());
                                adapter.notifyDataSetChanged();
                            }
                            public void onPostWorkFoot(){
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
        mlvNotice.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent();
                intent.putExtra("recordid",mData.get(position).getId());//获取当前记录的记录号，传到notice
                Log.e("id ", "onItemClick: "+mData.get(position).getId() );
                intent.putExtra("selfFlag","0");//若由通知列表跳入详情页则此标志为0
                intent.setClass(NoticeListActivity.this,NoticeActivity.class);
                startActivity(intent);
               // finishActivity();
            }
        });
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageNum = 1;
//                noticeListOperation.setNoticeList(pageNum, mStatus,new NoticeListOperation.TaskWork() {
//                    @Override
//                    public void onPostWork() {
//                        mListBean = arrayListBeans();
//                       // adapter.setListBeans(mListBean);
//                        setdata(metSearch.getText().toString());
//                        adapter.notifyDataSetChanged();
//                        mlvNotice.setSelection(0);
//                        swipeRefreshLayout.setRefreshing(false);
//                    }
//                });
                if (mStatus == 0){
                    //显示全部数据
                    pageNum = 1;
                    noticeListOperation.setNoticeList(pageNum, mStatus,new NoticeListOperation.TaskWork() {
                        @Override
                        public void onPostWork() {
                            mListBean.clear();
                            mListBean = arrayListBeans();
                            setdata(metSearch.getText().toString());
                            adapter.notifyDataSetChanged();
                            mlvNotice.setSelection(0);
                            swipeRefreshLayout.setRefreshing(false);
                        }

                        @Override
                        public void onPostWorkFoot() {

                        }
                    });
                }else if (mStatus == 1){
                    //只显示未读数据
                    pageNumNoRead = 1;
                    noticeListOperation.setNoticeList(pageNumNoRead,mStatus, new NoticeListOperation.TaskWork() {
                        @Override
                        public void onPostWork() {
                            mListBean.clear();
                            mListBean = noticeListOperation.getAL();
                            getmData(mData);
                            setdata(metSearch.getText().toString());
                            adapter.notifyDataSetChanged();
                            swipeRefreshLayout.setRefreshing(false);
                        }

                        @Override
                        public void onPostWorkFoot() {

                        }
                    });
                }else if (mStatus == 2) {
                    //只显示已读数据
                    pageNumNoRead = 1;
                    noticeListOperation.setNoticeList(pageNumNoRead, mStatus, new NoticeListOperation.TaskWork() {
                        @Override
                        public void onPostWork() {
                            mListBean.clear();
                            mListBean = noticeListOperation.getAL();
                            getmData(mData);
                            setdata(metSearch.getText().toString());
                            adapter.notifyDataSetChanged();
                            swipeRefreshLayout.setRefreshing(false);
                        }

                        @Override
                        public void onPostWorkFoot() {

                        }
                    });
                }
            }
        });
    }

    public ArrayList<ListBean> arrayListBeans(){
        NoticeListDatahelper datahelper = new NoticeListDatahelper();
        ArrayList<ListBean> arrayList = new ArrayList<>();
//        if (i == 0) {
            //连接数据库取出全部数据存入listview
            arrayList = datahelper.setListBeans();
//        }
//        }else if (i==2){
//            //连接数据库取出已读数据
//            arrayList = datahelper.setListBeansRead(search);
//        }else if (i==1){
//            //连接数据库取出未读数据
//            arrayList = datahelper.setListBeansNoRead(search);
//        }
        return arrayList;
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

    public void setOnCreate(){
        SharedPreferences sharedPreferences = getSharedPreferences("NoticeListActivityOnCreate", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putInt("NoticeListActivityOnCreate",1);
        editor.apply();
    }

    private void finishActivity(){
        SharedPreferences sharedPreferences = getSharedPreferences("NoticeListActivityOnCreate", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putInt("NoticeListActivityOnCreate",0);
        editor.apply();
        finish();
    }

    /**
     * 自定义弹窗
     */
    private void showPopupWindow(){
        final ListView listView;
        final RelativeLayout rlAdpForLvNoticeListMenu;
        rlAdpForLvNoticeListMenu = (RelativeLayout) findViewById(R.id.adp_for_lv_notice_list_menu_rl);
        View contentView = LayoutInflater.from(mcontext).inflate(R.layout.listview_notice_list_menu, rlAdpForLvNoticeListMenu,false);
        listView = (ListView) contentView.findViewById(R.id.lv_notice_list_menu_listview);
        NoticeListMenuAdapter listViewAdapter = new NoticeListMenuAdapter(mcontext);
        listViewAdapter.setListBeanMenus(arrayListBeansMenu());
        listView.setAdapter(listViewAdapter);
        listViewAdapter.notifyDataSetChanged();
        int H = getTotalHeightofListView(listView);

        //为弹窗列表设置点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (position == 0){
                    //显示全部数据
                    mStatus = 0;
                    mListBean = arrayListBeans();
                    getmData(mData);
//                    adapter.setListBeans(arrayListBeans(0));
                    adapter.notifyDataSetChanged();
                }else if (position == 1){
                    //只显示未读数据
                    mStatus = 1;
                    pageNumNoRead = 1;
                    noticeListOperation.setNoticeList(pageNumNoRead,mStatus, new NoticeListOperation.TaskWork() {
                        @Override
                        public void onPostWork() {
                            mListBean = noticeListOperation.getAL();
                            getmData(mData);
//                            adapter.setListBeans(mListBean);
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onPostWorkFoot() {

                        }
                    });
                }else if (position == 2){
                    //只显示已读数据
                    mStatus = 2;
                    pageNumNoRead = 1;
                    noticeListOperation.setNoticeList(pageNumNoRead,mStatus, new NoticeListOperation.TaskWork() {
                        @Override
                        public void onPostWork() {
                            mListBean = noticeListOperation.getAL();
                            getmData(mData);
//                            adapter.setListBeans(mListBean);
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onPostWorkFoot() {

                        }
                    });
                }else if (position == 4){
                    // TODO : 跳转到已发布页面查看自己发布的通知
                    Intent intent = new Intent();
                    intent.setClass(NoticeListActivity.this,PublishedNoticeListActivity.class);
                    startActivity(intent);
                } else {
                    /**
                     * 此处向服务器提交一个已全部标记已读的字段,若有网络，并返回成功，之后进行本地修改
                     */
                    NoticeListSetAllReadOperation operation = new NoticeListSetAllReadOperation(mcontext);
                    operation.NoticeListSetAllRead(new NoticeListSetAllReadOperation.TaskWork() {
                        @Override
                        public void onPostWork() {
                            NoticeDatahelper datahelper = new NoticeDatahelper();
                            datahelper.setAllRead();
                            adapter.setListBeans(arrayListBeans());
                            adapter.notifyDataSetChanged();
                            popupWindow.dismiss();
                        }
                    });
                }
            }
        });
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
        popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(mcontext,R.drawable.ic_ga_menu_24dp));
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        //显示popwindow
        popupWindow.showAsDropDown(findViewById(R.id.act_notice_list_ibt_menu),0,0);
    }

    /**
     * 取listview的高度
     */
    public static int getTotalHeightofListView(ListView listView){
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
        ListBeanMenu listBeanMenu4 = new ListBeanMenu();
        listBeanMenu4.setListViewItem("已发布");
        listBeanMenus.add(listBeanMenu4);
        return listBeanMenus;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent();
            intent.setClass(NoticeListActivity.this,MainFaceActivity.class);
            startActivity(intent);
            finishActivity();
        }
        return super.onKeyDown(keyCode, event);
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
            if(mListBean.get(i).getTitle().contains(data))
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
    public  void  setdata(String data)
    {
        mData.clear();//先要清空，不然会叠加

        getmDataSub(mData, data);//获取更新数据

        adapter.notifyDataSetChanged();//更新
    }
    /**
     * 设置搜索框的文本更改时的监听器
     */
    private void set_eSearch_TextChanged()
    {
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
                setdata(metSearch.getText().toString());
                Log.i("changed","changed!");

            }
        });

    }

    /**
     * 设置获取焦点时的监听器*/
    private void setOnFocusChange(){
        metSearch.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    // 此处为得到焦点时的处理内容
                    getmData(mData);
                    adapter.setListBeans(mData);
                    adapter.notifyDataSetChanged();
                } else {
                    // 此处为失去焦点时的处理内容

                }
            }
        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        //重置未读消息数
      //  UnreadMessageNum.resetnoticenum();

    }
}
