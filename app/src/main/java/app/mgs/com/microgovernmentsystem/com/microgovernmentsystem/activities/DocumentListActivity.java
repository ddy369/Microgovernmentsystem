package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
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
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.adapters.DocumentListAdapter;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.adapters.DocumentListMenuAdapter;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.Dao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.ListBeanDocument;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.ListBeanMenu;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.operations.DocListSetAllReadOperation;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.operations.DocumentListOperation;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.CalculateListViewTool;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.DocumentListDataHelper;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Values;

public class DocumentListActivity extends BaseActivity {

    Context mcontext = this;
    public static DocumentListActivity activity = null;
    private ArrayList<ListBeanDocument> mListBean = null;
    private ImageButton mibtMenu;
    private ImageButton mibtReturn;
    private EditText metSearch;
    private ListView lvDocument;
    private SwipeRefreshLayout swipeRefreshLayout;

    private View footbar;//listview的底部加载布局与控件
    public PopupWindow popupWindow = new PopupWindow();
    private boolean isOuter = false;//监听弹窗是否处于弹出状态
    private int pageNum = 1;
    private int pageNumRead = 1;
    private int pageNumNoRead = 1;
    private String mStatus = "2";//用来记录当前列表显示全部，已读或未读。2表示全部，1表示已读，0表示已读
    private int isLoading = 0;
    private int LastItem;//用来记录listview最后一行记录的位置
    DocumentListAdapter documentListAdapter;
    DocumentListOperation documentListOperation = new DocumentListOperation(mcontext);
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle();

        setContentView(R.layout.activity_document_list);

        initView();

        setOnCreate();

        setOnClickListener();

        activity = this;

        setListView();

        set_eSearch_TextChanged();
        //先加载本地的数据，然后联网获取到数据后刷新列表
        mListBean = documentArrayListBeans("",2);//2代表随意

