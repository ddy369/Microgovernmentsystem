package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import app.mgs.com.microgovernmentsystem.R;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.adapters.PublishNoticeChoiceReceiverAdapter;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.Dao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.PublishNoticeChoiceReceiverCheckDao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.ListBeanPNCReceiver;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.PublishNoticeChoiceReceiverDataHelper;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Values;


public class PublishNoticeChoiceReceiverActivity extends BaseActivity
        implements PublishNoticeChoiceReceiverAdapter.Callback{
    Context mcontext = this;

    public static PublishNoticeChoiceReceiverActivity activity= null;

    PublishNoticeChoiceReceiverAdapter adapter = new PublishNoticeChoiceReceiverAdapter(mcontext,this);

    private List<String> group = null;
    private Map<String, ArrayList<ListBeanPNCReceiver>> map = null;

    private ExpandableListView melvName;
    private Button mbtDone;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle();//先设置主题

        setContentView(R.layout.activity_publish_notice_choice_receiver);

        initViews();

        setOnClickListener();

        activity = this;

        setOnCreate();

        initList();//初始化列表
    }

    private void initViews(){
        mbtDone = (Button) findViewById(R.id.act_publish_notice_choice_receiver_bt_done);
        melvName = (ExpandableListView) findViewById(R.id.act_publish_notice_choice_receiver_elv);
    }

    private void setOnClickListener(){
        mbtDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishActivity();
            }
        });
    }

    private void initList(){
        adapter.setListAndMap(setGroup(),setMap());
        melvName.setAdapter(adapter);
        melvName.setGroupIndicator(null);
        melvName.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                ArrayList<ListBeanPNCReceiver> list = map.get(group.get(i));
                String id = list.get(i1).getId();
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.adp_for_expandable_publish_notice_receiver_child_checkbox);
                PublishNoticeChoiceReceiverCheckDao pncrDao = new PublishNoticeChoiceReceiverCheckDao(mcontext);
                if (checkBox.isChecked()){
                    checkBox.setChecked(false);
                    pncrDao.setChecked(0,id);
                    list.get(i1).setChoice(0);
                    //遍历数据库表，如果同一部门下的所有人都被选中，则部门置为被选中
                }else {
                    checkBox.setChecked(true);
                    pncrDao.setChecked(1,id);
                    list.get(i1).setChoice(1);
                }
                melvName.collapseGroup(i);
                melvName.expandGroup(i);
                return true;
            }
        });

        /**
         * 将选中联系人的组控件设置为默认展开
         */
        //查询数据库找出被选中的人所在的部门
        Dao dao = new Dao(this,null, Values.database_version);
        SQLiteDatabase dbRead = dao.getReadableDatabase();
        Cursor cursor = dbRead.query(true,"ReceiverTable",null,"isChoice=?",new String[]{"1"},null,null,null,null);
        if (cursor.moveToFirst()){
            for (int i = 0; i < cursor.getCount(); i++){
                String rid = cursor.getString(cursor.getColumnIndex("departmentId"));
                Cursor cursor1 = dbRead.query(true,"DepartmentTable",null,"departmentId=?",new String[]{rid},null,null,null,null);
                String rn = null;
                if (cursor1.moveToNext())
                    rn = cursor1.getString(cursor1.getColumnIndex("departmentName"));
                cursor1.close();
                for (int j = 0; j < group.size(); j++){
                    if (group.get(j).equals(rn)){
                        melvName.expandGroup(j);
                    }
                }
                cursor.moveToNext();
            }
        }
        cursor.close();
    }

    public void click(View v , int i){
        PublishNoticeChoiceReceiverDataHelper helper = new PublishNoticeChoiceReceiverDataHelper(mcontext);
        ArrayList<ListBeanPNCReceiver> list = map.get(group.get(i));
        CheckBox checkBox = (CheckBox) v.findViewById(R.id.adp_for_expandable_publish_notice_receiver_parent_checkbox);
        if (checkBox.isChecked()){
            helper.selectGroup(group.get(i),1);
            for (int count = 0; count < list.size(); count++)
                list.get(count).setChoice(1);
        }else {
            helper.selectGroup(group.get(i),0);
            for (int count = 0; count < list.size(); count++)
                list.get(count).setChoice(0);
        }
        melvName.collapseGroup(i);
        melvName.expandGroup(i);
    }

    public List<String> setGroup(){
        PublishNoticeChoiceReceiverDataHelper dataHelper = new PublishNoticeChoiceReceiverDataHelper(mcontext);
        group = dataHelper.setParentData();
        return group;
    }

    public Map<String, ArrayList<ListBeanPNCReceiver>> setMap(){
        PublishNoticeChoiceReceiverDataHelper dataHelper = new PublishNoticeChoiceReceiverDataHelper(mcontext);
        map = dataHelper.setChildData();
        return map;
    }

    /**
     * 在创建act前设置字体大小
     */
    void setStyle(){
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
        SharedPreferences sharedPreferences = getSharedPreferences("PublishNoticeChoiceReceiverActivityOnCreate", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putInt("PublishNoticeChoiceReceiverActivityOnCreate",1);
        editor.apply();
    }

    private void finishActivity(){
        SharedPreferences sharedPreferences = getSharedPreferences("PublishNoticeChoiceReceiverActivityOnCreate", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putInt("PublishNoticeChoiceReceiverActivityOnCreate",0);
        editor.apply();
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            finishActivity();
        }
        return super.onKeyDown(keyCode, event);
    }

}
