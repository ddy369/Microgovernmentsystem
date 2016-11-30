package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.ListBeanMenu;
import app.mgs.com.microgovernmentsystem.R;


public class NoticeListMenuAdapter extends BaseAdapter{
    private LayoutInflater layoutInflater;
    private ArrayList<ListBeanMenu> listBeanMenus;

    public NoticeListMenuAdapter(Context context){
        layoutInflater = LayoutInflater.from(context);
    }

    public void setListBeanMenus(ArrayList<ListBeanMenu> listBeanMenus){
        this.listBeanMenus = listBeanMenus;
    }

    @Override
    public int getCount() {
        return listBeanMenus == null ? 0 : listBeanMenus.size() ;
    }

    @Override
    public Object getItem(int position) {
        return listBeanMenus.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final ListBeanMenu bean = listBeanMenus.get(position);
        InfoViewMenu infoView;
        if (convertView == null) {
            infoView = new InfoViewMenu();
            convertView = layoutInflater.inflate(R.layout.adapter_for_listview_notice_list_menu,parent,false);
            infoView.item = (TextView) convertView.findViewById(R.id.adp_for_lv_notice_list_menu_tv);
            convertView.setTag(infoView);
        }else {
            infoView = (InfoViewMenu) convertView.getTag();
        }

        infoView.item.setText(bean.getListViewItem());
        return convertView;
    }

}
