package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.operations;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.GetUserInformationDao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.PublishNoticeDao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tasks.PublishNoticeWorkerTask;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.GetSignTool;


public class PublishNoticeOperation {

    private Context mcontext;

    public PublishNoticeOperation(Context context){
        this.mcontext = context;
    }

    //接口
    public interface TaskWork{
        void onPostWork();
    }

    private String mtype;
    private String mdocId;

    //数据库的初始化
    GetUserInformationDao getUserInformationDao = new GetUserInformationDao();
    PublishNoticeDao publishNoticeDao = new PublishNoticeDao(mcontext);

    public void publishNotice(String subject,
                              String content,
                              String needSendnote,
                              String needReturn,
                              String type,
                              String docId,
                              String attachmenturls,
                              String recordId){
        mtype = type;
        Log.i("bbb",mtype);
        mdocId = docId;
        content=content.replace("<","&lt;").replace(">","&gt;");



        new PublishNoticeWorkerTask(new PublishNoticeWorkerTask.AsyncWork(){
            Toast toast;

            @Override
            public void preExcute() {
                toast = Toast.makeText(mcontext,"正在发送...",Toast.LENGTH_SHORT);
                toast.show();
            }

            @Override
            public void postWork(Integer integer) {
                Log.i("integer4:"," "+integer);
                if (integer == 0){
                    toast.cancel();
                    Toast.makeText(mcontext, "发送成功！", Toast.LENGTH_SHORT).show();
                }else if (integer == 1){
                    toast.cancel();
                    Toast.makeText(mcontext, "无效的用户id，请重新登录", Toast.LENGTH_SHORT).show();
                }else if (integer == 2){
                    toast.cancel();
                    Toast.makeText(mcontext, "获取机器码错误，请重新登录", Toast.LENGTH_SHORT).show();
                }else if (integer == -2){
                    toast.cancel();
                    Toast.makeText(mcontext, "未知错误", Toast.LENGTH_SHORT).show();
                }else {
                    toast.cancel();
                    Toast.makeText(mcontext, "发送失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                }
            }
        }).execute(getUid(),getPeopleList(),getOrganizeList(),getMac(),
                getSign(subject, content,needSendnote,needReturn,attachmenturls,recordId),
                getTimeStamp(),subject,content,needSendnote,needReturn,type,docId,attachmenturls,recordId);
    }

    private String getUid(){
        return getUserInformationDao.getUid();
    }

    private String getMac(){
        return getUserInformationDao.getMac();
    }

    private String getTimeStamp(){
        return getUserInformationDao.getTimeStamp();
    }

    private String getPeopleList(){
        return publishNoticeDao.selectPeopleList();
    }

    private String getOrganizeList(){
        return publishNoticeDao.selectOrganizeList();
    }

    private String getSign(String subject,
                                String content,
                                String is_sendnote,
                                String is_return,
                           String attachmenturls,
                           String recordId){
        HashMap<String , String> map = new HashMap<>();
        if(mtype == "2" ){
            map.put("mac",getMac());
            map.put("uid",getUid());
            map.put("timestamp",getTimeStamp());
            if(!getOrganizeList().isEmpty())
            map.put("organize_list",getOrganizeList());
            if(!getPeopleList().isEmpty())
            map.put("people_list",getPeopleList());
            map.put("docId",mdocId);
        }else{
            map.put("mac",getMac());
            map.put("uid",getUid());
            map.put("timestamp",getTimeStamp());
            if(!getPeopleList().isEmpty())
            map.put("people_list",getPeopleList());
            if(!getOrganizeList().isEmpty())
            map.put("organize_list",getOrganizeList());
            map.put("subject",subject);
            if(!content.isEmpty())
            map.put("content",content);
            if(!attachmenturls.isEmpty())
                map.put("attachment",attachmenturls);
            map.put("is_sendnote",is_sendnote);
            map.put("is_return",is_return);
            if (!recordId.equals("null")){
                map.put("is_relay",recordId);
            }
        }
        GetSignTool getSignTool = new GetSignTool();
        return getSignTool.getSign(map);
    }
}
