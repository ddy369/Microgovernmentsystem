package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import app.mgs.com.microgovernmentsystem.R;


public class AboutActivity extends BaseActivity {

    public static AboutActivity activity = null;

    private TextView mtvVersionNum;
    private ImageButton mibtReturn;
    private TextView mtvContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle();

        setContentView(R.layout.activity_about);

        activity = this;

        initViews();

        setOnClickListener();

        setOnCreate();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent();
            intent.setClass(AboutActivity.this,MainFaceActivity.class);
            intent.putExtra("select",3);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initViews(){
        mtvVersionNum = (TextView) findViewById(R.id.act_about_tv_version_number);
        mibtReturn = (ImageButton) findViewById(R.id.act_about_ibt_return);
        mtvContent = (TextView) findViewById(R.id.act_about_tv_content);

        mtvContent.setText("本APP为洛阳高新区管委会" +
                "OA办公系统手机端，仅供内" +
                "部人员使用，请勿对外传送。" +
                "系统加强了对OA系统密码强度" +
                "的检测，增加了对手机的绑定，" +
                "使用中有如问题请与我们联系。\n\n" +
                "电话：64902657\n");
    }

    private void setOnClickListener(){
        mibtReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(AboutActivity.this,MainFaceActivity.class);
                intent.putExtra("select",3);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * 在创建act前设置字体大小
     */
    private void setStyle() {
        SharedPreferences sharedPreferences = getSharedPreferences("TextMode", 0);
        int TextMode = sharedPreferences.getInt("TextMode", 1);

        if (TextMode == 0) {
            this.setTheme(R.style.Theme_Small);
        } else if (TextMode == 1) {
            this.setTheme(R.style.Theme_Standard);
        } else if (TextMode == 2) {
            this.setTheme(R.style.Theme_Large);
        } else if (TextMode == 3) {
            this.setTheme(R.style.Theme_Larger);
        } else {
            this.setTheme(R.style.Theme_Largest);
        }
    }

    public void setOnCreate() {
        SharedPreferences sharedPreferences = getSharedPreferences("AboutActivityOnCreate", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putInt("AboutActivityOnCreate", 1);
        editor.apply();
    }
}
