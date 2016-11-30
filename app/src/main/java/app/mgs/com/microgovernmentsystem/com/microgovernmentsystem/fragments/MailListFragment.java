package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import app.mgs.com.microgovernmentsystem.R;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.ImportContactsActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.RecentContactsActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.adapters.GovernmentAffairsListViewAdapter;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.adapters.MailListSelectAdapter;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.im.ConversationListActiviity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.ListBeanMenu;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.ListBeanSelect;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.CalculateListViewTool;


public class MailListFragment extends Fragment {


    private RelativeLayout mrlContent;
    private Button mbtImport;
    private EditText metSearch;
    private ImageButton mibtSelect;
    public ImageButton mibtMenu;
    //以下为自定义popwindow用到的变量
    private ArrayList<ListBeanSelect> mListBeanSelects;
    private int prePosition;
    private ListView listView;
    private View contentView;
    public ListView listView1;
    private View contentView1;
    private boolean isOuterSelect = false;//监听筛选弹窗是否处于弹出状态
    public PopupWindow popupWindowSelect = new PopupWindow();
    //
    public PopupWindow popupWindow = new PopupWindow();
    private boolean isOuter = false;//监听弹窗是否处于弹出状态
    //以下为两个fragment
    private SortByDepartmentFragment sortByDepartmentFragment;
    private SortByNameFragment sortByNameFragment;
    //搜索用fragment
    private SerachFragment serachFragment;
    //设置fragmentManager
    private android.app.FragmentManager fragmentManager;
    private int lastfragment;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mail_list,container,false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initViews();

        mListBeanSelects = arrayListBeansSelect();

        setOnClickListener();

        fragmentManager = getFragmentManager();

        setTabSelection(0);

    }

    private void initViews(){
        mrlContent = (RelativeLayout) getActivity().findViewById(R.id.frg_mail_list_rl);
      //  mbtImport = (Button) getActivity().findViewById(R.id.frg_mail_list_bt_import);
        metSearch = (EditText) getActivity().findViewById(R.id.frg_mail_list_edt_search);
        mibtSelect = (ImageButton) getActivity().findViewById(R.id.frg_mail_list_ibt_select);
        mibtMenu = (ImageButton) getActivity().findViewById(R.id.frg_ga_ibt_menu1);
        //

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


       /* mbtImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.setClass(getActivity(),ImportContactsActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });*/
        setOnFocusChange();
        set_eSearch_TextChanged();
    }

    /**
     * 以下是筛选弹窗的内容
     */
    private void showPopupWindowSelect(){

        initListView();

        MailListSelectAdapter listViewAdapter = new MailListSelectAdapter(getActivity());
        listViewAdapter.setListBeanSelects(mListBeanSelects);
        listView.setAdapter(listViewAdapter);
        listViewAdapter.notifyDataSetChanged();
        int H = getTotalHeightofListViewSelect(listView);
        int W = mrlContent.getWidth();

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
        popupWindowSelect.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(),R.drawable.ic_popwindow_white));
        popupWindowSelect.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        //显示popwindow
        popupWindowSelect.showAsDropDown(getActivity().findViewById(R.id.frg_mail_list_ibt_select),-155,50);
    }

    /**
     * 初始化listView并设置点击监听事件
     */
    private void initListView(){
        final RelativeLayout rlAdpForLvMailListSelect;
        rlAdpForLvMailListSelect =
                (RelativeLayout) getActivity().findViewById(R.id.adp_for_lv_mail_list_select_rl);
        contentView = LayoutInflater.from(getActivity()).inflate(R.layout.listview_mail_list_select,
                rlAdpForLvMailListSelect,false);
        listView = (ListView) contentView.findViewById(R.id.lv_mail_list_select_listview);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (position==0){
                    popupWindowSelect.dismiss();
                    isOuterSelect = false;
                    setTabSelection(0);
                }else {
                    popupWindowSelect.dismiss();
                    isOuterSelect = false;
                    setTabSelection(1);
                }
                //获取当前点击的item位置并更新上一次点击的item位置以设置文本和图片
                mListBeanSelects.get(position).setStatus(true);
                mListBeanSelects.get(prePosition).setStatus(false);
                prePosition = position;
            }
        });
    }

    public ArrayList<ListBeanSelect> arrayListBeansSelect(){
        ArrayList<ListBeanSelect> listBeanSelects = new ArrayList<>();
        ListBeanSelect listBeanSelect = new ListBeanSelect();
        listBeanSelect.setListViewItem("按部门排序");
        listBeanSelect.setStatus(true);
        listBeanSelects.add(listBeanSelect);
        ListBeanSelect listBeanSelect1 = new ListBeanSelect();
        listBeanSelect1.setListViewItem("按姓名排序");
        listBeanSelect1.setStatus(false);
        listBeanSelects.add(listBeanSelect1);
        return listBeanSelects;
    }

    public static int getTotalHeightofListViewSelect(ListView listView){
        CalculateListViewTool calculateListViewTool = new CalculateListViewTool(listView);
        return calculateListViewTool.CalculateHeight();
    }


    /**
     * 根据传入的index参数来设置选中的tab页。
     *
     */
    private void setTabSelection(int index)
    {
        android.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        hideFragments(transaction);

        switch (index)
        {
            case 0:
                metSearch.clearFocus();
                lastfragment=index;
                if (sortByDepartmentFragment == null)
                {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
                    sortByDepartmentFragment = new SortByDepartmentFragment();
                    transaction.add(R.id.frg_mail_list_rl, sortByDepartmentFragment);
                } else
                {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    transaction.show(sortByDepartmentFragment);
                }
                break;
            case 1:
                lastfragment=index;
                metSearch.clearFocus();
                if (sortByNameFragment == null)
                {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
                    sortByNameFragment = new SortByNameFragment();
                    transaction.add(R.id.frg_mail_list_rl, sortByNameFragment);
                } else
                {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    transaction.show(sortByNameFragment);
                }
                break;
            case 2:
                if (serachFragment == null)
                {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
                    serachFragment = new SerachFragment();

                    transaction.add(R.id.frg_mail_list_rl, serachFragment);
                } else
                {
                    // 如果MessageFragment不为空，则直接将它显示出来

                    transaction.show(serachFragment);
                }
                break;
        }
        transaction.commit();
    }


    /**
     * 将所有的Fragment都置为隐藏状态。
     *
     * @param transaction
     *            用于对Fragment执行操作的事务
     */
    private void hideFragments(android.app.FragmentTransaction transaction)
    {
        if (sortByDepartmentFragment != null)
        {
            transaction.hide(sortByDepartmentFragment);
        }
        if (sortByNameFragment != null)
        {
            transaction.hide(sortByNameFragment);
        }
        if (serachFragment != null)
        {
            transaction.hide(serachFragment);
        }
    }
