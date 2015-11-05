package moon.urpcest_proto.model;


import android.util.Log;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

import moon.urpcest_proto.datatype.TypeMember;
import moon.urpcest_proto.global.mGlobal;
import moon.urpcest_proto.utils.JSON_Parser;

/**
 * Created by Moon on 2015-08-13.
 */


public class CrewPreStartTCP_Model implements Runnable {
    private static final String serverIP = "155.230.160.58";  // ex: 192.168.0.100
    private static final int serverPort = 10001; // ex: 5555
    private String msg;
    private String return_msg = null;
    mGlobal mGlobalVar;

    public boolean endFlag = false;

    public CrewPreStartTCP_Model(String _msg, mGlobal mGlobalVar) {
        this.msg = _msg;
        this.mGlobalVar = mGlobalVar;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        try {

            InetAddress server_adr = InetAddress.getByName(serverIP);
            Log.d("JM", "LoginSocket: Connecting...");
            Socket socket = new Socket(server_adr, serverPort);
            //socket.setSoTimeout(3000);
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
                JSONArray jArr = new JSONArray(return_msg);
                ArrayList<TypeMember> memList = new ArrayList<TypeMember>();
                for (int i = 0; i < jArr.length(); ++i) {
                    JSONArray memArr = new JSONArray(jArr.getJSONArray(i).toString());
                    memList.add(JSON_Parser.getTypeMember(memArr.toString()));

                }
                mGlobalVar.mCrew.setmCrew_Members_no(memList);
                socket.close();
                endFlag = true;
            }
        } catch (Exception e) {
            Log.e("JM", "LoginSocket: Error2", e);
        }
    }
}