        //异步任务接口，把listview传入，联网获取到数据之后刷新listview
        documentListOperation.setDocumentList(pageNum,mStatus, new DocumentListOperation.TaskWork() {
            @Override
            public void onPostWork() {
                mListBean = documentArrayListBeans("",2);//2代表随意
                documentListAdapter.setListBeans(mListBean);
                documentListAdapter.notifyDataSetChanged();
            }
            @Override
            public void onPostWorkFoot() {
            }
        });
    }

    @Override
    protected  void onStart(){
        super.onStart();
        if(mStatus == "2"){
            mListBean = documentArrayListBeans("",2);//2代表随意
            mListBean.clear();
            mListBean = documentArrayListBeans(metSearch.getText().toString(),2);//2代表随意
            documentListAdapter.setListBeans(mListBean);
            documentListAdapter.notifyDataSetChanged();
        }else if ( mStatus == "0"){
            pageNumNoRead = 1;
            //只显示未读数据
            documentListOperation.setDocumentList(pageNumNoRead, mStatus, new DocumentListOperation.TaskWork() {
                @Override
                public void onPostWork() {
                    mListBean.clear();//先要清空，不然会叠加
                    mListBean = documentListOperation.getAL();
                    documentListAdapter.setListBeans(mListBean);
                    documentListAdapter.notifyDataSetChanged();
                }
                @Override
                public void onPostWorkFoot() {
                }
            });
        } else if (mStatus == "1"){
        pageNumRead = 1;
        //只显示已读数据
        //显示全部数据需要底部加载和下拉刷新控件
        documentListOperation.setDocumentList(pageNumRead, mStatus, new DocumentListOperation.TaskWork() {
            @Override
            public void onPostWork() {
                mListBean.clear();//先要清空，不然会叠加
                mListBean = documentListOperation.getAL();
                documentListAdapter.setListBeans(mListBean);
                documentListAdapter.notifyDataSetChanged();
            }
            @Override
            public void onPostWorkFoot() {
            }
        });
    }
}

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent();
            intent.setClass(DocumentListActivity.this,MainFaceActivity.class);
            startActivity(intent);
            finishActivity();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void setOnCreate(){
        SharedPreferences sharedPreferences = getSharedPreferences("DocumentListActivityOnCreate", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putInt("DocumentListActivityOnCreate",1);
        editor.apply();
    }

    private void finishActivity(){
        SharedPreferences sharedPreferences = getSharedPreferences("DocumentListActivityOnCreate", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putInt("DocumentListActivityOnCreate",0);
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

    private void initView(){
        mibtMenu = (ImageButton) findViewById(R.id.act_document_list_ibt_menu);
        mibtReturn = (ImageButton) findViewById(R.id.act_document_list_ibt_return);
        metSearch = (EditText) findViewById(R.id.act_document_list_edt_search);
        lvDocument = (ListView) findViewById(R.id.act_document_list_listview);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.act_document_list_swipe_container);
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

        mibtReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(DocumentListActivity.this,MainFaceActivity.class);
                startActivity(intent);
                finishActivity();
            }
        });
    }

    /**
     * 自定义弹窗
     */
    private void showPopupWindow(){
        final ListView listView;
        final RelativeLayout rlAdpForLvDocumentListMenu;
        rlAdpForLvDocumentListMenu = (RelativeLayout) findViewById(R.id.adp_for_lv_document_list_menu_rl);
        View contentView = LayoutInflater.from(mcontext).inflate(R.layout.listview_document_list_menu, rlAdpForLvDocumentListMenu,false);
        listView = (ListView) contentView.findViewById(R.id.lv_document_list_menu_listview);
        DocumentListMenuAdapter listViewAdapter = new DocumentListMenuAdapter(mcontext);
        listViewAdapter.setListBeanMenus(arrayListBeans());
        listView.setAdapter(listViewAdapter);
        listViewAdapter.notifyDataSetChanged();
        int H = getTotalHeightofListView(listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                final String str = String.valueOf(metSearch.getText());
                int i = lvDocument.getFooterViewsCount();//获取是否存在底部加载控件
                if (position == 0){
                    mStatus = "2" ;
                    //显示全部数据
                    //显示全部数据需要底部加载和下拉刷新控件
                    if (i == 0){
                        //如果底部加载控件为0，则添加底部加载控件
                        lvDocument.addFooterView(footbar);
                    }
                    setData(str,2);
                }else if (position == 1){
                    mStatus = "0";
                    pageNumNoRead = 1;
                    //只显示未读数据
                    documentListOperation.setDocumentList(pageNumNoRead, mStatus, new DocumentListOperation.TaskWork() {
                        @Override
                        public void onPostWork() {
                            mListBean.clear();//先要清空，不然会叠加
                            mListBean = documentListOperation.getAL();
                            documentListAdapter.setListBeans(mListBean);
                            documentListAdapter.notifyDataSetChanged();
                        }
                        @Override
                        public void onPostWorkFoot() {
                        }
                    });
                    //setData(str,0);
                }else if (position == 2){
                    mStatus = "1";
                    pageNumRead = 1;
                    //只显示已读数据
                    //显示全部数据需要底部加载和下拉刷新控件
                    documentListOperation.setDocumentList(pageNumRead, mStatus, new DocumentListOperation.TaskWork() {
                        @Override
                        public void onPostWork() {
                            mListBean.clear();//先要清空，不然会叠加
                            mListBean=documentListOperation.getAL();
                            documentListAdapter.setListBeans(mListBean);
                            documentListAdapter.notifyDataSetChanged();
                        }
                        @Override
                        public void onPostWorkFoot() {
                        }
                    });
                }else {
                    if (i == 0){
                        lvDocument.addFooterView(footbar);
                    }
                    DocListSetAllReadOperation operation = new DocListSetAllReadOperation(mcontext);
                    operation.DocListSetAllRead(new DocListSetAllReadOperation.TaskWork(){
                        @Override
                        public void onPostWork() {
                            DocumentListDataHelper documentListAdapter = new DocumentListDataHelper();
                            documentListAdapter.setAllRead();
                            setData(str,2);
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
        popupWindow.showAsDropDown(findViewById(R.id.act_document_list_ibt_menu),0,0);
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
    public ArrayList<ListBeanMenu> arrayListBeans(){
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

    public ArrayList<ListBeanDocument> documentArrayListBeans(String str,int reads){
        //连接数据库取出数据存入listview
        DocumentListDataHelper dataHelper = new DocumentListDataHelper(str,reads);
        return dataHelper.setListBeansDocument();
    }

    /**
     * 联网获取列表信息+下拉刷新控件+底部动态加载
     */
    private void setListView(){
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        footbar = inflater.inflate(R.layout.listview_footbar, null);
        footbar.setVisibility(View.INVISIBLE);
        footbar.setClickable(false);
        int count = lvDocument.getFooterViewsCount();
        if (count == 0){
            lvDocument.addFooterView(footbar,null,false);
        }
        documentListAdapter = new DocumentListAdapter(mcontext);
        documentListAdapter.setListBeans(mListBean);
        lvDocument.setAdapter(documentListAdapter);
        documentListAdapter.notifyDataSetChanged();

        lvDocument.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                if (LastItem == documentListAdapter.getCount() && i == AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
                    footbar.setVisibility(View.VISIBLE);
                    //如果列表滑到最底端，开始请求加载数据
                    if(isLoading == 0){
                        isLoading = 1;
                        int pn;//定义一个局部的pagenumber

                        if(mStatus.equals("2")) {
                            DocumentListDataHelper documentListDataHelper = new DocumentListDataHelper();
                            //pageNum = documentListDataHelper.selectCount() / 15 + 1;
                            pageNum = (int) (Math.ceil((double)documentListDataHelper.selectCount() / 15) + 1);
                            if (pageNum == 0){
                                pageNum++;
                            }
                            pn = pageNum;
                        }
                        else if(mStatus.equals("1")) {
                            pageNumRead = (int) (Math.ceil((double)mListBean.size() / 15) + 1);
                            if (pageNumRead == 0){
                                pageNumRead++;
                            }
                            pn = pageNumRead;
                        }
                        else {
                            pageNumNoRead = (int) (Math.ceil((double)mListBean.size() / 15) + 1);
                            if (pageNumNoRead == 0){
                                pageNumNoRead++;
                            }
                            pn = pageNumNoRead;
                        }
                        documentListOperation.setDocumentList(pn, mStatus, new DocumentListOperation.TaskWork() {
                            @Override
                            public void onPostWork() {
                                if(mStatus.equals("2")) {
                                    setData(String.valueOf(metSearch.getText()), 2);
                                }
                                else{
                                    mListBean.addAll(documentListOperation.getAL());
                                    documentListAdapter.setListBeans(mListBean);
                                    documentListAdapter.notifyDataSetChanged();
                                }
                                isLoading = 0;
                            }
                            @Override
                            public void onPostWorkFoot() {
                                footbar.setVisibility(View.INVISIBLE);
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

        lvDocument.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent();
                intent.putExtra("id",mListBean.get(position).getId());//获取当前记录的记录号，传到document
                intent.putExtra("purview",mListBean.get(position).getPurview());
                    intent.setClass(DocumentListActivity.this,DocumentActivity.class);
//                Dao dao = new Dao(DocumentListActivity.activity, null, Values.database_version);
//                SQLiteDatabase dbRead = dao.getReadableDatabase();
//                dbRead.execSQL(" update DocumentTable set read = ? where id  = ?  ", new String[]{"1", mListBean.get(position).getId()});
                startActivity(intent);
                //finishActivity();
            }
        });
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageNum = 1;
                documentListOperation.setDocumentList(pageNum,mStatus, new DocumentListOperation.TaskWork() {
                    @Override
                    public void onPostWork() {
                        if(mStatus.equals("2")) {
                            setData(String.valueOf(metSearch.getText()), 2);
                            lvDocument.setSelection(0);
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        else{
                            mListBean=documentListOperation.getAL();
                            documentListAdapter.setListBeans(mListBean);
                            documentListAdapter.notifyDataSetChanged();
                            lvDocument.setSelection(0);
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                    @Override
                    public void onPostWorkFoot() {
                    }
                });
            }
        });
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
                setData(String.valueOf(metSearch.getText()),2);
            }
        });
    }

        public  void  setData(String data,int read)
        {
            mListBean.clear();//先要清空，不然会叠加
            mListBean = documentArrayListBeans(data,read);
            documentListAdapter.setListBeans(mListBean);
            documentListAdapter.notifyDataSetChanged();
        }
    @Override
    protected void onResume() {
        super.onResume();
        //重置未读消息数
       // UnreadMessageNum.resetdocumentnum();
    }
}
