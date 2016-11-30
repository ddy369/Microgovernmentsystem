package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.ActivityCollector;

/**
 * 所有活动的基类
 * @author yy
 *
 */
public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //每创建一个活动，就加入到活动管理器中
        ActivityCollector.addActivity(this);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //每销毁一个活动，就从活动管理器中移除
        ActivityCollector.removeActivity(this);
    }
}
