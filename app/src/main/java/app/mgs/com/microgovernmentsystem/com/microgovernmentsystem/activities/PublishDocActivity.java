package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import app.mgs.com.microgovernmentsystem.R;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.adapters.FileSelectAdapter;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.Dao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.GetUserInformationDao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.PublishNoticeDao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.operations.GetPublishNoticeChoiceListOperation;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.operations.PublishNoticeOperation;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Datahelper;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.FileHelper;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.PublishChoiceDataHelper;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Values;

/**
 * Created by Administrator on 2016-10-24.
 */
public class PublishDocActivity extends BaseActivity {
    private static final int REQUEST_CODE_SELECT_FILE = 12;
    public static PublishDocActivity activity = null;
    Context mcontext = this;
    private TextView mtvDepartment;
    private TextView mtvReceiver;
    private TextView mtvSender;
    private TextView mtvMessage;
    private TextView mtvReceipt;
    private EditText medtTheme;
    private EditText medtText;
    private ImageButton mibtReturn;
    private TextView mtvAddAttachment;
    private RelativeLayout mrlMessage;
    private RelativeLayout mrlReceipt;
    private RelativeLayout mrlDepartment;
    private RelativeLayout mrlReceiver;
    private Button mbtSend;
    private List<File> filelist = new ArrayList<>();
    private List<String> filepathlist=new ArrayList<>();
    private String attachmenturls="";
    private ListView mListView;
    private FileSelectAdapter adapter;

    private TextView mainTitle;
    private TextView publish_content;
    private RelativeLayout relativeLayout;
    private String docId = "00000000";
    private TextView reMark;
    private ProgressDialog dialog;
    private int needMessage = 0;
    private int needReceipt = 0;
    private String mRecordId = "null";
    private int kindOfPublish = 2;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 2:
                    mainTitle.setText("公文分发");
                    reMark.setText("备注");
                    relativeLayout.setVisibility(View.INVISIBLE);
                    docId = getIntent().getStringExtra("docId");
                    PublishNoticeOperation operation = new PublishNoticeOperation(mcontext);
                    String needSendnote = "false";
                    String needReturn = "false";
                    if (mtvMessage.getText().toString().equals("发送"))
                        needSendnote = "true";
                    if (mtvReceipt.getText().toString().equals("需要"))
                        needReturn = "true";

                    Log.i("aaaa",String.valueOf(kindOfPublish));
                    operation.publishNotice(medtTheme.getText().toString(),
                            medtText.getText().toString(),
                            needSendnote, needReturn, String.valueOf(kindOfPublish), docId,attachmenturls,mRecordId);
                    //2.修改数据库将所有选中字段置0
                    PublishNoticeDao dao = new PublishNoticeDao(mcontext);
                    dao.resetChoice();
                    //跳转页面
                    finishActivity();
                    break;

                default:
                    break;
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle();

        setContentView(R.layout.activity_publish_notice);

        initView();

        initActivity();//若为转发通知，则对界面上的部分内容进行赋值

        getInformation();//联网获取部门，通知到人列表内容，并存入本地数据库

        setOnCreate();//判断页面是否已经被create

