package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools;

/**
 * Created by Administrator on 2016/8/1.
 */
public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
