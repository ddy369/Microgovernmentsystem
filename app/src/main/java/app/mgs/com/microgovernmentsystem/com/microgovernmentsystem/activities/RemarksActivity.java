package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import app.mgs.com.microgovernmentsystem.R;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.RemarkDataHelper;


public class RemarksActivity extends BaseActivity{
    public static RemarksActivity activity = null;
    RemarkDataHelper dataHelper = new RemarkDataHelper();

    private Button mbtDone;
    private ImageButton mibtReturn;
    private EditText medtRemark;
    private String flag = "";
    private int existence;
    //    private User user;
    //    private List<User> users;
    //    private Datahelper data;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remarks);
        Intent intent = getIntent();
        flag = intent.getStringExtra("flag");
        existence = intent.getIntExtra("existence",0);
        initview();
        setOnClickListener();
//        data=new Datahelper();
        activity = this;
    }

    private void initview() {
        mibtReturn = (ImageButton) findViewById(R.id.act_remarks_notice_ibt_return);
        mbtDone = (Button) findViewById(R.id.act_remarks_notice_bt_done);
        medtRemark = (EditText) findViewById(R.id.act_remarks_notice_edt);

        if (existence==1)
            medtRemark.setText(dataHelper.selectRemark(flag));
    }

    private void setOnClickListener(){
        mbtDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * 在此处仅需进行数据库的更改即可
                 */
                if (existence==1)
                    dataHelper.updateRemark(medtRemark.getText().toString(),flag);
                else
                    dataHelper.insertRemark(flag,medtRemark.getText().toString());
                RemarksActivity.activity.finish();
            }
        });

        mibtReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RemarksActivity.activity.finish();
            }
        });
    }


//    private void setOnClickListener() {
//        mbtDone.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(!mtvremark.getText().toString().isEmpty())
//                {
//                    fineuser();
//                    user.name=mtvremark.getText().toString();
//                    data.Saveusertodb(users);
//                    data.setremark(user);
//                    Intent intent = new Intent();
//                    intent.setClass(RemarksActivity.this,ContactsInformationActivity.class);
//                    intent.putExtra("user",user);
//                    startActivity(intent);
//                    finish();
//                }
//            }
//        });
//        mibtReturn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent();
//                intent.setClass(RemarksActivity.this,ContactsInformationActivity.class);
//                intent.putExtra("user",user);
//                startActivity(intent);
//                finish();
//            }
//        });
//
//    }
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK){
//            Intent intent = new Intent();
//            intent.setClass(RemarksActivity.this,ContactsInformationActivity.class);
//            intent.putExtra("user",user);
//            startActivity(intent);
//            finish();
//        }
//        return super.onKeyDown(keyCode, event);
//    }
//    private void fineuser()
//    {
//
//        users=data.getusers(new Datahelper.callback() {
//            @Override
//            public void callback(List<User> users) {
//
//            }
//        });
//        for(User u :users)
//        {
//            if(u.equals(user)){
//                user=u;
//                break;
//            }
//        }
//    }
}
