package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.List;

import app.mgs.com.microgovernmentsystem.R;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.ContactsInformationActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.adapters.SortByDepartmentAdapter;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.Contacts;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Datahelper;


public class SortByDepartmentFragment extends Fragment {
    private ExpandableListView melvContacts = null;
    private List<Contacts> contactses;
    private  Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg)
        {
            if(msg.what==0)
                setExpandaListView();
        }
    };
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contacts_sort_by_department,container,false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        contactses =new ArrayList<>();
        initView();
        initdata();
    }

    private void initView(){
        melvContacts = (ExpandableListView) getActivity().findViewById(R.id.frg_sort_by_department_list_elv);
    }
    private void initdata()
    {
        final boolean i=false;
        final ProgressDialog progressDialog = new ProgressDialog
                (getActivity());
        progressDialog.setTitle("获取联系人中...");
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Datahelper datahelper=new Datahelper();
        contactses = datahelper.getusers(new Datahelper.callback() {
            @Override
            public void callback(List<Contacts> users1) {
                contactses =users1;

                mHandler.sendEmptyMessageDelayed(0,0);
                progressDialog.dismiss();

            }
        });


    }
    public void setExpandaListView(){
        //创建一个BaseExpandableListAdapter对象
        if(contactses.size()!=0)
        {
            final ExpandableListAdapter adapter = new SortByDepartmentAdapter(contactses, getActivity());
            melvContacts.setAdapter(adapter);

            melvContacts.setGroupIndicator(null);

            melvContacts.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                    for (int j = 0; j < adapter.getGroupCount(); j++) {
                        if (j != i)
                            melvContacts.collapseGroup(j);
                    }
                    if (!melvContacts.isGroupExpanded(i)) {
                        melvContacts.expandGroup(i);
                        melvContacts.setSelectedGroup(i);
                    } else
                        melvContacts.collapseGroup(i);
                    return true;
                }
            });

            melvContacts.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), ContactsInformationActivity.class);
                    intent.putExtra("contacts", (Contacts) adapter.getChild(i, i1));

                    startActivity(intent);
                    //    getActivity().finish();
                    return true;
                }
            });
        }
    }
    @Override
    public void onResume() {
        super.onResume();

      //  initdata();
    }
}
