package moon.urpcest_proto.activity;

import android.content.Context;
import android.graphics.PointF;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.service.RangedBeacon;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.HashMap;

import moon.urpcest_proto.R;
import moon.urpcest_proto.datatype.TypeRiding;
import moon.urpcest_proto.global.mGlobal;
import moon.urpcest_proto.model.CrewCreateDeleteTCP_Model;
import moon.urpcest_proto.model.CrewJoinTCP_Model;
import moon.urpcest_proto.model.CrewPreStartTCP_Model;
import moon.urpcest_proto.model.LocateDataThread;
import moon.urpcest_proto.model.TimeThread;
import moon.urpcest_proto.view.PositionView;

public class RidingActivity extends BaseActivity implements BeaconConsumer, MapView.MapViewEventListener {

    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private boolean mNowTab = false;
    private double mSpeed = 0;
    private double mLongitude = 0;
    private double mLatitude = 0;
    public boolean StartFlag = false;
    private LocateDataThread mThread;
    private TimeThread mTimeThread;

    public MapView mMapView;

    public TextView mTextDistance;
    public TextView mTextVelocity;
    public TextView mTextTime;

    private FrameLayout mPositionLayout;
    private FrameLayout mMapViewLayout;
    private FrameLayout mContentsLayout;
    private PositionView mPositionView;

    public TextView mbeaconLog;
    public TextView mGPSLog;

    public mGlobal mGlobalVar = null;

    private RidingActivity mActivity;

    private Button mBtn_Start;
    private Button mBtn_Cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        setContentView(R.layout.activity_riding);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mGlobalVar = (mGlobal) getApplicationContext();

        RangedBeacon.setSampleExpirationMilliseconds(2000);
        mGlobalVar.beaconManager = BeaconManager.getInstanceForApplication(this);
        mGlobalVar.beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        //mGlobalVar.beaconManager.setForegroundBetweenScanPeriod(1000l);

        mThread = new LocateDataThread(mGlobalVar, this);
        mTimeThread = new TimeThread(mGlobalVar, this);

        mMapView = new MapView(this);
        mMapView.setDaumMapApiKey("a7c0b571ecc561953db6a3e9752ae0bd");
        mMapView.setHDMapTileEnabled(false);


