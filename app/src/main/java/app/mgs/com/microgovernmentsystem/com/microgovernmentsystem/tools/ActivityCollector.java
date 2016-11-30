package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * 所有活动管理器
 * @author yy
 *
 */
public class ActivityCollector {
    public static List<Activity> activities = new ArrayList<Activity>();
    /**
     * 添加活动
     * @param activity
     */
    public static void addActivity(Activity activity){
        if(!activities.contains(activity)){
            activities.add(activity);
        }
    }
    public static Activity getlast()
    {
        return activities.get(activities.size()-1);
    }
    /**
     * 移除活动
     * @param activity
     */
    public static void removeActivity(Activity activity){
        activities.remove(activity);
    }
    /**
     * 结束所有活动
     */
    public static void finishAll(){
        for(Activity activity : activities){
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
    }
}
