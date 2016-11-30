package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import app.mgs.com.microgovernmentsystem.R;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.Contacts;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.ChineseToSpell;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.SideBar;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.UserComparator;

/**
 * Created by Administrator on 2016/8/5.
 */
public class SortByNameAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> group = new ArrayList<>();
    private Map<String, List<Contacts>> childMap=new HashMap<>();
    private SideBar sideBar;
    private String[] department = new String[]{ "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z" };
    public void setSideBar(SideBar a)
    {
        this.sideBar=a;
    }
    public SortByNameAdapter(List<Contacts> data, Context a)
    {
        context=a;
        group = new ArrayList<>();
        childMap=new HashMap<>();
        sideBar=null;
        int i;
        /*UserComparator comparator=new UserComparator();
        Collections.sort(data,comparator);*/
        for(String s:department)
        {
            if(!childMap.containsKey(s))
            {
                List<Contacts> tem=new ArrayList<>();
                childMap.put(s,tem);
                group.add(s);
            }
        }
        for (Contacts contacts : data) {
            //首次遇到新部门
            if(!group.contains(getheadchar(contacts.name)))
            {
                group.add(getheadchar(contacts.name));
                List<Contacts> tem=new ArrayList<>();
                tem.add(contacts);


                childMap.put(getheadchar(contacts.name),tem);
            }else{


                childMap.get(getheadchar(contacts.name)).add(contacts);
            }


        }
        UserComparator comparator=new UserComparator();
     //   Collections.sort(data,comparator);
        Iterator iter = childMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
             //  Object key = entry.getKey();
            List<Contacts> val =( List<Contacts>) entry.getValue();
            Collections.sort(val,comparator);
        }


    }
    public String getheadchar(String s)
    {
        String convert="";
        ChineseToSpell chineseToSpell=new ChineseToSpell();
        convert += String.valueOf(chineseToSpell.getStringPinYin(s).charAt(0)).toUpperCase();
        return convert;
    }



    @Override
    public int getGroupCount() {
        return group.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return childMap.get(group.get(i)).size();
    }

    @Override
    public Object getGroup(int i) {
        return group.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return childMap.get(group.get(i)).get(i1);
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
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        View view1;
        if(getChildrenCount(i)>0) {
            view1 = inflater.inflate(R.layout.adapter_for_expandablelistview_sort_by_name_list_parent, null);
            TextView textView = (TextView) view1.findViewById(R.id.frg_sort_by_name_expandable_parent_tv_tittle);
            textView.setText(department[i]);
        }
        else
        {
            view1 = inflater.inflate(R.layout.fragment_blank, null);

        }
        if(sideBar!=null)
        {
            sideBar.setChoose(i);
        }
        return view1;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        RelativeLayout view1 = (RelativeLayout)inflater.inflate(R.layout.adapter_for_expandablelistview_sort_by_name_list_child,null);
        TextView tvName = (TextView) view1.findViewById(R.id.frg_sort_by_name_expandable_child_tv_name);
        TextView tvPosition = (TextView) view1.findViewById(R.id.frg_sort_by_name_expandable_tv_position);

        tvName.setText(childMap.get(group.get(i)).get(i1).name);
        tvPosition.setText(childMap.get(group.get(i)).get(i1).position);

        if(!childMap.get(group.get(i)).get(i1).mobile_phone.isEmpty())
        {
            //  ImageButton tem=new ImageButton(context);
            ImageView item1 = new ImageView(context);
            item1.setImageResource(R.drawable.ic_phone_18dp);//设置图片
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);//与父容器的右侧对齐
            //lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);//与父容器的上侧对齐
            lp.leftMargin=30;
            lp.topMargin=30;
            item1.setId(R.id.ic_phone_18dp);//设置这个View 的id
            item1.setLayoutParams(lp);//设置布局参数
            view1.addView(item1);
        }
        if(!childMap.get(group.get(i)).get(i1).tel.isEmpty())
        {
            ImageView item1 = new ImageView(context);
            item1.setImageResource(R.drawable.ic_telephone1_18dp);//设置图片
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            //
            //lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);//与父容器的上侧对齐
            if(!childMap.get(group.get(i)).get(i1).mobile_phone.isEmpty())
            {
                lp.addRule(RelativeLayout.LEFT_OF,R.id.ic_phone_18dp);//与父容器的右侧对齐
            }else {
                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);//与父容器的右侧对齐
            }
            lp.leftMargin=30;
            lp.topMargin=30;
            item1.setId(R.id.ic_telephone1_18dp);//设置这个View 的id
            item1.setLayoutParams(lp);//设置布局参数
            view1.addView(item1);
        }
        TextView tvPicture = (TextView) view1.findViewById(R.id.frg_sort_by_name_expandable_child_tv_picture);
        int index = i1 % 4;
        if (index==0){
            tvPicture.setTextColor(0xFFb8d229);
            tvPicture.setText(childMap.get(group.get(i)).get(i1).name.substring(0,1));
            tvPicture.setBackgroundResource(R.drawable.shape_circular_green);
        }else if (index==1){
            tvPicture.setTextColor(0xFF85a4db);
            tvPicture.setText(childMap.get(group.get(i)).get(i1).name.substring(0,1));
            tvPicture.setBackgroundResource(R.drawable.shape_circular_blue);
        }else if (index==2){
            tvPicture.setTextColor(0xFFF5ae6e);
            tvPicture.setText(childMap.get(group.get(i)).get(i1).name.substring(0,1));
            tvPicture.setBackgroundResource(R.drawable.shape_circular_orange);
        }else {
            tvPicture.setTextColor(0xFF4cb6c4);
            tvPicture.setText(childMap.get(group.get(i)).get(i1).name.substring(0,1));
            tvPicture.setBackgroundResource(R.drawable.shape_circular_blue1);
        }
        return view1;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}