        CountDownTimer mTimer = new CountDownTimer(1500, 1500) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
                mMapView.setZoomLevel(0, true);
            }
        }.start();


        final ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mMapView);
        mMapView.setMapViewEventListener(this);

        mPositionLayout = (FrameLayout) findViewById(R.id.position_lay);
        mPositionLayout.setVisibility(View.GONE);
        mPositionView = (PositionView) findViewById(R.id.position_view);

        mbeaconLog = (TextView) findViewById(R.id.txt_beaconlog);
        mGPSLog = (TextView) findViewById(R.id.txt_gpslog);
        mMapViewLayout = (FrameLayout) findViewById(R.id.mapview_lay);
        mContentsLayout = (FrameLayout) findViewById(R.id.mlayout);
        mContentsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("JM", "sigle tab!");
                if (mNowTab) {
                    mPositionView.hideView();
                    mPositionLayout.setVisibility(View.GONE);
                    mMapViewLayout.setVisibility(View.VISIBLE);
                }
                mNowTab = !mNowTab;
            }
        });

        mTextVelocity = (TextView) findViewById(R.id.txt_velocity);
        mTextDistance = (TextView) findViewById(R.id.txt_distance);
        mTextTime = (TextView) findViewById(R.id.txt_time);
        mBtn_Start = (Button) findViewById(R.id.btn_riding_start);
        mBtn_Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject putData = new JSONObject();
                try {
                    putData.put("FLAG", "9");
                    putData.put("CR_OWN_NO", mGlobalVar.mCrew.getmCrew_Owner_no().getIdx());
                    putData.put("CR_NO", mGlobalVar.mCrew.getmCrew_no());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                CrewPreStartTCP_Model tp = new CrewPreStartTCP_Model(putData.toString(), mGlobalVar);
                tp.run();
                while (true) {
                    if (tp.endFlag == true)
                        break;
                }
                //
                TypeRiding tmpRide = new TypeRiding(Integer.valueOf(mGlobalVar.mMember.getIdx()));
                tmpRide.setGPS(new PointF(0, 0));
                mGlobalVar.mRideData.add(tmpRide);
                for (int i = 0; i < mGlobalVar.mCrew.getmCrew_Members_no().size(); ++i) {
                    if (mGlobalVar.mCrew.getmCrew_Members_no().get(i).getIdx() == mGlobalVar.mMember.getIdx())
                        continue;
                    else {
                        HashMap<Integer, Double> tmp = new HashMap<Integer, Double>();
                        int key = Integer.valueOf(mGlobalVar.mCrew.getmCrew_Members_no().get(i).getIdx());
                        tmp.put(key, -1.0);
                    }
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                       mBtn_Start.setText("Stop");
                    }
                });
                StartFlag = true;


                mTimeThread.start();
                mThread.start();
            }
        });
        mBtn_Cancel = (Button) findViewById(R.id.btn_riding_cancel);
        mBtn_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartFlag = false;
                JSONObject putData = new JSONObject();
                if (mGlobalVar.mCrew.getmCrew_Owner_no().getIdx() == mGlobalVar.mMember.getIdx()) {
                    try {
                        putData.put("FLAG", "6");
                        putData.put("CR_OWNER_NO", mGlobalVar.mMember.getIdx());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    CrewCreateDeleteTCP_Model tp = new CrewCreateDeleteTCP_Model(putData.toString(), mGlobalVar, false);
                    tp.run();
                    while (true) {
                        if (tp.endFlag == true)
                            break;
                    }
                } else {
                    try {
                        putData.put("FLAG", "8");
                        putData.put("MB_NO", mGlobalVar.mMember.getIdx());
                        putData.put("CR_NO", mGlobalVar.mCrew.getmCrew_no());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    CrewJoinTCP_Model tp = new CrewJoinTCP_Model(putData.toString(), mGlobalVar);
                    tp.run();
                    while (true) {
                        if (tp.endFlag == true)
                            break;
                    }
                }

                finish();
            }
        });

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new SpeedoActionListener();
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, mLocationListener);
        //mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000, 1, mLocationListener);
        mGlobalVar.beaconManager.bind(this);

        mTextTime.setOnClickListener(new View.OnClickListener() { // 스로틀 테스트
            @Override
            public void onClick(View v) {
                //0X01 0X11 0X11 0X0D 스로틀
                //0X01 0X22 0X22 0X0D 브레이크
                byte[] tmp = new byte[4];
                tmp[0] = 0x01;
                tmp[1] = 0x11;
                tmp[2] = 0x11;
                tmp[3] = (byte) 0x0D;

                mGlobalVar.mBtService.mSendMessage(tmp);
            }
        });

        mTextDistance.setOnClickListener(new View.OnClickListener() { // 브레이크 테스트
            @Override
            public void onClick(View v) {
                //0X01 0X11 0X11 0X0D 스로틀
                //0X01 0X22 0X22 0X0D 브레이크
                byte[] tmp = new byte[4];
                tmp[0] = 0x01;
                tmp[1] = 0x22;
                tmp[2] = 0x22;
                tmp[3] = (byte) 0x0D;

                mGlobalVar.mBtService.mSendMessage(tmp);
            }
        });
    }

    @Override
    protected void onDestroy() {
        mGlobalVar.beaconManager.unbind(this);
        super.onDestroy();
    }


    @Override
    public void onMapViewInitialized(MapView mapView) {

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {
        if (StartFlag) {
            mPositionView.showView(mGlobalVar, mActivity);
            mPositionLayout.setVisibility(View.VISIBLE);
            mMapViewLayout.setVisibility(View.INVISIBLE);
            mNowTab = !mNowTab;
        }
    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int zoomLevel) {
    }

    @Override
    public void onBackPressed() {
        Log.i("JM", "onBackPressed!");
    }


    private class SpeedoActionListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                mSpeed = location.getSpeed() * 3.6f;
                Log.i("JM", "OnlocationChanged : " + mSpeed);
                mTextVelocity.setText(String.format("%.2f", mSpeed) + "Km/h");
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub

        }
    }


    @Override
    public void onBeaconServiceConnect() {

        Log.i("JM", "onBeaconServiceConnect");

        mGlobalVar.beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                HashMap<Integer, Double> tmp = new HashMap<Integer, Double>();
                if (StartFlag) {
                    Log.i("JM", "RangeNotifier! : " + mGlobalVar.mCrew.getmCrew_Members_no().size());
                    for (Beacon beacon : beacons) {
                        for (int i = 0; i < mGlobalVar.mCrew.getmCrew_Members_no().size(); ++i) {
                            if (mGlobalVar.mCrew.getmCrew_Members_no().get(i).getBeaconAddr().equals(beacon.getBluetoothAddress())) {
                                if (!mGlobalVar.mMember.getBeaconAddr().equals(beacon.getBluetoothAddress())) {
                                    int key = Integer.valueOf(mGlobalVar.mCrew.getmCrew_Members_no().get(i).getIdx());

                                    Log.i("JM", "mCrew: " + mGlobalVar.mCrew.getmCrew_Members_no().get(i).getBeaconAddr());
                                    Log.i("JM", "beacon : " + beacon.getBluetoothAddress());

                                    tmp.put(key, beacon.getDistance());
                                }
                            }

                        }
                    }
                    TypeRiding tmpRideData = mGlobalVar.mRideData.get(0);
                    tmpRideData.setBeaconLength(tmp);
                    mGlobalVar.mRideData.set(0, tmpRideData);
                }
            }
        });
        try {
            String uuid = "myBeacon";
            mGlobalVar.beaconManager.startRangingBeaconsInRegion(new Region(uuid, null, null, null));
        } catch (RemoteException e) {
        }

    }

}