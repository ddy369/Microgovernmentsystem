package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import app.mgs.com.microgovernmentsystem.R;

/**
 * Created by Administrator on 2016/9/7.
 */
public class FileSelectAdapter extends ArrayAdapter<File> {
    private int resourceId;
    private callback e;
    public FileSelectAdapter(Context context, int textViewResourceId,
                                List<File> objects,callback a) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
        e=a;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        File file = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.name=(TextView) view.findViewById(R.id.act_add_attachment_name);
            viewHolder.size=(TextView) view.findViewById(R.id.act_add_attachment_size);
            viewHolder.delete=(TextView) view.findViewById(R.id.act_add_attachment_delete);
            view.setTag(viewHolder); // 将ViewHolder存储在View中
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag(); // 重新获取ViewHolder
        }
        viewHolder.name.setText(file.getName());
        String size=String.valueOf(file.length());
        float i1 = Float.parseFloat(size);
        size=size+"B";
        if (i1 > 1024) {
            i1 = i1 / 1024;
            size = "(" + String.valueOf(i1).substring(0, min(String.valueOf(i1).indexOf(".") + 3, String.valueOf(i1).length())) + "K)";
        }
        if (i1 > 1024) {
            i1 = i1 / 1024;
            size = "(" + String.valueOf(i1).substring(0, min(String.valueOf(i1).indexOf(".") + 3, String.valueOf(i1).length())) + "M)";
        }
        if (i1 > 1024) {
            i1 = i1 / 1024;
            size = "(" + String.valueOf(i1).substring(0, min(String.valueOf(i1).indexOf(".") + 3, String.valueOf(i1).length())) + "G)";
        }
        viewHolder.size.setText(size);
        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 e.callback(position);
            }
        });

        return view;
    }
    class ViewHolder {
        TextView name;
        TextView size;
        TextView delete;
    }
    public interface callback {
        void callback(int i);
    }
    private  int min(int a,int b)
    {
        if(a>b)
            return b;
        else
            return a;
    }
}
