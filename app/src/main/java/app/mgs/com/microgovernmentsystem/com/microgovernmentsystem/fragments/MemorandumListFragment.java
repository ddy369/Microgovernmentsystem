package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.fragments;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import app.mgs.com.microgovernmentsystem.R;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.MemorandumTagActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.adapters.MemorandumAdapter;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.SetMemorandumDao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.MemorandumListBean;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.AlarmReceiver;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.MemorandumListDatahelper;


public class MemorandumListFragment extends Fragment {

    private ArrayList<MemorandumListBean> mListBean;

    private ListView mlvMemorandum;
    private TextView mtvDelete;
    private TextView mtvCancel;

    private int isDel = 0;//设置按钮来判断当前状态

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_memorandum_list,container,false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        initViews();

        setOnClickListener();
    }

    private void initViews(){
        mlvMemorandum = (ListView) getActivity().findViewById(R.id.frg_memorandum_list_lv);
        mtvDelete = (TextView) getActivity().findViewById(R.id.act_memorandum_tv_delete);
        mtvCancel = (TextView) getActivity().findViewById(R.id.act_memorandum_tv_cancel);

        setListView();//初始化列表
    }

    private void setOnClickListener(){
        mtvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isDel == 0){
                    mtvDelete.setText("取消");
                    MemorandumAdapter adapter = new MemorandumAdapter(getActivity(),1);
                    adapter.setListBeans(arrayListBeans());
                    mlvMemorandum.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    isDel = 1;
                }else {
                    mtvDelete.setText("删除");
                    MemorandumAdapter adapter = new MemorandumAdapter(getActivity(),0);
                    adapter.setListBeans(arrayListBeans());
                    mlvMemorandum.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    isDel = 0;
                }
            }
        });

        mtvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mtvCancel.setVisibility(View.GONE);
                mtvDelete.setVisibility(View.VISIBLE);
            }
        });

    }

    private void setListView(){
        mListBean = arrayListBeans();
        MemorandumAdapter adapter = new MemorandumAdapter(getActivity(),0);
        adapter.setListBeans(mListBean);
        mlvMemorandum.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        mlvMemorandum.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (isDel == 0){
                    Intent intent = new Intent();
                    intent.putExtra("id",mListBean.get(position).getId());//获取当前记录的记录号，传到notice
                    intent.setClass(getActivity(),MemorandumTagActivity.class);
                    startActivity(intent);
//                    getActivity().finish();
                }else {
                    int id = mListBean.get(position).getId();//获取对应id,用来删除相应的条目
                    SetMemorandumDao dao = new SetMemorandumDao();
                    dao.deleteMemorandum(id);//调用删除方法进行删除

                    //将已经设置的通知取消
                    Intent intent1 = new Intent(getActivity(), AlarmReceiver.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(),
                            id ,intent1,0);
                    AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                    alarmManager.cancel(pendingIntent);

                    MemorandumAdapter adapter = new MemorandumAdapter(getActivity(),1);
                    mListBean = arrayListBeans();//重置列表数据，此处必须重置，否则会得到已经删
                    // 除过的id导致某些数据无法删除
                    adapter.setListBeans(mListBean);//重置列表
                    mlvMemorandum.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    //判断一下是否列表被清空，如果被清空则由删除状态自动变为普通状态
                    //在列表空状态下，无需再保持删除状态
                    //TODO:在列表为空时，跳转到添加事件页面
                    if (mlvMemorandum.getCount()==0){
                        mtvDelete.setText("删除");
                        isDel = 0;
                    }
                }
            }
        });
    }

    //从本地去出数据放入listbean中
    private ArrayList<MemorandumListBean> arrayListBeans(){
        MemorandumListDatahelper datahelper = new MemorandumListDatahelper();
        return datahelper.setListBeans();
    }
}
