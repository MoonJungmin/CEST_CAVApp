package moon.urpcest_proto.utils;

import android.graphics.PointF;

/**
 * Created by Moon on 2015-09-23.
 */
public class VectorUtil {

    public static PointF getVector(PointF a, PointF b)
    {
        PointF dst = new PointF();
        dst.set(b.x-a.x, b.y-a.y);
        return dst;
    }

    public static PointF getUnitVector(PointF src){
        PointF dst = new PointF();
        double length = getScalar(src);
        dst.set((float)(src.x/length), (float)(src.y/length));
        return dst;
    }

    public static double getScalar(PointF src){
        return Math.sqrt(Math.pow(src.x, 2)+Math.pow(src.y,2));
    }

    public static PointF VecMulScalar(PointF src, float a)
    {
        PointF dst = new PointF();
        dst.set((float)src.x*a, (float)src.y*a);
        return dst;
    }

    public static float getBetweenRadian(PointF a, PointF b)
    {
        return (a.x*b.x + a.y*b.y)/(float)(getScalar(a)*getScalar(b));
    }



    public static float calDistance(float lat1, float lon1, float lat2, float lon2){

        double theta, dist;
        theta = lon1 - lon2;
        dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);

        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;    // 단위 mile 에서 km 변환.
        dist = dist * 1000.0;      // 단위  km 에서 m 로 변환

        return (float)dist;
    }

    // 주어진 도(degree) 값을 라디언으로 변환
    public static double deg2rad(double deg){
        return (double)(deg * Math.PI / (double)180d);
    }

    // 주어진 라디언(radian) 값을 도(degree) 값으로 변환
    public static double rad2deg(double rad){
        return (double)(rad * (double)180d / Math.PI);
    }
}

