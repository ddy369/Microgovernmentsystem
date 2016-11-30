package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import app.mgs.com.microgovernmentsystem.R;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.ListBean;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.ListBeanDocument;

/**
 * Created by dingyi on 2016-08-13.
 */
public class DocumentListAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private ArrayList<ListBeanDocument> listBeanDocument;

    public void setListBeans(ArrayList<ListBeanDocument> listBean){
        this.listBeanDocument = listBean;
    }

    public DocumentListAdapter(Context context){
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listBeanDocument == null ? 0 : listBeanDocument.size() ;
    }

    @Override
    public Object getItem(int position) {
        return listBeanDocument.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ListBeanDocument bean = listBeanDocument.get(position);
        DocumentViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new DocumentViewHolder();
            //获得组件，实例化组件
            convertView = layoutInflater.inflate(R.layout.adapter_for_listview_magazine_list,parent,false);
            viewHolder.documentTitle = (TextView) convertView.findViewById(R.id.adp_for_lv_magazine_list_tv_title);
            viewHolder.documentTime = (TextView) convertView.findViewById(R.id.adp_for_lv_magazine_tv_time);
            viewHolder.documentSubject = (TextView) convertView.findViewById(R.id.adp_for_lv_magazine_list_tv_subject);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (DocumentViewHolder) convertView.getTag();
        }

        //绑定数据
        viewHolder.documentTitle.setText(bean.getTitle());
        viewHolder.documentSubject.setText(bean.getSubject());
        viewHolder.documentTime.setText(bean.getUpdateDate());

        int i = bean.getRead();
        if (i == 1){
            viewHolder.documentTitle.setBackgroundResource(R.drawable.department_sharp);
        }else {
            viewHolder.documentTitle.setBackgroundResource(R.drawable.button_sharp);
        }

        return convertView;
    }
}
