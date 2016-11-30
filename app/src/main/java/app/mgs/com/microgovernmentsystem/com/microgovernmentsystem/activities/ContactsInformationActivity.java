package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.telephony.PhoneNumberUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import app.mgs.com.microgovernmentsystem.R;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.im.ChatActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.Contacts;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Datahelper;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Md5Tool;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.RemarkDataHelper;


public class ContactsInformationActivity extends BaseActivity  {

    public static ContactsInformationActivity activity = null;

    Context mContext = this;

    private ImageButton mibtReturn;
    private ImageButton mbtsendmsg;
    private ImageButton mbtcallmobile_phone;
    private ImageButton mbtcalltel;
    private TextView mtvName;
    private TextView mtvDepartment;
    private TextView mtvPosition;
    private TextView mtvMobilePhone;
    private TextView mtvPhone;
    private TextView mtvRemark;
    private RelativeLayout mrlMobilePhone;
    private RelativeLayout mrlPhone;
    private RelativeLayout mrlRemarks;
    private Button mbtImport;
    private Button mbtInstantMessaging;
    private Contacts contacts;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle();

        setContentView(R.layout.activity_contacts_information);
        Intent intent = getIntent();
        contacts = intent.getParcelableExtra("contacts");
        Datahelper data=new Datahelper();
        data.setRecentContacts(contacts);
        initViews();
        initdata();
        setOnClickListener();
        activity = this;
        setOnCreate();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //在拿到数据的同时进行一波姓名+部门+职位的md5加密
        Md5Tool tool = new Md5Tool();
        final String flag = tool.getMDSStr(contacts.name+contacts.department+contacts.position);
        //首先查找数据库看看备注是否有内容，若有则取出来赋值，没有则显示未添加备注
        final RemarkDataHelper dataHelper = new RemarkDataHelper();
        final int existence = dataHelper.selectExis(flag);
        final String remark = dataHelper.selectRemark(flag);
        if (!remark.isEmpty())
            mtvRemark.setText(remark);
        else
            mtvRemark.setText("未添加备注");

        mrlRemarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(ContactsInformationActivity.this, RemarksActivity.class);
                intent.putExtra("flag", flag);
                if (existence==0)
                    intent.putExtra("existence",0);//0状态代表之前没有备注，需插入数据库
                else
                    intent.putExtra("existence",1);//0状态代表已经存在备注或存在空备注，需更新数据库
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //  initdata();
    }
    private void initdata() {
        mtvName.setText(contacts.name);
        mtvDepartment.setText(contacts.department);
        mtvPosition.setText(contacts.position);

        if (!contacts.tel.isEmpty()) {
            mtvPhone.setText(contacts.tel);
            mrlPhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callnum(contacts.tel);
                }
            });
        } else {
            mtvPhone.setText("暂无");
        }
        if (!contacts.mobile_phone.isEmpty()) {
            mtvMobilePhone.setText(contacts.mobile_phone);
            mrlMobilePhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ContactsInformationActivity.this);
                    TextView tv = new TextView(mContext);
                    tv.setText("请选择联系方式");	//内容
                    tv.setTextSize(22);//字体大小
                    tv.setPadding(80,50,10,10);
                    tv.setTextColor(0xFF0984EC);
                    builder.setCustomTitle(tv);
//                    builder.setIcon(R.drawable.ic_launcher);
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                   /* List<String> tem=new ArrayList<String>();
                    tem.add("拨打电话");
                    tem.add("发送消息");
                    DialogAdapter adapter=new DialogAdapter(ContactsInformationActivity.this,R.layout.adapter_for_dialogitem_list,tem);*/
                    builder.setSingleChoiceItems(new String[]{"拨打电话","发送消息"}, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            dialog.dismiss();
                            switch (which)
                            {
                                case 0:
                                    callnum(contacts.mobile_phone);
                                    break;
                                case 1:
                                    if (PhoneNumberUtils.isGlobalPhoneNumber(contacts.mobile_phone)) {
                                        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + contacts.mobile_phone));
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(ContactsInformationActivity.this, contacts.mobile_phone + "不是一个手机号", Toast.LENGTH_LONG).show();
                                    }
                                    break;
                            }
                        }
                    });

//                    builder.setNegativeButton("Cancel",null);
                    final AlertDialog dialog = builder.create();
                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        private Button negativeBtn ;
                        private Button positiveBtn;
                        @Override
                        public void onShow(DialogInterface dialogInterface) {
                            positiveBtn = (dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                            negativeBtn = (dialog).getButton(DialogInterface.BUTTON_NEGATIVE);
                            negativeBtn.setTextSize(18);
                            positiveBtn.setTextSize(18);
                            positiveBtn.setTextColor(0xFF0984EC);
                            negativeBtn.setTextColor(0xFF0984EC);
                        }
                    });
                    dialog.show();
                }
            });
        } else {
            mtvMobilePhone.setText("暂无");
        }
