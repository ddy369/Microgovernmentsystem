package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.im;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseNotifier;
import com.hyphenate.easeui.utils.EaseCommonUtils;

import java.util.List;

import app.mgs.com.microgovernmentsystem.R;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.Contacts;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Datahelper;

import static com.hyphenate.easeui.utils.EaseUserUtils.getUserInfo;

/**
 * Created by Administrator on 2016/7/29.
 */
public class imhelper {
    public EaseUI easeUI;
    EMMessageListener msgListener;
    Context appContext;

    public void  initim(Context context)
    {
        appContext=context;
       // intteaseui();
        msgListener = new EMMessageListener() {

            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                //收到消息
                //   easeUI.getNotifier().onNewMesg(messages);
                /*int sysVersion = Integer.parseInt(Build.VERSION.SDK);
                if(sysVersion>15) {*/
                    NotificationManager manager = (NotificationManager)
                            appContext.getSystemService(appContext.NOTIFICATION_SERVICE);
                    Notification.Builder builder = new Notification.Builder(appContext);
                    builder.setContentInfo("");
                    builder.setContentText("您有新消息");
                    builder.setContentTitle("");
                    builder.setSmallIcon(R.mipmap.ic_launcher);
                    builder.setTicker("新消息");
                    builder.setAutoCancel(true);
                    builder.setDefaults(Notification.DEFAULT_ALL);
                    builder.setWhen(System.currentTimeMillis());
                    Intent intent = new Intent(appContext, ConversationListActiviity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(appContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                    builder.setContentIntent(pendingIntent);
                    Notification notification = builder.getNotification();
                    manager.notify(1, notification);

           /* for(int i = 0; i < messages.size(); i++)
            {
                EMMessage msg= messages.get(i);
                //System.out.println(list.get(i));
              *//*  startActivity(new
                        Intent(MainActivity.this,
                        ChatActivity.class).putExtra("userId",
                        msg.getFrom()));*//*
              //  EMClient.getInstance().chatManager().importMessages(msg);
            }*/

            }


            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {
                //收到透传消息
            }

            @Override
            public void onMessageReadAckReceived(List<EMMessage> messages) {
                //收到已读回执
            }

            @Override
            public void onMessageDeliveryAckReceived(List<EMMessage> message) {
                //收到已送达回执
            }

            @Override
            public void onMessageChanged(EMMessage message, Object change) {
                //消息状态变动
            }
        };
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
        //get easeui instance
        easeUI = EaseUI.getInstance();
//需要EaseUI库显示用户头像和昵称设置此provider
        easeUI.setUserProfileProvider(new EaseUI.EaseUserProfileProvider() {

            @Override
            public EaseUser getUser(String username) {
                Datahelper datahelper=new Datahelper();
                EaseUser emuser=new EaseUser(username);
                List<Contacts> contactses =datahelper.getusers(new Datahelper.callback() {
                    @Override
                    public void callback(List<Contacts> users) {

                    }
                });

                for(Contacts contacts : contactses)
                {
                    if(contacts.emid.equals(username)){
                            emuser.setNick(contacts.name);
                        break;

                    }
                }
                return emuser;
            }
        });
    }
    private void intteaseui() {
        easeUI = EaseUI.getInstance();
        easeUI.getNotifier().setNotificationInfoProvider(new EaseNotifier.EaseNotificationInfoProvider() {

            @Override
            public String getTitle(EMMessage message) {
                //修改标题,这里使用默认
                return null;
            }

            @Override
            public int getSmallIcon(EMMessage message) {
                //设置小图标，这里为默认
                return 0;
            }

            @Override
            public String getDisplayedText(EMMessage message) {
                // 设置状态栏的消息提示，可以根据message的类型做相应提示
                String ticker = EaseCommonUtils.getMessageDigest(message, appContext);
                if (message.getType() == EMMessage.Type.TXT) {
                    ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");
                }
                EaseUser user = getUserInfo(message.getFrom());
                if (user != null) {
                    return getUserInfo(message.getFrom()).getNick() + ": " + ticker;
                } else {
                    return message.getFrom() + ": " + ticker;
                }
            }

            @Override
            public String getLatestText(EMMessage message, int fromUsersNum, int messageNum) {
                return null;
                // return fromUsersNum + "个基友，发来了" + messageNum + "条消息";
            }

            @Override
            public Intent getLaunchIntent(EMMessage message, int usernum) {
                //设置点击通知栏跳转事件
                Intent intent = new Intent(appContext, ConversationListActiviity.class);
               /* if (usernum == 1) {
                    intent = new Intent(appContext, ChatActivity.class);
                    //有电话时优先跳转到通话页面
                    if (false) {
                        //      intent = new Intent(appContext, VideoCallActivity.class);
                    } else if (false) {
                        //            intent = new Intent(appContext, VoiceCallActivity.class);
                    } else {
                        EMMessage.ChatType chatType = message.getChatType();
                        if (chatType == EMMessage.ChatType.Chat) { // 单聊信息
                            intent.putExtra("userId", message.getFrom());
                            intent.putExtra("chatType", Constant.CHATTYPE_SINGLE);
                        } else { // 群聊信息
                            // message.getTo()为群聊id
                            intent.putExtra("userId", message.getTo());
                            if (chatType == EMMessage.ChatType.GroupChat) {
                                intent.putExtra("chatType", Constant.CHATTYPE_GROUP);
                            } else {
                                intent.putExtra("chatType", Constant.CHATTYPE_CHATROOM);
                            }

                        }
                    }
                }*/


                return intent;
            }
        });
    }
}
