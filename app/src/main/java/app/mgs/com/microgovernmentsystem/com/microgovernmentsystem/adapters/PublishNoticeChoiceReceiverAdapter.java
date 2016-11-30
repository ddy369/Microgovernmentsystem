package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.adapters;

import android.content.Context;
import android.content.pm.LauncherApps;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import app.mgs.com.microgovernmentsystem.R;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.ListBeanPNCReceiver;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.PublishNoticeChoiceReceiverDataHelper;


public class PublishNoticeChoiceReceiverAdapter extends BaseExpandableListAdapter {

    private List<String> group = null;
    private Map<String, ArrayList<ListBeanPNCReceiver>> map = null;

    private Callback mcallback;

    private Context context;

    //父控件勾选的回调接口
    public interface Callback{
        void click(View view,int i);
    }

    public PublishNoticeChoiceReceiverAdapter(Context context,Callback callback){
        this.context = context;
        mcallback = callback;
    }

    public void setListAndMap(List<String> group, Map<String, ArrayList<ListBeanPNCReceiver>> map){
        this.group = group;
        this.map = map;
    }

    @Override
    public int getGroupCount() {
        return group.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return map.get(group.get(i)).size();
    }

    @Override
    public Object getGroup(int i) {
        return group.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return map.get(group.get(i)).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(final int i, boolean b, View view, ViewGroup viewGroup) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        View view1 = inflater.inflate(R.layout.adapter_for_expandable_publish_notice_receiver_parent,null);
        TextView textView = (TextView) view1.findViewById(R.id.adp_for_expandable_publish_notice_receiver_parent_tv);
        textView.setText(group.get(i));

        //将组控件的checkbox通过接口传出去
        final CheckBox checkBox = (CheckBox) view1.findViewById(R.id.adp_for_expandable_publish_notice_receiver_parent_checkbox);
        //遍历组控件下的所有子控件是否被选中，如果全部被选中，则组控件被选中，反之子控件若有1个没被选中，则组控件不被选中
        int num = 0;
        for (int count = 0; count < map.get(group.get(i)).size(); count++){
            if (map.get(group.get(i)).get(count).getChoice() == 1)
                num++;
        }
        if (num == map.get(group.get(i)).size())
            checkBox.setChecked(true);

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mcallback.click(view,i);
            }
        });

        ImageView imageView = (ImageView) view1.findViewById(R.id.adp_for_expandable_publish_notice_receiver_parent_itv);
        if (b) {
            imageView.setImageResource(R.drawable.ic_down_arrow_24dp);
        } else {
            imageView.setImageResource(R.drawable.ic_chevron_right_24dp);
        }
        return view1;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        RelativeLayout view1 = (RelativeLayout)inflater.inflate(R.layout.adapter_for_expandable_publish_notice_receiver_child,null);
        TextView textView = (TextView) view1.findViewById(R.id.adp_for_expandable_publish_notice_receiver_child_tv_name);
        textView.setText(map.get(group.get(i)).get(i1).getName());
        CheckBox checkBox = (CheckBox) view1.findViewById(R.id.adp_for_expandable_publish_notice_receiver_child_checkbox);
        int flag = map.get(group.get(i)).get(i1).getChoice();
        if (flag==1){
            checkBox.setChecked(true);
        }else {
            checkBox.setChecked(false);
        }
        return view1;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
