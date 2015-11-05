package moon.urpcest_proto.model;

import moon.urpcest_proto.activity.RidingActivity;
import moon.urpcest_proto.global.mGlobal;

/**
 * Created by Moon on 2015-10-26.
 */
public class TimeThread extends Thread{

    int sumTime = 0;
    int sec;
    int min;
    int msec;

    mGlobal mGlobalVar;
    RidingActivity mActivity;

    public TimeThread(mGlobal mGlobalVar, RidingActivity aActivity) {
        this.mGlobalVar = mGlobalVar;
        this.mActivity = aActivity;
        sumTime = 0;
    }

    public void run() {
        while (mActivity.StartFlag) {
            try {

                sumTime += 1;
                sec = ( sumTime / 100 ) % 60;
                min = (sumTime / 6000);
                msec = (sumTime % 6000) % 100;
                mActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        mActivity.mTextTime.setText(String.format("%02d",min) + ":" + String.format("%02d",sec) + ":" + String.format("%02d",msec));
                    }
                });

                Thread.sleep(10);

            } catch (Exception e) {
            }
        }


    }
}

