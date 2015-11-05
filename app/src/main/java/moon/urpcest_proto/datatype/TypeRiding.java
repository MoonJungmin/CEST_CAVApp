package moon.urpcest_proto.datatype;

import android.graphics.PointF;

import java.util.HashMap;

/**
 * Created by Moon on 2015-09-15.
 */
public class TypeRiding {

    public int Index;

    private PointF GPS = new PointF();

    private HashMap<Integer,Double> BeaconLength = new HashMap<Integer,Double>();

    public PointF getGPS() {
        return GPS;
    }

    public void setGPS(PointF GPS) {
        this.GPS = GPS;
    }

    public TypeRiding(int aIndex) {
        this.Index = aIndex;
    }

    public HashMap<Integer, Double> getBeaconLength() {
        return BeaconLength;
    }

    public void setBeaconLength(HashMap<Integer, Double> beaconLength) {
        BeaconLength = beaconLength;
    }



}
