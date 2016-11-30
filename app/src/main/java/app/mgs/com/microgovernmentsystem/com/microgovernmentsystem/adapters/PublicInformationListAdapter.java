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

/**
 * Created by Administrator on 2016/8/22.
 */
public class PublicInformationListAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private ArrayList<ListBean> listBeans;

    public void setListBeans(ArrayList<ListBean> listBeans){
        this.listBeans = listBeans;
    }
    public PublicInformationListAdapter(Context context){
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
            convertView = layoutInflater.inflate(R.layout.adapter_for_listview_public_information_list,parent,false);
            infoView.tvTitle = (TextView) convertView.findViewById(R.id.adp_for_lv_public_information_list_tv_title);
            infoView.tvUpdateDate = (TextView) convertView.findViewById(R.id.adp_for_lv_public_information_tv_time);
            infoView.tvField = (TextView) convertView.findViewById(R.id.adp_for_lv_public_information_list_tv_field);
            convertView.setTag(infoView);
        }else {
            infoView = (InfoViewList) convertView.getTag();
        }

        //绑定数据
        infoView.tvTitle.setText(bean.getTitle());
        infoView.tvUpdateDate.setText(bean.getUpdateDate());
        infoView.tvField.setText(bean.getField());

        int i = bean.getRead();
        if (i == 1){
            infoView.tvField.setBackgroundResource(R.drawable.department_sharp);
        }else {
            infoView.tvField.setBackgroundResource(R.drawable.button_sharp);
        }

        return convertView;
    }
}