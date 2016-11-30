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

public class NoticeListAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private ArrayList<ListBean> listBeans;

    public void setListBeans(ArrayList<ListBean> listBeans){
        this.listBeans = listBeans;
    }

    public NoticeListAdapter(Context context){
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listBeans == null ? 0 : listBeans.size() ;
    }

    @Override
    public Object getItem(int position) {
        return listBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ListBean bean = listBeans.get(position);
        InfoViewList infoView;
        if (convertView == null){
            infoView = new InfoViewList();
            //获得组件，实例化组件
            convertView = layoutInflater.inflate(R.layout.adapter_for_listview_notice_list,parent,false);
            infoView.tvTitle = (TextView) convertView.findViewById(R.id.adp_for_lv_notice_list_tv_title);
            infoView.tvTime = (TextView) convertView.findViewById(R.id.adp_for_lv_notice_tv_time);
            infoView.tvDepartment = (TextView) convertView.findViewById(R.id.adp_for_lv_notice_list_tv_department);
            convertView.setTag(infoView);
        }else {
            infoView = (InfoViewList) convertView.getTag();
        }

        //绑定数据
        infoView.tvTitle.setText(bean.getTitle());
        infoView.tvTime.setText(bean.getTime());
        infoView.tvDepartment.setText(bean.getDepartment());

        int i = bean.getRead();
        if (i == 1){
            infoView.tvDepartment.setBackgroundResource(R.drawable.department_sharp);
        }else {
            infoView.tvDepartment.setBackgroundResource(R.drawable.button_sharp);
        }

        return convertView;
    }
}
