package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;

import app.mgs.com.microgovernmentsystem.R;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Values;

public class ReadDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_detail);
        setStyle();
        setOnCreate();

        WebView webView = (WebView) findViewById(R.id.readDetail);
        ImageButton imageButton = (ImageButton) findViewById(R.id.read_return);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishActivity();
            }
        });
        webView.loadUrl(Values.ServerAddress+"/Manager/InformationDetail?RecordID="+getIntentValues()+"");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
//            Intent intent = new Intent();
//            intent.setClass(DocumentActivity.this,DocumentListActivity.class);
//            startActivity(intent);
            finishActivity();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void setOnCreate(){
        SharedPreferences sharedPreferences = getSharedPreferences("ReadDetailActivityOnCreate", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putInt("ReadDetailActivityOnCreate",1);
        editor.apply();
    }

    private void finishActivity(){
        SharedPreferences sharedPreferences = getSharedPreferences("ReadDetailActivityOnCreate", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putInt("ReadDetailActivityOnCreate",0);
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
        }else {
            this.setTheme(R.style.Theme_Larger);
        }
    }
    private String getIntentValues(){
        Intent intent = getIntent();
        return intent.getStringExtra("id");
    }
}
