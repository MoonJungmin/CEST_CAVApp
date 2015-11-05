package moon.urpcest_proto.datatype;

import android.graphics.PointF;

import java.util.HashMap;

/**
 * Created by Moon on 2015-09-23.
 */
public class TypeRideOptimal {

    public int mGpsStatus = 0;
    public HashMap<Integer, Integer> mIndexTable = new HashMap<Integer, Integer>();
    private double[][] mBeaconLengthTable;
    private PointF[][] mGpsVectorTable;
    private HashMap<Integer, PointF> mGPSPoint = new HashMap<Integer, PointF>();


    public PointF[][] getmGpsVectorTable() {
        return mGpsVectorTable;
    }
    public void setmGpsVectorTable(PointF[][] mGpsVectorTable) {
        this.mGpsVectorTable = mGpsVectorTable;
    }
    public double[][] getmBeaconLengthTable() {
        return mBeaconLengthTable;
    }
    public void setmBeaconLengthTable(double[][] mBeaconLengthTable) {
        this.mBeaconLengthTable = mBeaconLengthTable;
    }

    public HashMap<Integer, PointF> getmGPSPoint() {
        return mGPSPoint;
    }

    public void setmGPSPoint(HashMap<Integer, PointF> mGPSPoint) {
        this.mGPSPoint = mGPSPoint;
    }
}
