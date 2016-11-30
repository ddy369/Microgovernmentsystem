package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import app.mgs.com.microgovernmentsystem.R;

/**
 * Created by Administrator on 2016/10/9.
 */
public class DialogAdapter extends ArrayAdapter<String> {
    private int resourceId;
    public DialogAdapter(Context context, int textViewResourceId,
                                List<String> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String s = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.username = (TextView) view.findViewById
                    (R.id.dialog_item);
            view.setTag(viewHolder); // 将ViewHolder存储在View中
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag(); // 重新获取ViewHolder
        }
        viewHolder.username.setText(s);
        return view;
    }
    class ViewHolder {
        TextView username;
    }
}