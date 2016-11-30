package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.provider.Settings;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.GetUserInformationDao;

/**
 * Created by Administrator on 2016/9/18.
 */
public class UnreadMessageNum {
    private static int noticenum;
    private static int documentnum;
    private static int magazinenum;
    private static int publicinformationnum;
    private static int departmentinformationnum;
    private static Handler mhandler;
    public static ArrayList<Integer> list=new ArrayList<>();
    public static int getNoticenum()
    {
        return noticenum;
    }
    public static int getDocumentnum()
    {
        return documentnum;
    }
    public static int getMagazinenum()
    {
        return magazinenum;
    }
    public static int getPublicinformationnum()
    {
        return publicinformationnum;
    }
    public static int getDepartmentinformationnum()
    {
        return departmentinformationnum;
    }
    public static void addnoticenum(int num)
    {
        noticenum+=num;
        sendchenge();
    }
    public static void adddocumentnum(int num)
    {
        documentnum+=num;
        sendchenge();
    }
    public static void addmagazinenum(int num)
    {
        magazinenum+=num;
        sendchenge();

    }
    public static void addpublicinformationnum(int num)
    {
        publicinformationnum+=num;
        sendchenge();
    }
    public static void adddepartmentinformationnum(int num)
    {
        departmentinformationnum+=num;
        sendchenge();
    }
    public static void cancelNotification()
    {
        NotificationManager manager = (NotificationManager)
                Datahelper.getContext().getSystemService(Datahelper.getContext().NOTIFICATION_SERVICE);
        while (!list.isEmpty())
        {
            manager.cancel(list.remove(0));
        }
    }
    public static void setHandler(Handler a)
    {
      mhandler=a;
    }
    private static void sendchenge()
    {
        if(mhandler!=null)
        mhandler.sendEmptyMessageDelayed(0,0);
    }
    public static void resetnoticenum()
    {
        noticenum=0;
        sendchenge();
    }
    public static void resetdocumentnum()
    {
        documentnum=0;
        sendchenge();
    }
    public static void resetmagazinenum()
    {
        magazinenum=0;
        sendchenge();
    }
    public static void resetpublicinformationnum()
    {
        publicinformationnum=0;
        sendchenge();
    }
    public static void resetdepartmentinformationnum()
    {
        departmentinformationnum=0;
        sendchenge();
    }
    public static void resetall()
    {
        noticenum=0;
        documentnum=0;
        departmentinformationnum=0;
        magazinenum=0;
        departmentinformationnum=0;
    }

    public static void setnoticenum(int num)
    {
        noticenum=num;
        sendchenge();
    }

    public static void setdocumentnum(int num)
    {
        documentnum=num;
        sendchenge();
    }

    public static void updateunreadnum()
    {
        RequestParams params = new RequestParams(Values.ServerAddress+"/InformationRead");
        params.addBodyParameter("uid",getUid());
        params.addBodyParameter("mac",getMac());
        params.addBodyParameter("sign" , getSign());
        params.addBodyParameter("timestamp" ,getTimeStamp());
        x.http().post(params, new Callback.CommonCallback<String>() {
            public void onSuccess(String result) {
                try{
                if (result != null) {
                    JSONObject object = new JSONObject(result);
                    int errorCode = object.getInt("error_code");
                    if (errorCode == 0) {
                        setnoticenum(object.getInt("info_count"));
                        setdocumentnum(object.getInt("doc_count"));

                    }
                }
                }catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            }

            @Override
            public void onCancelled(Callback.CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }

        });
    }
    private static String getSign() {
        HashMap<String, String> map = new HashMap<>();
        map.put("mac", getMac());
        map.put("timestamp", getTimeStamp());
        map.put("uid", getUid());
        GetSignTool getSignTool = new GetSignTool();
        return getSignTool.getSign(map);
        //获取签名值
//        return md5Tool.getMDSStr("govoa"+"mac"+getMac()+"password"+getPassword()+"timestamp"+getTimeStamp()
//                +"username"+account+"govoa");
    }
    private static String getMac() {
        return Settings.Secure.getString(Datahelper.getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private static String getTimeStamp() {
        //获取时间戳
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE);
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        return formatter.format(curDate);
    }

    private static String getUid() {
        GetUserInformationDao getUserInformationDao = new GetUserInformationDao();
        return getUserInformationDao.getUid();
    }


    /**
     * 绘制未读消息数量显示
     *
     * @param context
     *            上下文
     * @param icon
     *            需要被添加的icon的资源ID
     * @param news
     *            未读的消息数量
     * @return drawable
     */
    @SuppressWarnings("unused")
    public static Drawable displayNewsNumber(Context context, int icon, String news) {
        // 初始化画布
        int iconSize = (int) context.getResources().getDimension(
                android.R.dimen.app_icon_size);
        // Bitmap contactIcon = Bitmap.createBitmap(iconSize, iconSize,
        // Config.ARGB_8888);
        Bitmap iconBitmap = BitmapFactory.decodeResource(
                context.getResources(), icon);
        Canvas canvas = new Canvas(iconBitmap);
        // 拷贝图片
        Paint iconPaint = new Paint();
        iconPaint.setDither(true);// 防抖动
        iconPaint.setFilterBitmap(true);// 用来对Bitmap进行滤波处理
        Rect src = new Rect(0, 0, iconBitmap.getWidth(), iconBitmap.getHeight());
        Rect dst = new Rect(0, 0, iconBitmap.getWidth(), iconBitmap.getHeight());
        canvas.drawBitmap(iconBitmap, src, dst, iconPaint);
        // 启用抗锯齿和使用设备的文本字距
        Paint countPaint = new Paint(Paint.ANTI_ALIAS_FLAG
                | Paint.DEV_KERN_TEXT_FLAG);
        countPaint.setColor(Color.RED);
        canvas.drawCircle(iconSize - 13, 20, 10, countPaint);

        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        // textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        textPaint.setTextSize(19f);
        canvas.drawText(news, iconSize - 18, 27, textPaint);
        return new BitmapDrawable(iconBitmap);
    }

}
