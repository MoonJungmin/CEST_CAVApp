package moon.urpcest_proto.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import moon.urpcest_proto.R;
import moon.urpcest_proto.activity.FindArduinoActivty;
import moon.urpcest_proto.activity.FindBeaconActivity;
import moon.urpcest_proto.global.mGlobal;
import moon.urpcest_proto.model.JoinusTCP_Model;
import moon.urpcest_proto.model.LoginTCP_Model;
import moon.urpcest_proto.utils.Util;


public class MemberFragment extends Fragment {

    /////////////////////////////////
    private LinearLayout mLoginPage;
    private TextView mTxt_LoginPageID;
    private TextView mTxt_LoginPagePW;
    private EditText mEdit_LoginPageID;
    private EditText mEdit_LoginPagePW;
    private Button mBtn_LoginPageLogin;
    /////////////////////////////////

    /////////////////////////////////
    private LinearLayout mMypage;
    private Button mBtn_MyPageLogin;
    private Button mBtn_MyPageJoinus;
    /////////////////////////////////

    /////////////////////////////////
    private LinearLayout mMypage2;
    private TextView mTxt_MyPage2ID;
    private TextView mTxt_MyPage2MyBeacon;
    private TextView mTxt_MyPage2Name;
    private TextView mTxt_MyPage2UserID;
    private TextView mTxt_MyPage2UserBeacon;
    private TextView mTxt_MyPage2UserName;
    /////////////////////////////////

    /////////////////////////////////
    private LinearLayout mJoinusPage;
    private TextView mTxt_JoinusPageID;
    private TextView mTxt_JoinusPagePW;
    private TextView mTxt_JoinusPageName;
    private EditText mEdit_JoinusPageID;
    private EditText mEdit_JoinusPagePW;
    private EditText mEdit_JoinusPageName;
    private Button mBtn_JoinusPageBeacon;
    private Button mBtn_JoinusPageArduino;
    private Button mBtn_JoinusPageJoinus;
    /////////////////////////////////

    /////////////////////////////////
    private static final int MYPAGE_PAGE = 0;
    private static final int LOGIN_PAGE = 1;
    private static final int JOINUS_PAGE = 2;
    public int mNowPage = 0;

    /////////////////////////////////

    private static final int CODE_FIND_BEACON = 0;
    private static final int CODE_FIND_ARDUINO = 1;

    mGlobal mGlobalVar = null;

