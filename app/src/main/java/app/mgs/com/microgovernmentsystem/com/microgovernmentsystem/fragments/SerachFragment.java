package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.fragments;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import app.mgs.com.microgovernmentsystem.R;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.ContactsInformationActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.adapters.MaiListserachAdapter;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.Contacts;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Datahelper;

/**
 * A simple {@link Fragment} subclass.
 */
public class SerachFragment extends Fragment {

    ListView mListView;
    ArrayList<Contacts> mData = new ArrayList<>();
    List<Contacts> contactses;
    MaiListserachAdapter adapter;
    public SerachFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_serach, container, false);
    }
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Datahelper datahelper=new Datahelper();
        contactses =datahelper.getusers(new Datahelper.callback() {
            @Override
            public void callback(List<Contacts> contactses) {

            }
        });
        set_mListView_adapter();//给listview控件添加一个adapter

    }
    /**
     * 设置ListView的Adapter
     */
    private void set_mListView_adapter()
    {
        mListView = (ListView) getActivity().findViewById(R.id.SerachListView);

        getmData(mData);

        adapter = new MaiListserachAdapter(getActivity(),R.layout.adapter_for_listview_serach_list,mData);

        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Contacts contacts = mData.get(position);
                Intent intent = new Intent();
                intent.setClass(getActivity(),ContactsInformationActivity.class);
                intent.putExtra("contacts", contacts);

                startActivity(intent);
              //  getActivity().finish();
            }
        });
    }
    /**
     * 获得根据搜索框的数据data来从元数据筛选，筛选出来的数据放入mDataSubs里
     * @param mDataSubs
     * @param data
     */

    private void getmDataSub(ArrayList<Contacts> mDataSubs, String data)
    {
        int length = contactses.size();
        for(int i = 0; i < length; ++i){
            if(contactses.get(i).name.contains(data) ||
                    contactses.get(i).mobile_phone.contains(data)||
                    contactses.get(i).tel.contains(data)){
                Contacts item = contactses.get(i);
                mDataSubs.add(item);
            }
        }
    }



    /**
     * 获得元数据 并初始化mDatas
     * @param mDatas
     */

    private void getmData(ArrayList<Contacts> mDatas)
    {
        Contacts item;
        for(Contacts contacts : contactses)
        {
            item = contacts;
            mDatas.add(item);
        }

    }
    public  void  setdata(String data)
    {
        mData.clear();//先要清空，不然会叠加

        getmDataSub(mData, data);//获取更新数据

        adapter.notifyDataSetChanged();//更新
    }


}



