package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import app.mgs.com.microgovernmentsystem.R;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.adapters.MaiListserachAdapter;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.Contacts;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Datahelper;

/**
 * Created by Administrator on 2016/10/9.
 */
public class RecentContactsActivity extends BaseActivity{
    ListView mListView;
   // ArrayList<Contacts> mData = new ArrayList<>();
    List<Contacts> contactses;
    MaiListserachAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle();
//
        setContentView(R.layout.activity_recent_contacts);
        getdata();
        setlistview();
    }
    private void getdata()
    {
        Datahelper data=new Datahelper();
        contactses=data.getRecentContacts();
    }
    private void setlistview()
    {
        mListView = (ListView)findViewById(R.id.SerachListView);


        adapter = new MaiListserachAdapter(RecentContactsActivity.this,R.layout.adapter_for_listview_serach_list,contactses);

        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Contacts contacts = contactses.get(position);
                Intent intent = new Intent();
                intent.setClass(RecentContactsActivity.this,ContactsInformationActivity.class);
                intent.putExtra("contacts", contacts);

                startActivity(intent);
                //  getActivity().finish();
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
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            finishActivity();
        }
        return super.onKeyDown(keyCode, event);
    }
    private void finishActivity(){
        SharedPreferences sharedPreferences = getSharedPreferences("RecentContactsActivity", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putInt("RecentContactsActivityOnCreate",0);
        editor.apply();
        finish();
    }
    @Override
    protected void onResume() {
        super.onResume();
        getdata();
        adapter.notifyDataSetChanged();

    }
}
