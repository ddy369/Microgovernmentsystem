package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.mgs.com.microgovernmentsystem.R;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.Contacts;

/**
 * Created by Administrator on 2016/8/2.
 */
public class ImportContactsAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> group = new ArrayList<>();
    private Map<String, List<Contacts>> childMap = new HashMap<>();
    private Map<String, CheckBox> groupcheck = new HashMap<>();
    private Map<String, List<CheckBox>> childCheckdMap = new HashMap<>();
    public List<Contacts> data;

    public ImportContactsAdapter(List<Contacts> data, Context a) {
        context = a;
        this.data=data;
        group = new ArrayList<>();
        childMap = new HashMap<>();

        for (Contacts contacts : data) {
            //默认全都不选中
            contacts.ischeck = false;
            //首次遇到新部门
            if (!group.contains(contacts.department)) {
                group.add(contacts.department);
                List<Contacts> tem = new ArrayList<>();
                List<CheckBox> tem1 = new ArrayList<>();
                tem.add(contacts);
                childCheckdMap.put(contacts.department, tem1);

                childMap.put(contacts.department, tem);
            } else {


                childMap.get(contacts.department).add(contacts);
            }

        }
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
        View view1 = inflater.inflate(R.layout.adapter_for_expandable_import_contacts_parent,null);
        TextView textView = (TextView) view1.findViewById(R.id.adp_for_expandable_import_contacts_parent_tv);
        textView.setText(group.get(i));
        ImageView imageView = (ImageView) view1.findViewById(R.id.adp_for_expandable_import_contacts_parent_itv);
        if (b) {
            imageView.setImageResource(R.drawable.ic_down_arrow_24dp);
        } else {
            imageView.setImageResource(R.drawable.ic_chevron_right_24dp);
        }
        CheckBox checkBox = (CheckBox) view1.findViewById(R.id.adp_for_expandable_import_contacts_parent_checkbox);
        groupcheck.put(group.get(i), checkBox);
        Myoncheckchange a = new Myoncheckchange(i);
        int num=0;
        List<Contacts> temgroup = childMap.get(group.get(i));
        for(Contacts contacts : temgroup)
        {
            if(contacts.ischeck)num++;
        }
        if(num==temgroup.size())
           checkBox.setChecked(true);
        checkBox.setOnCheckedChangeListener(a);
        return view1;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        RelativeLayout  view1 = (RelativeLayout)inflater.inflate(R.layout.adapter_for_expandable_import_contacts_child,null);
        TextView tvName = (TextView) view1.findViewById(R.id.adp_for_expandable_import_contacts_child_tv_name);
        TextView tvPosition = (TextView) view1.findViewById(R.id.adp_for_expandable_import_contacts_child_tv_position);
        tvName.setText(childMap.get(group.get(i)).get(i1).name);
        tvPosition.setText(childMap.get(group.get(i)).get(i1).position);
        if (!childMap.get(group.get(i)).get(i1).mobile_phone.isEmpty()) {
            //  ImageButton tem=new ImageButton(context);
            ImageView item1 = new ImageView(context);
            item1.setImageResource(R.drawable.ic_phone_18dp);//设置图片
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);//与父容器的右侧对齐
            //lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);//与父容器的上侧对齐
            lp.leftMargin = 30;
            lp.topMargin = 30;
            item1.setId(R.id.ic_phone_18dp);//设置这个View 的id
            item1.setLayoutParams(lp);//设置布局参数
            view1.addView(item1);
        }
        if (!childMap.get(group.get(i)).get(i1).tel.isEmpty()) {
            ImageView item1 = new ImageView(context);
            item1.setImageResource(R.drawable.ic_telephone1_18dp);//设置图片
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            //
            //lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);//与父容器的上侧对齐
            if (!childMap.get(group.get(i)).get(i1).mobile_phone.isEmpty()) {
                lp.addRule(RelativeLayout.LEFT_OF, R.id.ic_phone_18dp);//与父容器的右侧对齐
            } else {
                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);//与父容器的右侧对齐
            }
            lp.leftMargin = 30;
            lp.topMargin = 30;
            item1.setId(R.id.ic_telephone1_18dp);//设置这个View 的id
            item1.setLayoutParams(lp);//设置布局参数
            view1.addView(item1);
        }

        TextView tvPicture = (TextView) view1.findViewById(R.id.adp_for_expandable_import_contacts_child_tv_picture);
        int index = i1 % 4;
        if (index == 0) {
            tvPicture.setTextColor(0xFFb8d229);
            tvPicture.setText(childMap.get(group.get(i)).get(i1).name.substring(0, 1));
            tvPicture.setBackgroundResource(R.drawable.shape_circular_green);
        } else if (index == 1) {
            tvPicture.setTextColor(0xFF85a4db);
            tvPicture.setText(childMap.get(group.get(i)).get(i1).name.substring(0, 1));
            tvPicture.setBackgroundResource(R.drawable.shape_circular_blue);
        } else if (index == 2) {
            tvPicture.setTextColor(0xFFF5ae6e);
            tvPicture.setText(childMap.get(group.get(i)).get(i1).name.substring(0, 1));
            tvPicture.setBackgroundResource(R.drawable.shape_circular_orange);
        } else {
            tvPicture.setTextColor(0xFF4cb6c4);
            tvPicture.setText(childMap.get(group.get(i)).get(i1).name.substring(0, 1));
            tvPicture.setBackgroundResource(R.drawable.shape_circular_blue1);
        }
        CheckBox checkBox = (CheckBox) view1.findViewById(R.id.adp_for_expandable_import_contacts_child_checkbox);
        childCheckdMap.get(group.get(i)).add(checkBox);
        checkBox.setChecked(childMap.get(group.get(i)).get(i1).ischeck);
        checkBox.setOnCheckedChangeListener(new childoncheckchange(i,i1));
        return view1;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    public class Myoncheckchange implements CompoundButton.OnCheckedChangeListener {
        private int i;

        public Myoncheckchange(int a) {
            super();
            i = a;
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            if (!compoundButton.isPressed()) return;
            List<Contacts> tem = childMap.get(group.get(i));
            List<CheckBox> temcheck = childCheckdMap.get(group.get(i));
            if (b) {

                for (Contacts contacts : tem) {
                    contacts.ischeck = true;
                }
                for (CheckBox ch : temcheck) {
                    ch.setChecked(true);
                }


            } else {
                for (Contacts contacts : tem) {
                    contacts.ischeck = false;
                }
                for (CheckBox ch : temcheck) {
                    ch.setChecked(false);
                }
            }

        }

    }
    public class childoncheckchange implements CompoundButton.OnCheckedChangeListener {
        int i,j;
        public childoncheckchange(int i, int j)
        {
            this.i=i;
            this.j=j;
        }
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            Contacts tem=(Contacts)getChild(i,j);
            tem.ischeck=b;
            int num=0;
            List<Contacts> temgroup = childMap.get(group.get(i));
            for(Contacts contacts : temgroup)
            {
                if(contacts.ischeck)num++;
            }
            if(num==temgroup.size())
                groupcheck.get(group.get(i)).setChecked(true);
            else
                groupcheck.get(group.get(i)).setChecked(false);
        }

    }
    public int  setallchecked(boolean b)
    {

        for (Contacts contacts :data) {
            contacts.ischeck=b;
        }
        notifyDataSetInvalidated();
      /* for(String s : group)
       {
           groupcheck.get(s).setChecked(b);
           for (CheckBox checkBox : childCheckdMap.get(s))
           {
               checkBox.setChecked(b);
           }
       }*/
        return 0;
    }
    public List <Contacts> getchecked()
    {
        List<Contacts> tem=new ArrayList<>();
        for (Contacts contacts :data) {
            if (contacts.ischeck)
                tem.add(contacts);
        }
        return tem;
    }


}
