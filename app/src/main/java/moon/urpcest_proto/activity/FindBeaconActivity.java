package moon.urpcest_proto.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.RemoteException;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;

import moon.urpcest_proto.R;

public class FindBeaconActivity extends BaseActivity implements BeaconConsumer {

    private BeaconManager beaconManager;
    private double minDistance = 9999;
    private String minDistance_Name = "";
    private String minDistance_MAC = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_beacon);
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconManager.bind(this);

        new CountDownTimer(7000, 1000){
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                Bundle extra = new Bundle();
                Intent intent = new Intent();
                extra.putString("Beacon", minDistance_MAC);
                intent.putExtras(extra);
                setResult(RESULT_OK, intent);
                finish();
            }
        }.start();

    }

    @Override
    public void onBackPressed() {
        Bundle extra = new Bundle();
        Intent intent = new Intent();
        extra.putString("Beacon", minDistance_MAC);
        intent.putExtras(extra);
        setResult(RESULT_OK, intent);
        finish();

        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    @Override
    public void onBeaconServiceConnect() {

        Log.i("JM", "onBeaconServiceConnect");

        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                Log.i("JM", "didRangeBeaconsInRegion : " + beacons.size());
                for (Beacon beacon : beacons) {
                    double distance = beacon.getDistance();
                    if(minDistance > beacon.getDistance()) {
                        minDistance = distance;
                        minDistance_MAC = beacon.getBluetoothAddress();
                        minDistance_Name = beacon.getBluetoothName();
                    }
                }

            }
        });

        try {
            String uuid = "myBeacon";
            beaconManager.startRangingBeaconsInRegion(new Region(uuid, null, null, null));
        }
        catch (RemoteException e) {    }

    }
}
