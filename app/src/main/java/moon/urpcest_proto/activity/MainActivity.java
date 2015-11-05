package moon.urpcest_proto.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import moon.urpcest_proto.R;
import moon.urpcest_proto.fragment.MemberFragment;
import moon.urpcest_proto.fragment.RecordFragment;
import moon.urpcest_proto.fragment.RidingFragment;
import moon.urpcest_proto.global.mGlobal;
import moon.urpcest_proto.model.BluetoothService;
import moon.urpcest_proto.utils.Util;


public class MainActivity extends BaseActivity {

    FragmentManager manager;
    FragmentTransaction trans;
    MemberFragment fa;
    RidingFragment fb = new RidingFragment();
    RecordFragment fc = new RecordFragment();

    public mGlobal mGlobalVar= null;

    private static final int MEMBER_PAGE = 0;
    private static final int RIDING_PAGE = 1;
    private static final int RECORD_PAGE = 2;
    public int mNowPage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGlobalVar = (mGlobal)getApplicationContext();
        mGlobalVar.mHeight = Util.getScreenHeight(this);
        mGlobalVar.mWidth = Util.getScreenWidth(this);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        mGlobalVar.initMember();
        mGlobalVar.initCrew();
        mGlobalVar.initRideData();
        mGlobalVar.initOptimalData();
        mGlobalVar.initStackOptimalData();
        Log.i("JM", "MainStart");
        setContentView(R.layout.activity_main);
        startActivity(new Intent(this,SplashActivity.class));

        Button btn_a = (Button)findViewById(R.id.btn_member);
        Button btn_b = (Button)findViewById(R.id.btn_riding);
        Button btn_c = (Button)findViewById(R.id.btn_record);


   //   tcpClientModel tp = new tcpClientModel("test!!!!!");
   //   tp.run();
        Log.i("JM", "fragstart");
        manager = getFragmentManager();
        fa = (MemberFragment)manager.findFragmentById(R.id.view_fragment);

        Log.i("JM", "fragend");
        btn_a.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //fragment_a호출
                trans = manager.beginTransaction();
                trans.replace(R.id.view_fragment, fa);
                trans.commit();
                mNowPage = MEMBER_PAGE;
            }
        });
        btn_b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //fragment_a호출
                if(mGlobalVar.mMember.isLogin()){
                    trans = manager.beginTransaction();
                    trans.replace(R.id.view_fragment, fb);
                    trans.commit();
                    mNowPage = RIDING_PAGE;
                }
            }
        });
        btn_c.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //fragment_a호출
                if(mGlobalVar.mMember.isLogin()) {
                    trans = manager.beginTransaction();
                    trans.replace(R.id.view_fragment, fc);
                    trans.commit();
                    mNowPage = RECORD_PAGE;
                }
            }
        });

        mGlobalVar.mBtService.setBluetooth();
        if (mGlobalVar.mBtService != null) {
            if (mGlobalVar.mBtService.getState() == BluetoothService.STATE_NONE || mGlobalVar.mBtService.getState() == BluetoothService.STATE_LISTEN) {
                mGlobalVar.mBtService.start();
            }
        }

        mGlobalVar.mBtService.setBluetoothFind();


    }

    @Override
    public void onBackPressed() {

        switch (mNowPage)
        {
            case MEMBER_PAGE:
                if (fa.allowBackPressed()) {

                    super.onBackPressed();
                }
                break;
            case RIDING_PAGE:
                if (fb.allowBackPressed()) {

                    super.onBackPressed();
                }
                break;
            case RECORD_PAGE:
                if (fc.allowBackPressed()) {

                    super.onBackPressed();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        mGlobalVar.mBtService.mPairedDevicesAddress.clear();
        mGlobalVar.mBtService.mPairedDevicesName.clear();
        mGlobalVar.mBtService.mNewDevicesAddress.clear();
        mGlobalVar.mBtService.mNewDevicesName.clear();
        android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy();
    }

}
