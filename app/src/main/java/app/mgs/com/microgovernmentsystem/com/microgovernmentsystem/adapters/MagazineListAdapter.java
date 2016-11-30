package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import app.mgs.com.microgovernmentsystem.R;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.ListBeanMagazine;

/**
 * Created by Administrator on 2016-08-22.
 */
public class MagazineListAdapter extends BaseAdapter{
    private LayoutInflater layoutInflater;
    private ArrayList<ListBeanMagazine> listBeanMagazines;

    public void setListBeans(ArrayList<ListBeanMagazine> listBean){
        this.listBeanMagazines = listBean;
    }

    public MagazineListAdapter(Context context){
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listBeanMagazines == null ? 0 : listBeanMagazines.size() ;
    }

    @Override
    public Object getItem(int position) {
        return listBeanMagazines.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ListBeanMagazine bean = listBeanMagazines.get(position);
        MagazineViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new MagazineViewHolder();
            //获得组件，实例化组件
            convertView = layoutInflater.inflate(R.layout.adapter_for_listview_document_list,parent,false);
            viewHolder.magazineTitle = (TextView) convertView.findViewById(R.id.adp_for_lv_document_list_tv_title);
            viewHolder.magazineTime = (TextView) convertView.findViewById(R.id.adp_for_lv_document_tv_time);
            viewHolder.magazineSubject = (TextView) convertView.findViewById(R.id.adp_for_lv_document_list_tv_subject);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (MagazineViewHolder) convertView.getTag();
        }

        //绑定数据
        viewHolder.magazineTitle.setText(bean.getTitle());
        viewHolder.magazineSubject.setText(bean.getSubject());
        viewHolder.magazineTime.setText(bean.getUpdateDate());

        if(bean.getRead() == 1 ){
            viewHolder.magazineTitle.setBackgroundResource(R.drawable.department_sharp);
        }else{
            viewHolder.magazineTitle.setBackgroundResource(R.drawable.button_sharp);
        }

        return convertView;
    }
}
