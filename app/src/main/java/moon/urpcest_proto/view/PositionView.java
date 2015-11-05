package moon.urpcest_proto.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import java.util.HashMap;

import moon.urpcest_proto.activity.RidingActivity;
import moon.urpcest_proto.global.mGlobal;
import moon.urpcest_proto.utils.Util;
import moon.urpcest_proto.utils.VectorUtil;

public class PositionView extends ImageView {

    private RidingActivity mActivity;
    private Bitmap mCanvasBitmap;
    private Canvas mCanvas = new Canvas();
    private boolean isDraw = false;
    private boolean stoploading = false;
    public mGlobal mGlobalVar = null;
    public boolean beaconStatus = false;
    private int arduinoFlag = 0;
    private double mSUMDistance = 0;
    private float mDistanceTotal = 0;
    private int mDistanceCount = 0;
    Paint paint = new Paint();

    private boolean beaconFlag = false;

    public PositionView(Context context) {
        super(context);
    }

    public PositionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PositionView(Context context, AttributeSet attrs, RidingActivity mActivity) {
        super(context, attrs);
        this.mActivity = mActivity;
    }


    protected void doDraw(Canvas cv) {
        Log.i("JM", "dodraw! : " + mGlobalVar.mStackOptimalData.size());
        if (isDraw) {


            setOptimalData();
            //       TypeRideOptimal tmp = mGlobalVar.mOptimalData;
            //         mGlobalVar.mStackOptimalData.add(tmp);

            BT_task();
            int mPointSize = mGlobalVar.mWidth / 40;
            int mStepSize = mGlobalVar.mWidth / 30;

            float center_x = mGlobalVar.mWidth / 2;
            float center_y = Util.getDpToPix(getContext(), 342) / 2;

            Log.i("JM", "center : " + center_x + ", " + center_y);
            Paint mMePaint = new Paint();
            mMePaint.setColor(Color.RED);

            mMePaint.setStrokeWidth(mPointSize);
            cv.drawCircle(center_x, center_y, mPointSize, mMePaint);
            int mDataSize = mGlobalVar.mOptimalData.getmGPSPoint().size();

            Paint mOtherPaint = new Paint();
            mOtherPaint.setColor(Color.BLUE);
            mOtherPaint.setStrokeWidth(mPointSize);
            mOtherPaint.setTextSize(50);

            int mIndex = 0;
            for (int i = 0; i < mDataSize; ++i) {
                if (mGlobalVar.mOptimalData.mIndexTable.get(i) == Integer.valueOf(mGlobalVar.mMember.getIdx()))
                    mIndex = i;
            }

            for (int i = 0; i < mDataSize; ++i) {
                if (mIndex != i) {
                    PointF tmpPoint = mGlobalVar.mOptimalData.getmGpsVectorTable()[mIndex][i];
                    if (mGlobalVar.mOptimalData.mGpsStatus == mGlobalVar.mOptimalData.mIndexTable.size()) {

                    } else {

                    }
                    cv.drawCircle(center_x + tmpPoint.x * mStepSize, center_y + tmpPoint.y * mStepSize, mPointSize, mOtherPaint);

                    Log.i("JM", "other : " + tmpPoint.x + ", " + tmpPoint.y);
                }
            }

        }
    }

