package moon.urpcest_proto.model;


import android.graphics.PointF;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;

import moon.urpcest_proto.datatype.TypeRiding;
import moon.urpcest_proto.global.mGlobal;

/**
 * Created by Moon on 2015-08-13.
 */


public class CrewStartTCP_Model implements Runnable {
    private static final String serverIP = "155.230.160.58";  // ex: 192.168.0.100
    private static final int serverPort = 10001; // ex: 5555
    private String msg;
    private String return_msg = null;
    mGlobal mGlobalVar;

    public boolean endFlag = false;

    public CrewStartTCP_Model(String amsg, mGlobal mGlobalVar) {
        this.msg = amsg;
        this.mGlobalVar = mGlobalVar;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        try {
            InetAddress server_adr = InetAddress.getByName(serverIP);
            Log.d("JM", "LoginSocket: Connecting...");
            Socket socket = new Socket(server_adr, serverPort);
            try {
                    Log.d("JM", "LoginSocket: Sending: '" + msg + "'");
                    PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                    out.println(msg);
                    Log.d("JM", "LoginSocket: Sent.");
                    Log.d("JM", "LoginSocket: Done.");

                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    return_msg = in.readLine();

                    Log.d("JM", "LoginSocket: Server send to me this message -->" + return_msg);
            } catch (Exception e) {
                Log.e("JM", "LoginSocket: Error1", e);
            } finally {

                JSONObject jobj = new JSONObject(return_msg);
                for(int i=0;i<mGlobalVar.mCrew.getmCrew_Members_no().size();++i)
                {
                    String index = mGlobalVar.mCrew.getmCrew_Members_no().get(i).getIdx();
                    String mIndex =mGlobalVar.mMember.getIdx();
                    if(!index.equals(mIndex))
                    {
                        JSONObject locationObj = new JSONObject(jobj.getString(index));
                        TypeRiding tmpRideData = new TypeRiding(Integer.valueOf(index));
                        float x = Float.valueOf(locationObj.getString("GPS_x"));
                        float y = Float.valueOf(locationObj.getString("GPS_y"));
                        tmpRideData.setGPS(new PointF(x, y));
                        HashMap<Integer, Double> tmpHashMap = new HashMap<Integer, Double>();
                        for(int j=0;j<mGlobalVar.mCrew.getmCrew_Members_no().size();++j)
                        {
                            String Idx = mGlobalVar.mCrew.getmCrew_Members_no().get(j).getIdx();
                            if(!index.equals(Idx))
                            {
                                double length;
                                try{
                                    length = Double.valueOf(locationObj.getString(Idx));
                                    Log.i("JM","test : " + length);
                                    tmpHashMap.put(Integer.valueOf(Idx), length);
                                }
                                catch(JSONException e)
                                {
                                    Log.i("JM", "Error beacon length null!");
                                    length = -1.0;
                                    tmpHashMap.put(Integer.valueOf(Idx), length);
                                }
                            }
                        }
                        tmpRideData.setBeaconLength(tmpHashMap);

                        int targetIndex = 0;
                        for(int h=0;h<mGlobalVar.mRideData.size();++h)
                        {
                            if(mGlobalVar.mRideData.get(h).Index == tmpRideData.Index)
                            {
                                targetIndex = h;
                                break;
                            }

                        }
                        if(targetIndex != 0)
                        {
                            mGlobalVar.mRideData.set(targetIndex, tmpRideData);
                        }
                        else
                        {
                            mGlobalVar.mRideData.add(tmpRideData);
                        }

                    }
                }
                Log.i("JM", "size : " + mGlobalVar.mRideData.size());
                Log.i("JM", "to Me length! : "+ mGlobalVar.mRideData.get(1).getBeaconLength().get(Integer.valueOf(mGlobalVar.mMember.getIdx())));
                socket.close();
                endFlag = true;
            }


        } catch (Exception e) {
            Log.e("JM", "LoginSocket: Error2", e);
        }
    }
}



