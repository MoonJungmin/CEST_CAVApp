package moon.urpcest_proto.global;

import android.app.Application;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import org.altbeacon.beacon.BeaconManager;

import java.util.ArrayList;

import moon.urpcest_proto.datatype.TypeCrew;
import moon.urpcest_proto.datatype.TypeMember;
import moon.urpcest_proto.datatype.TypeRideOptimal;
import moon.urpcest_proto.datatype.TypeRiding;
import moon.urpcest_proto.model.BluetoothService;

/**
 * Created by Moon on 2015-08-24.
 */
public class mGlobal extends Application {

    public int mWidth;
    public int mHeight;

    public TypeMember mMember;
    public ArrayList<TypeCrew> mCrewList;
    public TypeCrew mCrew;
    public ArrayList<TypeRiding> mRideData;
    public BluetoothService mBtService = null;
    public String mBtStatus;
    public Handler mBtGHander = null;
    public TypeRideOptimal mOptimalData;
    public ArrayList<TypeRideOptimal> mStackOptimalData;


    public BeaconManager beaconManager;

    public void initCrew(){
        mCrew = new TypeCrew();
    }

    public void initMember(){
        mMember = new TypeMember();
    }

    public void initCrewList(){
        mCrewList = new ArrayList<TypeCrew>();
    }

    public void initRideData(){
       mRideData = new ArrayList<TypeRiding>();
    }

    public void initOptimalData(){
        mOptimalData = new TypeRideOptimal();
    }

    public void initStackOptimalData(){
        mStackOptimalData = new ArrayList<TypeRideOptimal>();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (mBtService == null) {
            mBtService = new BluetoothService(mHandler, getApplicationContext());
        }
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(mBtGHander != null){
                Log.i("JM", "msg: " + msg.toString());
                Message msg2 = new Message();
                msg2.arg1 = msg.arg1;
                msg2.what = msg.what;
                msg2.obj = msg.obj;
                mBtGHander.sendMessage(msg2);
            }
            switch (msg.what) {
                case BluetoothService.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            setStatus("connected");
                            if (mBtService.lastMAC != mBtService.lastTryMAC) {
                                mBtService.lastMAC = mBtService.lastTryMAC;
                                mBtService.configParaSave();
                            }
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            setStatus("connecting...");
                            break;
                        case BluetoothService.STATE_LISTEN:
                            setStatus("listen...");
                            break;
                        case BluetoothService.STATE_NONE:
                            setStatus("none");
                            break;
                    }
                    break;
                case BluetoothService.MESSAGE_WRITE:
                    break;
                case BluetoothService.MESSAGE_READ:
                    break;

				/**/
                case BluetoothService.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mBtService.mConnectedDeviceName = msg.getData().getString(BluetoothService.DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to " + mBtService.mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothService.MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(BluetoothService.TOAST), Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    private final void setStatus(String status) {
        mBtStatus = status;
    }

}