//        if(user.type.equals("0")) {
//            mtvRemark.setText("公有联系人不能备注");
//            mtvRemark.setGravity(Gravity.CENTER);
//        }
//        else{
//            mrlRemarks.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {

//                }
//            });
//        }
        if(!contacts.emid.isEmpty())
        {
            mbtInstantMessaging.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setClass(ContactsInformationActivity.this, ChatActivity.class);
                    intent.putExtra("userId", contacts.emid);
                    startActivity(intent);

                }
            });
        }else {
            mbtInstantMessaging.setText("此联系人无法即时通讯");
            mbtInstantMessaging.setGravity(Gravity.CENTER);
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
           /* Intent intent = new Intent();
            intent.setClass(ContactsInformationActivity.this, MainFaceActivity.class);
            intent.putExtra("select", 1);
            startActivity(intent);*/
            finishActivity();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initViews() {
        mibtReturn = (ImageButton) findViewById(R.id.act_contacts_information_ibt_return);
        mbtImport = (Button) findViewById(R.id.act_contacts_information_bt_import);
        mbtInstantMessaging = (Button) findViewById(R.id.act_contacts_information_bt_instant_messaging);
        mtvDepartment = (TextView) findViewById(R.id.act_contacts_information_tv_department);
        mtvRemark=(TextView) findViewById(R.id.act_contacts_information_tv_label_remarks);
        mtvMobilePhone = (TextView) findViewById(R.id.act_contacts_information_tv_mobile_phone);
        mtvName = (TextView) findViewById(R.id.act_contacts_information_tv_name);
        mtvPhone = (TextView) findViewById(R.id.act_contacts_information_tv_phone);
        mtvPosition = (TextView) findViewById(R.id.act_contacts_information_tv_position);
        mrlMobilePhone = (RelativeLayout) findViewById(R.id.act_contacts_information_rl_mobile_phone);
        mrlPhone = (RelativeLayout) findViewById(R.id.act_contacts_information_rl_phone);
        mrlRemarks = (RelativeLayout) findViewById(R.id.act_contacts_information_rl_remarks);
        mbtsendmsg = (ImageButton) mrlMobilePhone.findViewById(R.id.act_contacts_information_ibt_message);
        mbtcallmobile_phone = (ImageButton) mrlMobilePhone.findViewById(R.id.act_contacts_information_ibt_mobile_phone);
        mbtcalltel = (ImageButton) mrlPhone.findViewById(R.id.act_contacts_information_ibt_phone);
    }

    private void setOnClickListener() {
      /*  mbtcallmobile_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callnum(user.mobile_phone);
            }
        });
        mbtcalltel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callnum(user.tel);
            }
        });
        mbtsendmsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (PhoneNumberUtils.isGlobalPhoneNumber(user.tel)) {
                    Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + user.tel));
                    startActivity(intent);
                } else {
                    Toast.makeText(ContactsInformationActivity.this, user.tel + "不是一个手机号", Toast.LENGTH_LONG).show();
                }
            }
        });*/


        /*mrlRemarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(ContactsInformationActivity.this, RemarksActivity.class);
                intent.putExtra("user", user);

                startActivity(intent);
            }
        });*/


        mibtReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*  Intent intent = new Intent();
                intent.setClass(ContactsInformationActivity.this, MainFaceActivity.class);
                intent.putExtra("select", 1);
                startActivity(intent);*/
                finishActivity();
            }
        });
        mbtImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contacts.toMail_list(ContactsInformationActivity.this);
            }
        });

       /* mbtInstantMessaging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            //    if (checkNetworkState.checkNetworkState() != 0) {
                    Intent intent = new Intent();
                    intent.setClass(ContactsInformationActivity.this, ChatActivity.class);
                    intent.putExtra("userId", user.emid);
                    startActivity(intent);
             //   }
             //   else
            //    {
              //      Toast.makeText(ContactsInformationActivity.this, "网络错误，请检查是否联网", Toast.LENGTH_SHORT).show();
              //  }

                // finish();
            }
        });*/


    }

    private void callnum(String num) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + num));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

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
        SharedPreferences sharedPreferences = getSharedPreferences("ContactsInformationActivityOnCreate", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putInt("ContactsInformationActivityOnCreate",1);
        editor.apply();
    }

    private void finishActivity(){
        SharedPreferences sharedPreferences = getSharedPreferences("ContactsInformationActivityOnCreate", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putInt("ContactsInformationActivityOnCreate",0);
        editor.apply();
        finish();
    }

}
