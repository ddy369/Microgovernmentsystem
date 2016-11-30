package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import app.mgs.com.microgovernmentsystem.R;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.ListBeanAttachment;

/**
 * Created by Administrator on 2016/9/14.
 */
public class AttachmentAdapter extends ArrayAdapter<ListBeanAttachment> {
private int resourceId;
public AttachmentAdapter(Context context, int textViewResourceId,
                            List<ListBeanAttachment> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
        }
@Override
public View getView(int position, View convertView, ViewGroup parent) {
        //String s = getItem(position);
        ListBeanAttachment listBeanAttachment = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
        view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        viewHolder = new ViewHolder();
        viewHolder.username = (TextView) view.findViewById(R.id.act_notice_tv_download_content);
        view.setTag(viewHolder); // 将ViewHolder存储在View中
        } else {
        view = convertView;
        viewHolder = (ViewHolder) view.getTag(); // 重新获取ViewHolder
        }
        viewHolder.username.setText(listBeanAttachment.getName());
        return view;
        }

        class ViewHolder {
                TextView username;
        }

}