    public void BT_task() {
        int mSize = mGlobalVar.mOptimalData.getmGPSPoint().size();
        int mIndex = Integer.valueOf(mGlobalVar.mMember.getIdx());
        int remIndex = 0;
        for (int i = 0; i < mSize; ++i) {
            if (mGlobalVar.mOptimalData.mIndexTable.get(i) == Integer.valueOf(mGlobalVar.mMember.getIdx()))
                remIndex = i;
        }



        //가까운 내 앞 자전거 찾기
        int mFrontMember = -1;
        float mFrontDistance = 9999;
        for (int i = 0; i < mSize; ++i) {
            int Idx = mGlobalVar.mOptimalData.mIndexTable.get(i);
            if (Idx != mIndex) {
                float dis = (float) VectorUtil.getScalar(mGlobalVar.mOptimalData.getmGpsVectorTable()[remIndex][i]);

                if (dis < mFrontDistance) {
                    mFrontDistance = dis;
                    mFrontMember = i;
                }

            }
        }

        final int loga = mFrontMember;
        final float logb = mFrontDistance;


        Log.i("BT", "front mem, distance : " + mFrontMember + " / " + mFrontDistance);

        final float log1 = mFrontDistance;
        final float log2 = mDistanceTotal / mDistanceCount;
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                //mActivity.mbeaconLog.setText("beacon, gps mean : " + String.format("%.2f", (beaconlog+gpslog)/2) + "// beacon : "+ String.format("%.2f", beaconlog));
                mActivity.mGPSLog.setText("distance : " + String.format("%.2f", log1) + " // " + String.format("%.2f", log2));

            }
        });

        //거리에 따른 아두이노 제어
        if (mFrontDistance < 7.5) {
            mActivity.runOnUiThread(new Runnable() {
                public void run() {
                    //mActivity.mbeaconLog.setText("beacon, gps mean : " + String.format("%.2f", (beaconlog+gpslog)/2) + "// beacon : "+ String.format("%.2f", beaconlog));
                    mActivity.mbeaconLog.setText("break!");
                }
            });
            //0X01 0X11 0X11 0X0D 스로틀
            //0X01 0X22 0X22 0X0D 브레이크
            byte[] tmp = new byte[4];
            tmp[0] = 0x01;
            tmp[1] = 0x22;
            tmp[2] = 0x22;
            tmp[3] = (byte) 0x0D;
            if(arduinoFlag != 1)
            {
                arduinoFlag = 1;
                if (mGlobalVar.mBtService.getState() == mGlobalVar.mBtService.STATE_CONNECTED)
                    mGlobalVar.mBtService.mSendMessage(tmp);
            }


        }else
        {
            if (mFrontDistance < mDistanceTotal/mDistanceCount*1.3) {

                byte[] tmp = new byte[4];
                tmp[0] = 0x01;
                tmp[1] = 0x11;
                tmp[2] = 0x11;
                tmp[3] = (byte) 0x0D;

                mActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        //mActivity.mbeaconLog.setText("beacon, gps mean : " + String.format("%.2f", (beaconlog+gpslog)/2) + "// beacon : "+ String.format("%.2f", beaconlog));
                        mActivity.mbeaconLog.setText("Run!");

                    }
                });
                if(arduinoFlag != 2)
                {
                    arduinoFlag = 2;
                    if (mGlobalVar.mBtService.getState() == mGlobalVar.mBtService.STATE_CONNECTED)
                        mGlobalVar.mBtService.mSendMessage(tmp);
                }
            }
        }


        if (mFrontDistance < 100) {
            mDistanceTotal += mFrontDistance;
            mDistanceCount++;
            if (mDistanceCount == 20) {
                mDistanceTotal = mDistanceTotal / mDistanceCount;
                mDistanceCount = 1;
            }
        }

    }

    public void setOptimalData() {

        int mDataSize = mGlobalVar.mRideData.size();
        Log.i("JM", "setOptimalData : " + mDataSize);
        for (int i = 0; i < mDataSize; ++i) {
            mGlobalVar.mOptimalData.mIndexTable.put(i, mGlobalVar.mRideData.get(i).Index);
        }
        Log.i("JM", "test1 - setOptimalData");
        double[][] tmpTable = new double[mDataSize][mDataSize];
        for (int i = 0; i < mDataSize; ++i) {
            for (int j = 0; j < mDataSize; ++j) {
                if (i == j) {
                    tmpTable[i][j] = -1.0;
                } else {
                    // beacon length mean
                    Log.i("JM", "in double loop");

                    double tmp1 = -1.0;
                    int flag = 0;
                    double tmp2 = -1.0;
                    double tmp = 0;
                    try {
                        tmp1 = mGlobalVar.mRideData.get(i).getBeaconLength().get(mGlobalVar.mOptimalData.mIndexTable.get(j));
                    } catch (NullPointerException e) {
                        flag = 1;
                    }

                    try {
                        tmp2 = mGlobalVar.mRideData.get(j).getBeaconLength().get(mGlobalVar.mOptimalData.mIndexTable.get(i));
                    } catch (NullPointerException e) {
                        flag += 2;
                    }

                    switch (flag) {
                        case 0:
                            tmp = (tmp1 + tmp2) / 2;
                            break;
                        case 1:
                            tmp = tmp2;
                            break;
                        case 2:
                            tmp = tmp1;
                            break;
                        case 3:
                            tmp = 0;
                            break;

                    }
                    Log.i("JM", "beaconLength " + i + ", " + j + ": " + tmp);

                    tmpTable[i][j] = tmp;

                }

            }
        }
        Log.i("JM", "test2 - setOptimalData");
        mGlobalVar.mOptimalData.setmBeaconLengthTable(tmpTable);
        HashMap<Integer, PointF> tmpGpsData = new HashMap<Integer, PointF>();
        for (int i = 0; i < mDataSize; ++i) {
            Log.i("JM", "set GPS data");
            if (mGlobalVar.mRideData.get(i).getGPS().x > 0.1 && mGlobalVar.mRideData.get(i).getGPS().y > 0.1) {
                mGlobalVar.mOptimalData.mGpsStatus++;
            }
            tmpGpsData.put(mGlobalVar.mRideData.get(i).Index, mGlobalVar.mRideData.get(i).getGPS());

        }
        mGlobalVar.mOptimalData.setmGPSPoint(tmpGpsData);

        PointF[][] tmpGPStable = new PointF[mDataSize][mDataSize];

        int mIndex = 0;
        for (int i = 0; i < mDataSize; ++i) {
            if (mGlobalVar.mOptimalData.mIndexTable.get(i) == Integer.valueOf(mGlobalVar.mMember.getIdx()))
                mIndex = i;
        }

        for (int i = 0; i < mDataSize; ++i) {
            for (int j = 0; j < mDataSize; ++j) {
                int srcIdx = mGlobalVar.mOptimalData.mIndexTable.get(i);
                int dstIdx = mGlobalVar.mOptimalData.mIndexTable.get(j);
                tmpGPStable[i][j] = VectorUtil.getVector(mGlobalVar.mOptimalData.getmGPSPoint().get(srcIdx), mGlobalVar.mOptimalData.getmGPSPoint().get(dstIdx));
                float distance = VectorUtil.calDistance(mGlobalVar.mOptimalData.getmGPSPoint().get(srcIdx).x,
                        mGlobalVar.mOptimalData.getmGPSPoint().get(srcIdx).y,
                        mGlobalVar.mOptimalData.getmGPSPoint().get(dstIdx).x,
                        mGlobalVar.mOptimalData.getmGPSPoint().get(dstIdx).y);
                Log.i("JM", "distance : " + distance);
                Log.i("JM", "beacondistance : " + mGlobalVar.mOptimalData.getmBeaconLengthTable()[i][j]);

                float tmpBeaconLength = (float) mGlobalVar.mOptimalData.getmBeaconLengthTable()[i][j];
                if (tmpBeaconLength > 0) {
                    tmpGPStable[i][j] = new PointF(tmpBeaconLength * VectorUtil.getUnitVector(tmpGPStable[i][j]).x, tmpBeaconLength * VectorUtil.getUnitVector(tmpGPStable[i][j]).y);
                    beaconFlag = true;
                } else
                {
                    tmpGPStable[i][j] = new PointF(((distance + tmpBeaconLength) / 2) * VectorUtil.getUnitVector(tmpGPStable[i][j]).x, ((distance + tmpBeaconLength) / 2) * VectorUtil.getUnitVector(tmpGPStable[i][j]).y);
                    beaconFlag = false;
                }


            }
        }

        tmpGpsData.clear();
        for (int i = 0; i < mDataSize; ++i) {
            int srcIdx = mGlobalVar.mOptimalData.mIndexTable.get(i);
            float sumX = 0;
            float sumY = 0;
            for (int j = 0; j < mDataSize; ++j) {
                if (i != j) {
                    sumX = tmpGPStable[i][j].x;
                    sumY = tmpGPStable[i][j].y;
                }
            }
            PointF optimalPos = new PointF();
            optimalPos.set(sumX / (mDataSize - 1), sumY / (mDataSize - 1));
            tmpGpsData.put(srcIdx, optimalPos);
            Log.i("JM", "optimalPos : " + optimalPos.x + ", " + optimalPos.y);

        }
        mGlobalVar.mOptimalData.setmGPSPoint(tmpGpsData);
        for (int i = 0; i < mDataSize; ++i) {
            for (int j = 0; j < mDataSize; ++j) {
                int srcIdx = mGlobalVar.mOptimalData.mIndexTable.get(i);
                int dstIdx = mGlobalVar.mOptimalData.mIndexTable.get(j);
                tmpGPStable[i][j] = VectorUtil.getVector(mGlobalVar.mOptimalData.getmGPSPoint().get(srcIdx), mGlobalVar.mOptimalData.getmGPSPoint().get(dstIdx));
            }
        }


        mGlobalVar.mOptimalData.setmGpsVectorTable(tmpGPStable);
        Log.i("JM", "end - setOptimalData");
    }


    public void hideView() {
        isDraw = false;
        stoploading = true;
    }

    public void showView(mGlobal aGlobalVar, RidingActivity aActivity) {
        Log.i("JM", "Show view");
        mActivity = aActivity;
        mGlobalVar = aGlobalVar;
        mCanvasBitmap = Bitmap.createBitmap(mGlobalVar.mWidth, Util.getDpToPix(getContext(), 342), Bitmap.Config.ARGB_8888);
        Log.e("JM", "width : " + mGlobalVar.mWidth);
        Log.e("JM", "height : " + mGlobalVar.mHeight);
        mCanvas = new Canvas();
        mCanvas.setBitmap(mCanvasBitmap);
        mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        isDraw = true;
        paint.setColor(Color.BLUE);
        StartAni();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mCanvasBitmap = Bitmap.createBitmap(mGlobalVar.mWidth, Util.getDpToPix(getContext(), 342), Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas();
        mCanvas.setBitmap(mCanvasBitmap);
        mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        setImageBitmap(mCanvasBitmap);
        doDraw(mCanvas);
        super.onDraw(canvas);
        if (isDraw) {
            DrawHan.sendEmptyMessageDelayed(0, 0);
        }
    }

    public void StartAni() {
        DrawHan.sendEmptyMessageDelayed(0, 5);
    }

    private Handler DrawHan = new Handler() {
        public void handleMessage(Message msg) {
            invalidate();
        }
    };


}