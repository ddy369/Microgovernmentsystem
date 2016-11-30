package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import app.mgs.com.microgovernmentsystem.R;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.ListBeanMenu;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.MemorandumListBean;


public class MemorandumAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private ArrayList<MemorandumListBean> MemorandumListBeans;
    private int del;

    public MemorandumAdapter(Context context,int i) {
        layoutInflater = LayoutInflater.from(context);
        this.del = i;
    }

    public void setListBeans(ArrayList<MemorandumListBean> listBeans){
        this.MemorandumListBeans = listBeans;
    }

    @Override
    public int getCount() {
        return MemorandumListBeans == null ? 0 : MemorandumListBeans.size() ;
    }

    @Override
    public Object getItem(int position) {
        return MemorandumListBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final MemorandumListBean bean = MemorandumListBeans.get(position);
        InfoViewMemorandum infoView;
        if (convertView == null) {
            infoView = new InfoViewMemorandum();
            convertView = layoutInflater.inflate(R.layout.adapter_for_listview_memorandum,parent,false);
            infoView.tvTitle = (TextView) convertView.findViewById(R.id.adp_for_lv_memorandum_tv_title);
            infoView.tvTime = (TextView) convertView.findViewById(R.id.adp_for_lv_memorandum_tv_time);
            infoView.ivDelete = (ImageView) convertView.findViewById(R.id.adp_for_lv_memorandum_iv_delete);
            convertView.setTag(infoView);
        }else {
            infoView = (InfoViewMemorandum) convertView.getTag();
        }

        String time = bean.getYear()+"年"+bean.getMonth()+"月"+bean.getDay()+"日"
                +"  "+bean.getHour()+"时"+bean.getMinute()+"分";
        infoView.tvTime.setText(time);
        infoView.tvTitle.setText(bean.getTitle());
        if (del == 1){
            infoView.ivDelete.setVisibility(View.VISIBLE);
        }
        return convertView;
    }
}
