package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

import java.util.ArrayList;

import app.mgs.com.microgovernmentsystem.R;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.adapters.PublishNoticeChoiceDepartmentAdapter;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.ListBeanPNLDepartment;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.PublishNoticeChoiceDepartmentDataHelper;


public class PublishNoticeChoiceDepartmentActivity extends BaseActivity{

    public static PublishNoticeChoiceDepartmentActivity activity = null;

    private ListView mlvChoiceList;
    private Button mbtDone;

    private ArrayList<ListBeanPNLDepartment> mListBean;

//    public HashMap<String,String> map = new HashMap<>();

    Context mcontext = this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle();

        setContentView(R.layout.activity_publish_notice_choice_department);

        initViews();

        setOnClickListener();

        activity = this;

        setOnCreate();//判断页面是否已经被create

        mListBean = arrayListBeans();

        initList();//初始化列表
    }

    private void initViews(){
        mlvChoiceList = (ListView) findViewById(R.id.act_publish_notice_choice_department_lv);
        mbtDone = (Button) findViewById(R.id.act_publish_notice_choice_department_bt_done);
    }

    private void setOnClickListener(){
        mbtDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent();
//                SerMap serMap = new SerMap();
//                serMap.setMap(map);
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("map",serMap);
//                intent.putExtras(bundle);
//                PublishNoticeChoiceDepartmentActivity.this.setResult(0,intent);
                finishActivity();
            }
        });
    }

    private void initList(){
        PublishNoticeChoiceDepartmentAdapter adapter = new PublishNoticeChoiceDepartmentAdapter(mcontext);
        adapter.setListBeans(mListBean);
        mlvChoiceList.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        mlvChoiceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String id = mListBean.get(i).getId();
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.adp_for_lv_publish_notice_choice_department_cb);
                PublishNoticeChoiceDepartmentDataHelper helper = new PublishNoticeChoiceDepartmentDataHelper();
                if (checkBox.isChecked()){
                    checkBox.setChecked(false);
                    helper.setChecked(0,id);
                }else {
                    checkBox.setChecked(true);
                    helper.setChecked(1,id);
                }
            }
        });
    }

    /*
    向列表中填充数据
     */
    public ArrayList<ListBeanPNLDepartment> arrayListBeans(){
        PublishNoticeChoiceDepartmentDataHelper datahelper = new PublishNoticeChoiceDepartmentDataHelper();
        return datahelper.setListBeans();
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
        SharedPreferences sharedPreferences = getSharedPreferences("PublishNoticeChoiceDepartmentActivityOnCreate", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putInt("PublishNoticeChoiceDepartmentActivityOnCreate",1);
        editor.apply();
    }

    private void finishActivity(){
        SharedPreferences sharedPreferences = getSharedPreferences("PublishNoticeChoiceDepartmentActivityOnCreate", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putInt("PublishNoticeChoiceDepartmentActivityOnCreate",0);
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
