package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

import app.mgs.com.microgovernmentsystem.R;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.ListBeanPNLDepartment;


public class PublishNoticeChoiceDepartmentAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private ArrayList<ListBeanPNLDepartment> listBeans;

    public void setListBeans(ArrayList<ListBeanPNLDepartment> listBeans){
        this.listBeans = listBeans;
    }

    public PublishNoticeChoiceDepartmentAdapter(Context context){
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
        final ListBeanPNLDepartment bean = listBeans.get(position);
        InfoViewPNCList infoView;
        if (convertView == null){
            infoView = new InfoViewPNCList();
            //获得组件，实例化组件
            convertView = layoutInflater.inflate(R.layout.adapter_for_listview_publish_notice_choice_department_list,
                    parent,false);
            infoView.checkBox = (CheckBox) convertView.findViewById(R.id.adp_for_lv_publish_notice_choice_department_cb);
            infoView.textView = (TextView) convertView.findViewById(R.id.adp_for_lv_publish_notice_choice_department_tv);
            convertView.setTag(infoView);
        }else {
            infoView = (InfoViewPNCList) convertView.getTag();
        }

        //绑定数据
        infoView.textView.setText(bean.getName());

        //TODO:在此处写上被选中的部门
        int i = bean.getIsChoice();
        if (i==1){
            infoView.checkBox.setChecked(true);
        }else {
            infoView.checkBox.setChecked(false);
        }

        return convertView;
    }
}
