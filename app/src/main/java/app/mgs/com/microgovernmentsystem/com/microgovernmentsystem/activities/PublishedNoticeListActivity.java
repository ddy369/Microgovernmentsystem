package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import app.mgs.com.microgovernmentsystem.R;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.adapters.NoticeListAdapter;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.ListBean;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.operations.NoticeListOperation;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.operations.PublishedNoticeListOperation;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tasks.PublishedNoticeListTask;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.NoticeListDatahelper;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.PublishedNoticeListDatahelper;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.UnreadMessageNum;

/**
 * Created by mqs on 2016/10/10.
 */
public class PublishedNoticeListActivity extends BaseActivity {

    Context mcontext = this;

    public static PublishedNoticeListActivity activity = null;

    private ImageButton mibtReturn;
    private EditText metSearch;
    private ListView mlvNotice;
    private SwipeRefreshLayout swipeRefreshLayout;

    PublishedNoticeListOperation noticeListOperation = new PublishedNoticeListOperation(mcontext);
    //listview的底部加载布局与控件
    private View footbar;

    NoticeListAdapter adapter;//列表所使用的适配器

    //一些全局变量
    private int pageNum = 1;
    private int isLoading = 0;
    private int LastItem;//用来记录listview最后一行记录的位置
    private ArrayList<ListBean> mData= new ArrayList<>();
    private ArrayList<ListBean> mListBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle();

        setContentView(R.layout.activity_published_notice_list);

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
        noticeListOperation.setPublishedNoticeList(pageNum, new PublishedNoticeListOperation.TaskWork() {
            @Override
            public void onPostWork() {
                mListBean = arrayListBeans();
                getmData(mData);
                adapter.setListBeans(mData);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void initView(){
        mibtReturn = (ImageButton) findViewById(R.id.act_published_notice_list_ibt_return);
        metSearch = (EditText) findViewById(R.id.act_published_notice_list_edt_search);
        mlvNotice = (ListView) findViewById(R.id.act_published_notice_list_listview);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.act_published_notice_list_swipe_container);
    }

    private void setOnClickListener(){
        mibtReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                        PublishedNoticeListDatahelper datahelper = new PublishedNoticeListDatahelper();
                        pageNum = (int) (Math.ceil((double)datahelper.selectCount() / 15) + 1);
                        if (pageNum == 0){
                            pageNum++;
                        }
                        noticeListOperation.setPublishedNoticeList(pageNum, new PublishedNoticeListOperation.TaskWork() {
                            @Override
                            public void onPostWork() {
                                mListBean = arrayListBeans();
                                //  adapter.setListBeans(mListBean);
                                setdata(metSearch.getText().toString());
                                adapter.notifyDataSetChanged();
                                footbar.setVisibility(View.INVISIBLE);
                                isLoading = 0;
                            }
                        });
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
                intent.putExtra("selfFlag","1");//若由已发通知列表跳入详情页则此标志为1
                intent.setClass(PublishedNoticeListActivity.this,NoticeActivity.class);
                startActivity(intent);
                // finishActivity();
            }
        });
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageNum = 1;
                noticeListOperation.setPublishedNoticeList(pageNum, new PublishedNoticeListOperation.TaskWork() {
                    @Override
                    public void onPostWork() {
                        mListBean = arrayListBeans();
                        // adapter.setListBeans(mListBean);
                        setdata(metSearch.getText().toString());
                        adapter.notifyDataSetChanged();
                        mlvNotice.setSelection(0);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
    }

    public ArrayList<ListBean> arrayListBeans(){
        PublishedNoticeListDatahelper datahelper = new PublishedNoticeListDatahelper();
        ArrayList<ListBean> arrayList = null;
        //连接数据库取出全部数据存入listview
        arrayList = datahelper.setListBeans();
        return arrayList;
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

    public void setOnCreate(){
        SharedPreferences sharedPreferences = getSharedPreferences("PublishedNoticeListActivityOnCreate", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putInt("PublishedNoticeListActivityOnCreate",1);
        editor.apply();
    }

    private void finishActivity(){
        SharedPreferences sharedPreferences = getSharedPreferences("PublishedNoticeListActivityOnCreate", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putInt("PublishedNoticeListActivityOnCreate",0);
        editor.apply();
        finish();
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

    @Override
    protected void onResume() {
        super.onResume();
        //重置未读消息数
        UnreadMessageNum.resetnoticenum();
    }

}