    private String mBeaconAddr = "";
    private String mArduinoAddr = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mGlobalVar = (mGlobal)getActivity().getApplicationContext();

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_member, container, false);
        v = init(v);
        checkLogin();
        setClickListener();
        return v;
    }

    public void checkLogin(){
        if(!mGlobalVar.mMember.isLogin()){
            mMypage.setVisibility(View.VISIBLE);
            mLoginPage.setVisibility(View.GONE);
            mMypage2.setVisibility(View.GONE);
            mJoinusPage.setVisibility(View.GONE);
        }
        else {
            MyPageSet();
            if(mGlobalVar.mBtService.getState() != mGlobalVar.mBtService.STATE_CONNECTED)
            {
                mGlobalVar.mBtService.connectDevice(mGlobalVar.mMember.getmArduionAddr(), false);
            }
            mMypage2.setVisibility(View.VISIBLE);
            mLoginPage.setVisibility(View.GONE);
            mMypage.setVisibility(View.GONE);
            mJoinusPage.setVisibility(View.GONE);
        }
        mNowPage = MYPAGE_PAGE;
    }

    public void setClickListener(){
        mBtn_LoginPageLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mID = mEdit_LoginPageID.getText().toString();
                String mPW = mEdit_LoginPagePW.getText().toString();
                JSONObject jsonMember = new JSONObject();
                try {
                    jsonMember.put("FLAG", "2");
                    jsonMember.put("ID", mID);
                    jsonMember.put("PW", mPW);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                LoginTCP_Model tp = new LoginTCP_Model(jsonMember.toString(), mGlobalVar);
                tp.run();
                while(true)
                {
                    if(tp.endFlag == true)
                        break;
                }
                checkLogin();
                Util.hideKeyboard(getActivity().getApplicationContext(), mEdit_LoginPagePW);

            }
        });
        mBtn_MyPageLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMypage.setVisibility(View.GONE);
                mLoginPage.setVisibility(View.VISIBLE);
                mNowPage = LOGIN_PAGE;
            }
        });
        mBtn_MyPageJoinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMypage.setVisibility(View.GONE);
                mJoinusPage.setVisibility(View.VISIBLE);
                mNowPage = JOINUS_PAGE;
            }
        });
        mBtn_JoinusPageJoinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mID = mEdit_JoinusPageID.getText().toString();
                String mPW = mEdit_JoinusPagePW.getText().toString();
                String mName = mEdit_JoinusPageName.getText().toString();
                String mBeacon = mBeaconAddr;
                String mArduino = mArduinoAddr;
                JSONObject jsonMember = new JSONObject();
                try {
                    jsonMember.put("FLAG", "1");
                    jsonMember.put("ID", mID);
                    jsonMember.put("PW", mPW);
                    jsonMember.put("NAME", mName);
                    jsonMember.put("BEACON", mBeacon);
                    jsonMember.put("ADU", mArduino);
                    jsonMember.put("CREWID", "");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JoinusTCP_Model tp = new JoinusTCP_Model(jsonMember.toString(), mGlobalVar);
                tp.run();
                while(true)
                {
                    if(tp.endFlag == true)
                        break;
                }
                checkLogin();
            }
        });

        mBtn_JoinusPageBeacon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FindBeaconActivity.class);
                startActivityForResult(intent, CODE_FIND_BEACON);
            }
        });

        mBtn_JoinusPageArduino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FindArduinoActivty.class);
                startActivityForResult(intent, CODE_FIND_ARDUINO);
            }
        });
    }

    public View init(View v)
    {
        mLoginPage = (LinearLayout)v.findViewById(R.id.loginpage);
        mTxt_LoginPageID = (TextView)v.findViewById(R.id.loginpage_txt_id);
        mTxt_LoginPagePW = (TextView)v.findViewById(R.id.loginpage_txt_pw);
        mEdit_LoginPageID = (EditText)v.findViewById(R.id.loginpage_edit_id);
        mEdit_LoginPagePW = (EditText)v.findViewById(R.id.loginpage_edit_pw);
        mBtn_LoginPageLogin = (Button)v.findViewById(R.id.loginpage_btn_login);

        mMypage = (LinearLayout)v.findViewById(R.id.mypage);
        mBtn_MyPageLogin = (Button)v.findViewById(R.id.mypage_btn_login);
        mBtn_MyPageJoinus = (Button)v.findViewById(R.id.mypage_btn_joinus);

        mMypage2 = (LinearLayout)v.findViewById(R.id.mypage2);
        mTxt_MyPage2ID = (TextView)v.findViewById(R.id.mypage2_txt_id);
        mTxt_MyPage2MyBeacon = (TextView)v.findViewById(R.id.mypage2_txt_beacon);
        mTxt_MyPage2Name = (TextView)v.findViewById(R.id.mypage2_txt_name);
        mTxt_MyPage2UserID = (TextView)v.findViewById(R.id.mypage2_txt_user_id);
        mTxt_MyPage2UserBeacon = (TextView)v.findViewById(R.id.mypage2_txt_user_beacon);
        mTxt_MyPage2UserName = (TextView)v.findViewById(R.id.mypage2_txt_user_name);

        mJoinusPage = (LinearLayout)v.findViewById(R.id.joinuspage);
        mTxt_JoinusPageID = (TextView)v.findViewById(R.id.joinuspage_txt_id);
        mTxt_JoinusPagePW = (TextView)v.findViewById(R.id.joinuspage_txt_pw);
        mTxt_JoinusPageName = (TextView)v.findViewById(R.id.joinuspage_txt_name);
        mEdit_JoinusPageID = (EditText)v.findViewById(R.id.joinuspage_edit_id);
        mEdit_JoinusPagePW = (EditText)v.findViewById(R.id.joinuspage_edit_pw);
        mEdit_JoinusPageName = (EditText)v.findViewById(R.id.joinuspage_edit_name);
        mBtn_JoinusPageBeacon = (Button)v.findViewById(R.id.joinuspage_btn_beacon);
        mBtn_JoinusPageArduino = (Button)v.findViewById(R.id.joinuspage_btn_arduino);
        mBtn_JoinusPageJoinus = (Button)v.findViewById(R.id.joinuspage_btn_joinus);

        return v;
    }

    public void MyPageSet()
    {
        mTxt_MyPage2UserID.setText(mGlobalVar.mMember.getId());
        mTxt_MyPage2UserBeacon.setText(mGlobalVar.mMember.getBeaconAddr());
        mTxt_MyPage2UserName.setText(mGlobalVar.mMember.getName());
    }


    public boolean allowBackPressed(){
        if(mNowPage == 0)
            return true;
        else{
            checkLogin();
            return false;
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);

        switch(requestCode){
            case CODE_FIND_BEACON:
                Log.i("JM", "Beacon : " + intent.getExtras().getString("Beacon"));
                mBeaconAddr = intent.getExtras().getString("Beacon");
                break;
            case CODE_FIND_ARDUINO:
                Log.i("JM", "Arduino : " + intent.getExtras().getString("Arduino"));
                mArduinoAddr = intent.getExtras().getString("Arduino");
                break;

        }
    }
}