        activity = this;
        set_mListView_adapter();
        setOnClickListener();

    }

    @Override
    protected void onStart() {
        super.onStart();
        setDepartmentAndReceiver();//设置通知到部门和通知到人的Text
        changeView();
    }

    private void changeView() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = getIntentValues();
                handler.sendMessage(message);
            }
        }).start();
    }

    /**
     * 设置ListView的Adapter
     */
    private void set_mListView_adapter() {

        FileSelectAdapter.callback a = new FileSelectAdapter.callback() {
            @Override
            public void callback(int i) {
                filelist.remove(i);
                adapter.notifyDataSetChanged();
            }
        };
        adapter = new FileSelectAdapter(this, R.layout.activity_add_attachment, filelist, a);

        mListView.setAdapter(adapter);
    }

    private void initView() {
        mtvDepartment = (TextView) findViewById(R.id.act_publish_notice_tv_department);
        mtvReceiver = (TextView) findViewById(R.id.act_publish_notice_tv_receiver);
        mtvSender = (TextView) findViewById(R.id.act_publish_notice_tv_sender);
        medtTheme = (EditText) findViewById(R.id.act_publish_notice_edt_theme);
//        medtText = (EditText) findViewById(R.id.act_publish_notice_edt_text);
        mibtReturn = (ImageButton) findViewById(R.id.act_publish_notice_ibt_return);
        mtvAddAttachment = (TextView) findViewById(R.id.act_publish_notice_tv_label_add_attachment);
        mtvMessage = (TextView) findViewById(R.id.act_publish_notice_tv_message);
        mtvReceipt = (TextView) findViewById(R.id.act_publish_notice_tv_receipt);
        mrlMessage = (RelativeLayout) findViewById(R.id.act_publish_notice_rl_message);
        mtvMessage.setText("不发送");
        needMessage = 0;
        mrlReceipt = (RelativeLayout) findViewById(R.id.act_publish_notice_rl_receipt);
        mtvReceipt.setText("不需要");
        needReceipt = 1;
        mrlReceiver = (RelativeLayout) findViewById(R.id.act_publish_notice_rl_receiver);
        mrlDepartment = (RelativeLayout) findViewById(R.id.act_publish_notice_rl_department);
        mbtSend = (Button) findViewById(R.id.act_publish_notice_bt_send);
        mListView = (ListView) findViewById(R.id.act_add_attachment_list);

        mainTitle = (TextView) findViewById(R.id.main_title);
        publish_content = (TextView) findViewById(R.id.act_publish_notice_tv_text);
        relativeLayout = (RelativeLayout) findViewById(R.id.act_publish_notice_rl_Text);
        reMark = (TextView) findViewById(R.id.act_publish_notice_tv_label_theme);
        GetUserInformationDao dao = new GetUserInformationDao();
        mtvSender.setText(dao.getUserName());

    }

    private void initActivity() {
        //若为转发通知或复制通知，则对界面上的标题进行赋值
        Intent intent = getIntent();
        String mtvTitle = intent.getStringExtra("mtvTitle");
        String mtvContent = intent.getStringExtra("mtvContent");
        String url=intent.getStringExtra("attachmenturls");
        String recordId = intent.getStringExtra("recordid");
        if (recordId!=null){
            mRecordId = recordId;
        }
        if (mtvTitle != null && mtvContent != null) {
            medtTheme.setText(mtvTitle);
            medtText.setText(mtvContent);
        }
        if(url!=null)
            attachmenturls=url;

    }

    private void setOnClickListener() {
        final Intent intent = new Intent();
        mtvAddAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);

                try {
                    startActivityForResult(Intent.createChooser(intent, "选择文件发送"), REQUEST_CODE_SELECT_FILE);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(PublishDocActivity.this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mrlMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
                TextView tv = new TextView(mcontext);
                tv.setText("是否需要短信提醒");    //内容
                tv.setTextSize(22);//字体大小
                tv.setPadding(80, 50, 10, 10);
                tv.setTextColor(0xFF0984EC);
                builder.setCustomTitle(tv).setSingleChoiceItems(new String[]{"发送", "不发送"}, needMessage, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mtvMessage.setText("");
                        if (i == 0) {
                            mtvMessage.setText("发送");
                            needMessage = 0;
                        } else {
                            mtvMessage.setText("不发送");
                            needMessage = 1;
                        }
                        dialogInterface.dismiss();
                    }
                }).show();
            }
        });

        mrlReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
                TextView tv = new TextView(mcontext);
                tv.setText("是否需要回执");    //内容
                tv.setTextSize(22);//字体大小
                tv.setPadding(80, 50, 10, 10);
                tv.setTextColor(0xFF0984EC);
                builder.setCustomTitle(tv).setSingleChoiceItems(new String[]{"需要", "不需要"}, needReceipt, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mtvReceipt.setText("");
                        if (i == 0) {
                            mtvReceipt.setText("需要");
                            needReceipt = 0;
                        } else {
                            mtvReceipt.setText("不需要");
                            needReceipt = 1;
                        }
                        dialogInterface.dismiss();
                    }
                }).show();
            }
        });

        mibtReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishActivity();
            }
        });

        mrlDepartment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转到列表页面并从数据库获取部门列表
                if (findData()!=0){
                    intent.setClass(PublishDocActivity.this, PublishNoticeChoiceDepartmentActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(mcontext,"正在加载列表，请稍候",Toast.LENGTH_SHORT).show();
                }
            }
        });

        mrlReceiver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转到列表页面并从数据库获取接收人列表
                if (findData()!=0){
                    intent.setClass(PublishDocActivity.this, PublishNoticeChoiceReceiverActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(mcontext,"正在加载列表，请稍候",Toast.LENGTH_SHORT).show();
                }
            }
        });

        mbtSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * 首先对数据的填写情况进行过滤，如果没有完整的填写，则无法进行提交操作
                 */
                String department = mtvDepartment.getText().toString();
                String receiver = mtvReceiver.getText().toString();
                String message = mtvMessage.getText().toString();
                String receipt = mtvReceipt.getText().toString();
                String title = medtTheme.getText().toString();
                if ((department.isEmpty() && receiver.isEmpty()) || message.isEmpty() || receipt.isEmpty() ) {
                    Toast.makeText(mcontext, "请检查信息是否填写完整", Toast.LENGTH_SHORT).show();
                } else {
                    /**
                     * 此处分为两个操作：1.联网提交通知内容  2.将数据库里的选中信息全部置为0，并跳转页面
                     */
                    //上传附件并获得id
                        Message message1 = new Message();
                        message1.what = 2;
                        handler.sendMessage(message1);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_SELECT_FILE: //send the file
                    if (data != null) {
                        Uri uri = data.getData();
                        if (uri != null) {
                            Log.d("file", uri.getPath());
                            if(FileHelper.getFileByUri(uri, mcontext)!=null)
                            {
                                File tem = new File(FileHelper.getFileByUri(uri, mcontext));
                                if(tem.exists()) {
                                    filepathlist.add(FileHelper.getFileByUri(uri, mcontext));
                                    Log.d("file", "getFileByUri:" + tem.getPath() + tem.getName());
                                    if (!filelist.contains(tem))
                                        filelist.add(tem);
                                    else
                                        Toast.makeText(PublishDocActivity.this, "此文件已在附件列表中", Toast.LENGTH_SHORT).show();
                                    adapter.notifyDataSetChanged();
                                }else
                                {
                                    Datahelper.showToast("该文件未找到！");
                                }
                            }else{
                                Datahelper.showToast("打开文件失败！");
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        }

    }

    /**
     * 用来设置通知到部门和通知到人的内容
     */
    private void setDepartmentAndReceiver() {
        PublishChoiceDataHelper helper = new PublishChoiceDataHelper();
        mtvDepartment.setText(helper.ReadDepartment());
        mtvReceiver.setText(helper.ReadReceiver());
    }

    /**
     * 此方法用来联网获取通知部门，通知人列表，并存入数据库
     */
    private void getInformation() {
        GetPublishNoticeChoiceListOperation operation = new GetPublishNoticeChoiceListOperation(mcontext,"0");
        operation.setPublishNoticeChoiceList(new GetPublishNoticeChoiceListOperation.TaskWork() {
            @Override
            public void onPostWork() {
                //TODO:一个预留接口(如果没有使用到后面可以去掉)
            }
        });
    }

    /**
     * 跳转前先判断是否数据表中有数据
     * @return 0代表没数据，1代表有数据
     */
    private int findData(){
        int flag;
        Dao dao = new Dao(this, null, Values.database_version);
        SQLiteDatabase db = dao.getReadableDatabase();
        Cursor cursor = db.query("DepartmentTable",null,null,null,null,null,null);
        flag = cursor.getCount();
        cursor.close();
        return flag;
    }

    /**
     * 获取从其他页面传过来的值，0代表发送通知，1代表转发通知，2代表分发公文
     *
     * @return 返回参数值
     */
    private int getIntentValues() {
        Intent intent = getIntent();
        kindOfPublish = intent.getIntExtra("kindofpublish", 2);
        return kindOfPublish;
    }

    public void setOnCreate() {
        SharedPreferences sharedPreferences = getSharedPreferences("PublishNoticeActivityOnCreate", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putInt("PublishNoticeActivityOnCreate", 1);
        editor.apply();
    }

    private void finishActivity() {
        SharedPreferences sharedPreferences = getSharedPreferences("PublishNoticeActivityOnCreate", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();//获取编辑器
        editor.putInt("PublishNoticeActivityOnCreate", 0);
        editor.apply();
        finish();
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finishActivity();
        }
        return super.onKeyDown(keyCode, event);
    }
}
