package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import app.mgs.com.microgovernmentsystem.R;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.operations.FeedBackOpreation;

/**
 * Created by Administrator on 2016/9/7.
 */
public class FeedbackActivity extends BaseActivity {
    public static FeedbackActivity activity = null;

    Context mcontext = this;

    private ImageButton mibtreturn;
    private Button mbtSend;
    private EditText medtText;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle();
        setContentView(R.layout.activity_feedback);
        initview();
        setOnclickListener();
        activity = this;
        setOnCreate();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent();
            intent.setClass(FeedbackActivity.this,MainFaceActivity.class);
            intent.putExtra("select",3);
            startActivity(intent);
            finishActivity();
        }
        return super.onKeyDown(keyCode, event);
    }
    private void initview(){
        mibtreturn = (ImageButton) findViewById(R.id.act_feedback_notice_ibt_return);
        mbtSend = (Button) findViewById(R.id.act_feedback_notice_bt_send);
        medtText = (EditText) findViewById(R.id.act_remarks_notice_edt);
    }
    private void setOnclickListener(){
        mibtreturn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(FeedbackActivity.this,MainFaceActivity.class);
                intent.putExtra("select",3);
                startActivity(intent);
                finishActivity();
            }
        });

        mbtSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FeedBackOpreation opreation = new FeedBackOpreation(mcontext,medtText.getText().toString());
                opreation.feedBack();
                Intent intent = new Intent();
                intent.setClass(FeedbackActivity.this,MainFaceActivity.class);
                intent.putExtra("select",3);
                startActivity(intent);
                finishActivity();
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

    public void setOnCreate(){
        SharedPreferences sharedPreferences = getSharedPreferences("FeedBackActivityOnCreate", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putInt("FeedBackActivityOnCreate",1);
        editor.apply();
    }

    private void finishActivity(){
        SharedPreferences sharedPreferences = getSharedPreferences("FeedBackActivityOnCreate", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putInt("FeedBackActivityOnCreate",0);
        editor.apply();
        finish();
    }
}
