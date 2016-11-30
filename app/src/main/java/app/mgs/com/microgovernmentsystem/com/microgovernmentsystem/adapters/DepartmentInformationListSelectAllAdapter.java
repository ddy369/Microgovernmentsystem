package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.adapters;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.ListBeanSelect;
import app.mgs.com.microgovernmentsystem.R;

public class DepartmentInformationListSelectAllAdapter extends BaseAdapter{
    private LayoutInflater layoutInflater;
    private ArrayList<ListBeanSelect> listBeanSelects;

    public DepartmentInformationListSelectAllAdapter(Context context) {
        layoutInflater = LayoutInflater.from(context);
    }

    public void setListBeanSelects(ArrayList<ListBeanSelect> listBeanSelects){
        this.listBeanSelects = listBeanSelects;
    }

    @Override
    public int getCount() {
        return listBeanSelects == null ? 0 : listBeanSelects.size() ;
    }

    @Override
    public Object getItem(int position) {
        return listBeanSelects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final ListBeanSelect bean = listBeanSelects.get(position);
        InfoViewSelect infoView;
        if (convertView == null) {
            infoView = new InfoViewSelect();
            convertView = layoutInflater.inflate
                    (R.layout.adapter_for_listview_department_information_list_select_all,parent,false);
            infoView.tvContent = (TextView) convertView.findViewById
                    (R.id.adp_for_lv_department_information_list_select_all_tv);
            infoView.ibtSelect = (ImageButton) convertView.findViewById
                    (R.id.adp_for_lv_department_information_list_select_all_ibt);
            convertView.setTag(infoView);
        }else {
            infoView = (InfoViewSelect) convertView.getTag();
        }
        infoView.tvContent.setText(bean.getListViewItem());
        //根据点击状态来设置文本和图片
        if (bean.isStatus()){
            infoView.tvContent.setTextColor(0xFF0984EC);
            infoView.ibtSelect.setVisibility(View.VISIBLE);
        }
        return convertView;
    }
}
