package moon.urpcest_proto.model;

import android.graphics.PointF;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import moon.urpcest_proto.activity.RidingActivity;
import moon.urpcest_proto.global.mGlobal;
import moon.urpcest_proto.utils.VectorUtil;

/**
 * Created by Moon on 2015-09-18.
 */
public class LocateDataThread extends Thread{

    mGlobal mGlobalVar;
    RidingActivity mActivity;
    ArrayList<Double> latitudeList;
    ArrayList<Double> longitudeList;
    float distanceSUM = 0;
    int firstFlag = 0;

    public LocateDataThread(mGlobal mGlobalVar, RidingActivity aActivity) {
        this.mGlobalVar = mGlobalVar;
        this.mActivity = aActivity;
        latitudeList = new ArrayList<Double>();
        longitudeList = new ArrayList<Double>();
    }

    public void run() {
        while(mActivity.StartFlag){
            Log.i("JM", "Send Locate Data");
            JSONObject putData = new JSONObject();
            try {
                double latitude = mActivity.mMapView.getMapCenterPoint().getMapPointGeoCoord().latitude;
                double longitude = mActivity.mMapView.getMapCenterPoint().getMapPointGeoCoord().longitude;
                latitudeList.add(latitude);
                longitudeList.add(longitude);
                if(firstFlag != 10)
                    firstFlag++;
                else
                    distanceSUM += getMyDistance();

                mActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        mActivity.mTextDistance.setText(String.format("%.2f", distanceSUM) + "m");
                    }
                });


                Log.i("JM", "distanceSUM" + distanceSUM);
                mGlobalVar.mRideData.get(0).setGPS(new PointF((float)latitude, (float)longitude));
                putData.put("FLAG", "7");
                putData.put("STOP_FLAG", "0");
                putData.put("ME_NO", mGlobalVar.mMember.getIdx());
                putData.put("CR_NO", mGlobalVar.mCrew.getmCrew_no());
                JSONObject locationData = new JSONObject();
                locationData.put("GPS_x", mGlobalVar.mRideData.get(0).getGPS().x);
                locationData.put("GPS_y", mGlobalVar.mRideData.get(0).getGPS().y);
                for (int i = 0; i < mGlobalVar.mCrew.getmCrew_Members_no().size(); ++i) {
                    String key = mGlobalVar.mCrew.getmCrew_Members_no().get(i).getIdx();
                    locationData.put(key, mGlobalVar.mRideData.get(0).getBeaconLength().get(Integer.valueOf(key)));
                }
                putData.put("LOCATE_DATA", locationData.toString());
                Thread.sleep(500);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (InterruptedException e){
                Log.i("JM", "Sleep error");
            }

            CrewStartTCP_Model tp2 = new CrewStartTCP_Model(putData.toString(), mGlobalVar);
            tp2.run();
        }

    }

    private double getMyDistance()
    {
        double srcX, srcY, dstX, dstY;
        srcX = latitudeList.get(latitudeList.size()-2);
        srcY = longitudeList.get(longitudeList.size()-2);
        dstX = latitudeList.get(latitudeList.size()-1);
        dstY = longitudeList.get(longitudeList.size()-1);

        float distance = VectorUtil.calDistance((float)srcX, (float)srcY, (float)dstX, (float)dstY);
        return distance;
    }

}