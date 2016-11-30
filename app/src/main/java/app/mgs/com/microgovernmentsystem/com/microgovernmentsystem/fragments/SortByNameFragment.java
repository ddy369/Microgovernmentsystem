package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.List;

import app.mgs.com.microgovernmentsystem.R;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.ContactsInformationActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.adapters.SortByNameAdapter;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.listbeans.Contacts;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.ChineseToSpell;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Datahelper;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.SideBar;

/**
 * Created by mqs on 2016/7/22.
 */
public class SortByNameFragment extends Fragment {

    private SideBar mSideBar;
  //  private PinYinComparator pinYinComparator;
    String buff="A";
    private ChineseToSpell chineseToSpell;
    private TextView mdialog;
    private ExpandableListView mSortbyNameElv = null ;

    private String[] department = new String[]{ "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z" };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_contacts_sort_by_name,container,false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        init();
        initView();
        setExpandaListView();
        mSideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener(){
            @Override
            public void onTouchingLetterChanged(String s){
                int i,j,k;
                i = chineseToSpell.getPinYinSCII(s);
                j = chineseToSpell.getPinYinSCII(buff);
                k = chineseToSpell.getMul(i,j);
                mSortbyNameElv.setSelectedGroup(k);
            }
        });
    }
    private void initView(){

    }
    private void init() {
        mSortbyNameElv = (ExpandableListView) getActivity().findViewById(R.id.frag_sort_by_name_list_elv);
        mSideBar = (SideBar) getActivity().findViewById(R.id.frag_sort_by_name_sidebar);
        chineseToSpell = new ChineseToSpell();
        mdialog = (TextView) getActivity().findViewById(R.id.frag_sort_by_name__tv_dialog);
        mSideBar.setTextView(mdialog);
    }

    public void setExpandaListView(){
        //创建一个BaseExpandableListAdapter对象
        Datahelper datahelper= new Datahelper();
        final SortByNameAdapter adapter=new SortByNameAdapter(datahelper.getusersforname(new Datahelper.callback() {
            @Override
            public void callback(List<Contacts> users) {

            }
        }),getActivity());
        //adapter.setSideBar(mSideBar);
        mSortbyNameElv.setAdapter(adapter);

        mSortbyNameElv.setGroupIndicator(null);

        mSortbyNameElv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                Intent intent = new Intent();
                intent.setClass(getActivity(),ContactsInformationActivity.class);
                intent.putExtra("contacts",(Contacts)adapter.getChild(i,i1));

                startActivity(intent);
              //  getActivity().finish();
                return true;
            }
        });

        for (int i = 0; i < department.length; i++) {
            mSortbyNameElv.expandGroup(i);
        }

        mSortbyNameElv.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                return true;
            }
        });
    }
}
