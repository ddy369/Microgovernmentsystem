package app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import app.mgs.com.microgovernmentsystem.R;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.AboutActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.FeedbackActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.activities.SettingActivity;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.daos.GetUserInformationDao;
import app.mgs.com.microgovernmentsystem.com.microgovernmentsystem.operations.ExitLogOperation;
import cn.jpush.android.api.JPushInterface;

public class MeFragment extends Fragment {
    private TextView mtvName;
    private TextView mtvPosition;
    private TextView mtvJob;
    private RelativeLayout mrlMid;
    private RelativeLayout mrlBottom;
    private RelativeLayout mrlFeedBack;
  //  private RelativeLayout mrldeletecache;
    private Button mbtExit;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_me, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initViews();

        setOnClickListener();

        setUserInformationText();//得到用户信息并给tv赋值
    }

    private void initViews() {
        mtvName = (TextView) getActivity().findViewById(R.id.frg_me_tv_name);
        mtvPosition = (TextView) getActivity().findViewById(R.id.frg_me_tv_position);
        mtvJob = (TextView) getActivity().findViewById(R.id.frg_me_tv_job);
        mrlMid = (RelativeLayout) getActivity().findViewById(R.id.frg_me_rl_mid);
        mrlBottom = (RelativeLayout) getActivity().findViewById(R.id.frg_me_rl_bottom);
        mrlFeedBack = (RelativeLayout) getActivity().findViewById(R.id.frg_me_rl_feedback);
        mbtExit = (Button) getActivity().findViewById(R.id.frg_me_bt_log_exit);
    //    mrldeletecache = (RelativeLayout) getActivity().findViewById(R.id.frg_me_rl_deletecache);
    }

    private void setOnClickListener() {
        final Intent intent = new Intent();
        mrlBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.setClass(getActivity(), SettingActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        mrlMid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.setClass(getActivity(), AboutActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        mrlFeedBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.setClass(getActivity(), FeedbackActivity.class);
                startActivity(intent);
                getActivity().finish();

            }
        });

        mbtExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EMClient.getInstance().logout(true, new EMCallBack() {

                    @Override
                    public void onSuccess() {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onProgress(int progress, String status) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onError(int code, String message) {
                        // TODO Auto-generated method stub

                    }
                });


                ExitLogOperation operation = new ExitLogOperation();
                JPushInterface.setAlias(getActivity(), null, null);
                operation.exitLog(getActivity());


            }
        });
    }

    void setUserInformationText() {
        GetUserInformationDao guid = new GetUserInformationDao();
        mtvName.setText(guid.getUserName());
        mtvPosition.setText(guid.getPosition());
    }

}