/**
 * 设置获取焦点时的监听器*/
    private void setOnFocusChange(){
        metSearch.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                // 此处为得到焦点时的处理内容
                    setTabSelection(2);
                } else {
              // 此处为失去焦点时的处理内容

                }
            }
        });

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
                serachFragment.setdata(metSearch.getText().toString());

            }
        });

    }
    /**
     * 设置叉叉的点击事件，即清空功能
     */

    /*private void set_ivDeleteText_OnClick()
    {
        ivDeleteText = (ImageView) getActivity().findViewById(R.id.ivDeleteText);
        ivDeleteText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                eSearch.setText("");
            }
        });
    }*/
    // 返回键按下时会被调用
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            // TODO
            setTabSelection(lastfragment);
            return true;
        }
        return false;
    }
    /**
     * 自定义弹窗
     */
    private void showPopupWindow(){

        initListView2();

        GovernmentAffairsListViewAdapter listViewAdapter = new GovernmentAffairsListViewAdapter(getActivity());
        listViewAdapter.setListBeanMenus(arrayListBeans());
        listView1.setAdapter(listViewAdapter);
        listViewAdapter.notifyDataSetChanged();
        int H = getTotalHeightofListView(listView1);

        popupWindow.setContentView(contentView1);
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
        popupWindow.showAsDropDown(getActivity().findViewById(R.id.frg_ga_ibt_menu1),0,0);
    }


    private void initListView2(){
        final RelativeLayout rlAdpForLvGa;
        rlAdpForLvGa = (RelativeLayout) getActivity().findViewById(R.id.adp_for_lv_ga_rl);

        contentView1 = LayoutInflater.from(getActivity()).inflate(R.layout.listview_governmentaffairs,rlAdpForLvGa,false);
        listView1 = (ListView) contentView1.findViewById(R.id.lv_ga_listview);
        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                if (i == 0){
                    //导入联系人
                    intent.setClass(getActivity(),ImportContactsActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                    popupWindow.dismiss();
                }else if (i == 1){
                    //最近联系人
                    intent.setClass(getActivity(),RecentContactsActivity.class);
                    startActivity(intent);
                    popupWindow.dismiss();

                }else if (i == 2){
                    //即时通讯会话列表
                    intent.setClass(getActivity(), ConversationListActiviity.class);
                    startActivity(intent);
                    popupWindow.dismiss();
                }
            }
        });

    }
    /**
     * 往adapter中添加数据
     * 返回数组
     */
    public ArrayList<ListBeanMenu> arrayListBeans(){
        ArrayList<ListBeanMenu> listBeanMenus = new ArrayList<>();
        ListBeanMenu listBeanMenu = new ListBeanMenu();
        listBeanMenu.setListViewItem("导入");
        listBeanMenus.add(listBeanMenu);
        ListBeanMenu listBeanMenu1 = new ListBeanMenu();
        listBeanMenu1.setListViewItem("最近联系人");
        listBeanMenus.add(listBeanMenu1);
        ListBeanMenu listBeanMenu2 = new ListBeanMenu();
        listBeanMenu2.setListViewItem("会话列表");
        listBeanMenus.add(listBeanMenu2);
        return listBeanMenus;
    }

    /**
     * 取listview的高度
     */
    public static int getTotalHeightofListView(ListView listView){
        CalculateListViewTool calculateListViewTool = new CalculateListViewTool(listView);
        return calculateListViewTool.CalculateHeight()+45;
    }
}
