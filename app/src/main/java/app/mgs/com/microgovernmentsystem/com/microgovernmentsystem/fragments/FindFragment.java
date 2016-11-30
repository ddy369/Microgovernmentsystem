package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.mgs.com.microgovernmentsystem.R;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.tools.Jumptoapp;


public class FindFragment extends Fragment {
//    public ImageTextButtonTool mibtEmail;
//    public ImageTextButtonTool mibtDoorWeb;
    private GridView commonToolView;
    private List<Map<String, Object>> c_data_list;
    private SimpleAdapter c_sim_adapter;

    private GridView governmentToolView;
    private List<Map<String, Object>> g_data_list;
    private SimpleAdapter g_sim_adapter;

    // 图片封装为一个数组
    private int[] Cicon = {R.drawable.ic_weixin_48dp,R.drawable.ic_microblog_48dp,R.drawable.ic_drops_taxi_48dp,
            R.drawable.ic_wps_48dp};
    private String[] CiconName = { "政务微信", "政务微博", "滴滴出行", "文档阅读"};

    private int[] Gicon = {R.drawable.ic_message1_48dp,R.drawable.ic_torch_48dp,R.drawable.ic_pimin_48dp};
    private String[] GiconName = { "电子邮箱", "门户网站","网民诉求"};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_find,container,false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initViews();

        //新建List
        c_data_list = new ArrayList<>();
        g_data_list = new ArrayList<>();
        //获取数据
        getCData();
        getGData();
        //新建适配器
        String [] from ={"image","text"};
        int [] to = {R.id.custom_itb_iv,R.id.custom_itb_tv};
        c_sim_adapter = new SimpleAdapter(getActivity(), c_data_list, R.layout.tool_image_text_button, from, to);
        //配置适配器
        commonToolView.setAdapter(c_sim_adapter);

        //新建适配器
        g_sim_adapter = new SimpleAdapter(getActivity(), g_data_list, R.layout.tool_image_text_button, from, to);
        //配置适配器
        governmentToolView.setAdapter(g_sim_adapter);

//        setImageTextButton();
        setOnClickListener();
    }

    public List<Map<String, Object>> getCData(){
        //cion和iconName的长度是相同的，这里任选其一都可以
        for(int i=0;i<Cicon.length;i++){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("image", Cicon[i]);
            map.put("text", CiconName[i]);
            c_data_list.add(map);
        }
        return c_data_list;
    }

    public List<Map<String, Object>> getGData(){
        //cion和iconName的长度是相同的，这里任选其一都可以
        for(int i=0;i<Gicon.length;i++){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("image", Gicon[i]);
            map.put("text", GiconName[i]);
            g_data_list.add(map);
        }
        return g_data_list;
    }

    private void setOnClickListener()
    {
        governmentToolView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i==0){
                    Intent viewIntent = new
                            Intent("android.intent.action.VIEW",Uri.parse("http://mail.lhdz.gov.cn/wap"));
                    startActivity(viewIntent);
                }else if(i==1){
                    Intent viewIntent = new
                            Intent("android.intent.action.VIEW", Uri.parse("http://www.lhdz.gov.cn"));
                    startActivity(viewIntent);
                }else{
                    Intent viewIntent = new
                            Intent("android.intent.action.VIEW", Uri.parse("http://61.136.74.221/lyssq"));
                    startActivity(viewIntent);
                }
            }
        });
        commonToolView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i==0){
                    Jumptoapp.jump(getActivity(),"com.tencent.mm");
                }else if (i==1){
                    Intent viewIntent = new
                            Intent("android.intent.action.VIEW", Uri.parse("http://weibo.com/lhdz?is_hot=1"));
                    startActivity(viewIntent);
                }else if (i==2){
                    Jumptoapp.jump(getActivity(),"com.sdu.didi.psnger");
                }else {
                    Jumptoapp.jump(getActivity(),"cn.wps.moffice_eng");
                }
            }
        });
    }
    private void initViews(){
//        mibtEmail = (ImageTextButtonTool) getActivity().findViewById(R.id.act_find_ibt_email);
//        mibtDoorWeb = (ImageTextButtonTool) getActivity().findViewById(R.id.act_find_ibt_doorWeb);
        commonToolView = (GridView) getActivity().findViewById(R.id.frg_find_gv_common_tool);
        governmentToolView = (GridView) getActivity().findViewById(R.id.frg_find_gv_government_tool);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("TextMode",0);
        int TextMode = sharedPreferences.getInt("TextMode",1);
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int widthPixels = metrics.widthPixels;
        if (TextMode < 3){
//            commonToolView.setColumnWidth(widthPixels/5);
//            governmentToolView.setColumnWidth(widthPixels/5);
            commonToolView.setNumColumns(4);
            governmentToolView.setNumColumns(4);
        }
        else{
//            commonToolView.setColumnWidth(widthPixels/4);
//            governmentToolView.setColumnWidth(widthPixels/4);
            commonToolView.setNumColumns(3);
            governmentToolView.setNumColumns(3);
        }
    }

//    private void setImageTextButton(){
//        mibtEmail.setImageResource(R.drawable.ic_message1_48dp);
//        mibtDoorWeb.setImageResource(R.drawable.ic_torch_48dp);
//        mibtEmail.setTextViewText("电子邮箱");
//        mibtDoorWeb.setTextViewText("门户网站");
//    }

}
