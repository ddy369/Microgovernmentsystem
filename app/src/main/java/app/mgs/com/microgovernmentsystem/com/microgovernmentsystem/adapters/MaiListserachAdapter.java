package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import app.mgs.com.microgovernmentsystem.R;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.Contacts;

/**
 * Created by Administrator on 2016/8/8.
 */
public class MaiListserachAdapter extends ArrayAdapter<Contacts> {
    private int resourceId;
    public MaiListserachAdapter(Context context, int textViewResourceId,
                        List<Contacts> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Contacts contacts = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.username = (TextView) view.findViewById
                    (R.id.frg_sort_by_department_expandable_child_tv_name);
            viewHolder.userpicture = (TextView) view.findViewById
                    (R.id.frg_sort_by_department_expandable_child_tv_picture);
            viewHolder.usertel = (TextView) view.findViewById
                    (R.id.frg_sort_by_department_expandable_child_tv_tel);
            viewHolder.userphone = (TextView) view.findViewById
                    (R.id.frg_sort_by_department_expandable_child_tv_phone);

            view.setTag(viewHolder); // 将ViewHolder存储在View中
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag(); // 重新获取ViewHolder
        }
        viewHolder.username.setText(contacts.name);
        viewHolder.usertel.setText("办公室电话："+ contacts.tel);
        viewHolder.userphone.setText("手机："+ contacts.mobile_phone);
        int index = position % 4;
        if (index==0){
            viewHolder.userpicture.setTextColor(0xFFb8d229);
            viewHolder.userpicture.setText(contacts.name.substring(0,1));
            viewHolder.userpicture.setBackgroundResource(R.drawable.shape_circular_green);
        }else if (index==1){
            viewHolder.userpicture.setTextColor(0xFF85a4db);
            viewHolder.userpicture.setText(contacts.name.substring(0,1));
            viewHolder.userpicture.setBackgroundResource(R.drawable.shape_circular_blue);
        }else if (index==2){
            viewHolder.userpicture.setTextColor(0xFFF5ae6e);
            viewHolder.userpicture.setText(contacts.name.substring(0,1));
            viewHolder.userpicture.setBackgroundResource(R.drawable.shape_circular_orange);
        }else {
            viewHolder.userpicture.setTextColor(0xFF4cb6c4);
            viewHolder.userpicture.setText(contacts.name.substring(0,1));
            viewHolder.userpicture.setBackgroundResource(R.drawable.shape_circular_blue1);
        }
        return view;
    }
    class ViewHolder {
        TextView username;
        TextView userpicture;
        TextView usertel;
        TextView userphone;
    }
}

