package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.List;

import app.mgs.com.microgovernmentsystem.R;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.adapters.ImportContactsAdapter;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.Contacts;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Datahelper;


public class ImportContactsActivity extends BaseActivity {

    public static ImportContactsActivity activity = null;

    private ExpandableListView melvContacts;
    private Button mbtCancel;
    private Button mbtImport;
    private Button mbtselectall;
    private List<Contacts> contactses;
    private ImportContactsAdapter adapter;
    private Datahelper datahelper;
    private boolean checkall;
    Context mContext = this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle();//先设置主题

        setContentView(R.layout.activity_import_contacts);

        initViews();
        initdata();
        setOnClickListener();

        setOnCreate();

        setExpandaListView();
    }
    private void initdata()
    {
        contactses =new Datahelper().getusers(new Datahelper.callback() {
            @Override
            public void callback(List<Contacts> contactses) {

            }
        });
        checkall=true;
    }
    private void initViews(){

        melvContacts = (ExpandableListView) findViewById(R.id.act_import_contacts_list_elv);
        mbtCancel = (Button) findViewById(R.id.act_import_contacts_bt_cancel);
        mbtImport = (Button) findViewById(R.id.act_import_contacts_bt_import);
        mbtselectall=(Button)findViewById(R.id.act_import_contacts_bt_all);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent();
            intent.setClass(ImportContactsActivity.this,MainFaceActivity.class);
            intent.putExtra("select",1);
            startActivity(intent);
            finishActivity();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setOnClickListener(){
        final Intent intent = new Intent();

        mbtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.setClass(ImportContactsActivity.this,MainFaceActivity.class);
                intent.putExtra("select",1);
                startActivity(intent);
                finishActivity();
            }
        });
        mbtselectall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                adapter.setallchecked(checkall);
                checkall=checkall?false:true;
            }
        });
        mbtImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.tool_dialog_done,
                        (ViewGroup) findViewById(R.id.tool_dialog_done_dialog));
                TextView message = (TextView) layout.findViewById(R.id.tool_dialog_done_tv);
                List <Contacts>  tem= adapter.getchecked();
                if(tem.isEmpty())
                    message.setText("没有选中联系人");
                else{
                    for(Contacts contacts :tem)
                    {
                        contacts.flag=false;
                        contacts.toMail_list(mContext);
                        contacts.flag=true;
                    }
                }

                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setView(layout);
//              builder.setCancelable(false);  //TODO:在联网的时候让对话框无法消失，直到达到跳转条件时使用dismiss
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    public void setExpandaListView(){
        //创建一个BaseExpandableListAdapter对象
        adapter= new ImportContactsAdapter(contactses,mContext);
        melvContacts.setAdapter(adapter);
        melvContacts.setGroupIndicator(null);
        melvContacts.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {

            /*    Toast.makeText(mContext, "你单击了："
                        +adapter.getChild(i, i1), Toast.LENGTH_LONG).show();*/
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.adp_for_expandable_import_contacts_child_checkbox);
                checkBox.setChecked(!checkBox.isChecked());

                return true;
            }
        });
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
        SharedPreferences sharedPreferences = getSharedPreferences("ImportContactsActivityOnCreate", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putInt("ImportContactsActivityOnCreate",1);
        editor.apply();
    }

    private void finishActivity(){
        SharedPreferences sharedPreferences = getSharedPreferences("ImportContactsActivityOnCreate", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putInt("ImportContactsActivityOnCreate",0);
        editor.apply();
        finish();
    }
}
