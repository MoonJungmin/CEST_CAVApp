package moon.urpcest_proto.model;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import moon.urpcest_proto.activity.MainActivity;
import moon.urpcest_proto.utils.Util;

/**
 * Created by Moon on 2015-08-26.
 */
public class BluetoothService {
    public static final String TOAST = "toast";

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    // Intent request code
    public static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    public static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    public static final int REQUEST_ENABLE_BT = 3;

    public static final int PROTOCOL_MOVIE_START = 36778253;
    public static final int PROTOCOL_SHOOT = 36844045;
    public static final int PROTOCOL_MOVIE_STOP = 36909837;

    public final byte[] PROTOCOL_ACK = new byte[4];

    public byte[] PROTOCOL_CYCLE_ROT = new byte[4];
    public byte[] PROTOCOL_STOP_ROT = new byte[4];
    public byte[] PROTOCOL_CYCLE_ROT_SET = new byte[4];

    public static final byte[] PROTOCOL_STEP_ROT = new byte[4];
    public static final byte[] PROTOCOL_STEP_ROT_SET = new byte[4];
    public static final byte[] PROTOCOL_DEVICE_MODE = new byte[4];

    public String mConnectedDeviceName;

    public interface BtCompleteCallbacks {
        void onBtComplete(int aMessageType, int aBuffer, int arg1, Object aRcvBuffer);
    }

    public static String DEVICE_NAME = "device_name";

    MainActivity mActivity;

    // Local Bluetooth adapter
    public BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the BluetoothService

    // Debugging
    private static final String TAG = "BluetoothService";
    private static final boolean D = true;
    // Member fields
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private BluetoothAdapter mAdapter;
    private final Handler mHandler;
    private int mState;
    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0; // we're doing nothing
    public static final int STATE_LISTEN = 1; // now listening for incoming
    // connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing
    // connection
    public static final int STATE_CONNECTED = 3; // now connected to a remote
    // device
    // RFCOMM Protocol
    private static final UUID MY_UUID_SECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final UUID MY_UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    // 약간의 working
    byte[] RcvBuffer = new byte[4];
    int pRcvBuffer;

    private Context mContext;

    static final int MAX_PAYLOAD = 512;
    int protocolStage = 0;
    int pre_protocolStage = 0;
    int rcvSz, consolePkSz, iPayload, checkSum;
    byte[] consolePayload = new byte[MAX_PAYLOAD + 4];

    public String lastMAC = null;
    public String lastTryMAC = null;

    public boolean mDeviceRotateState = false;
    public int modeIndex = 0;

    private BtCompleteCallbacks mCompleteCallback;

    public List<String> mPairedDevicesName = new ArrayList<String>();
    public List<String> mPairedDevicesAddress = new ArrayList<String>();
    public List<String> mNewDevicesName = new ArrayList<String>();
    public List<String> mNewDevicesAddress = new ArrayList<String>();

    // ========================== Constructor ==========================
    public BluetoothService(Handler handler, Context aContext) {
        PROTOCOL_ACK[0] = 0x02;
        PROTOCOL_ACK[1] = (byte) 0xA1;
        PROTOCOL_ACK[2] = (byte) 0xA1;
        PROTOCOL_ACK[3] = 0x0D;

        //0X02, 0X21, 0X21 ,0X0D (4byte)

        PROTOCOL_CYCLE_ROT[0] = 0X02;
        PROTOCOL_CYCLE_ROT[1] = 0X21;
        PROTOCOL_CYCLE_ROT[2] = 0X21;
        PROTOCOL_CYCLE_ROT[3] = (byte)0X0D;

//		0X02, 0X23, 0X23 ,0X0D (4byte)

        PROTOCOL_STOP_ROT[0] = 0x02;
        PROTOCOL_STOP_ROT[0] = 0x23;
        PROTOCOL_STOP_ROT[0] = 0x23;
        PROTOCOL_STOP_ROT[0] = (byte)0x0D;

        mContext = aContext;
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mHandler = handler;
        setState(STATE_NONE);
        configParaLoad();
    }


    public void mSendMessage(byte[] message) {

        Log.i("JM","mSendMessage: " + message.toString());

        if (this.getState() != BluetoothService.STATE_CONNECTED) {
            Log.i("JM","notConnect");

            Toast.makeText(mActivity, "not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        if (message.length > 0) {
            // byte[] send = message.getBytes();
            Log.i("JM","write!");
            this.write(message);

        }
    }

    private boolean configParaLoad() {
        boolean success = false;
        int i;

        File file = new File(mContext.getFilesDir(), "configPara.cfg");
        if (file.exists()) {
            try {
                DataInputStream stream = new DataInputStream(new FileInputStream(file));

                byte[] byteMAC;
                byteMAC = new byte[32];
                for (i = 0; i < 32; i++) {
                    byteMAC[i] = stream.readByte();
                }
                if ((byteMAC[0] == 0) || (byteMAC[0] > 31))
                    lastMAC = "";
                else
                    lastMAC = new String(byteMAC, 1, byteMAC[0]);

                stream.close();
                success = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!success) {
            lastMAC = "";
        }
        if (D)
            Log.d("JM", "configParaLoad()-->" + success);
        if (D)
            Log.d("JM", "last MAC-->" + lastMAC);
        return success;
    }

    public boolean configParaSave() {
        int i;
        boolean success = false;
        File file = new File(mContext.getFilesDir(), "configPara.cfg");
        try {
            DataOutputStream stream = new DataOutputStream(new FileOutputStream(file));

            int len = 0;
            if (lastMAC != "") {
                byte[] byteMAC = lastMAC.getBytes();
                len = byteMAC.length;
                if (len > 31)
                    len = 31;
                stream.writeByte((byte) len); // string length
                for (i = 0; i < len; i++) { // string (max 31)
                    stream.writeByte(byteMAC[i]);
                }
            }
            else
                stream.writeByte(0);

            if (len < 31) {
                for (i = 0; i < (31 - len); i++) {
                    stream.writeByte(0);
                }
            }

            stream.flush();
            stream.close();
            success = true;
        } catch (IOException e) {
            e.printStackTrace();
            success = false;
        }
        if (D)
            Log.d(TAG, "configParaSave()-->" + success);
        return success;
    }

    // ========================== ConnectThread ==========================
    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     *
     * @param device
     *            The BluetoothDevice to connect
     * @param secure
     *            Socket Security type - Secure (true) , Insecure (false)
     */
    public synchronized void connect(BluetoothDevice device, boolean secure) {
        if (D)
            Log.d("JM", "connect to: " + device);
        // Cancel any thread attempting to make a connection
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }
        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(device, secure);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }

    /**
     * This thread runs while attempting to make an outgoing connection with a
     * device. It runs straight through; the connection either succeeds or
     * fails.
     */
    private class ConnectThread extends Thread {
        private BluetoothSocket mmSocket;
        private BluetoothDevice mmDevice;
        private String mSocketType;

        public ConnectThread(BluetoothDevice device, boolean secure) {
            mmDevice = device;
            BluetoothSocket tmp = null;
            mSocketType = secure ? "Secure" : "Insecure";

            // ParcelUuid[] uuids = device.getUuids();

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                if (secure)
                {
                    // Log.i("JM", device.getUuids()[0].getUuid().toString());
                    Method m;
                    try {
                        m = device.getClass().getMethod("createRfcommSocket", new Class[] { int.class });
                        tmp = (BluetoothSocket) m.invoke(device, 1);
                        // tmp =
                        // device.createRfcommSocketToServiceRecord(MY_UUID_SECURE);
                    } catch (NoSuchMethodException e) {
                        Log.i("JM", "noSuchMethod");
                        e.printStackTrace();
                    } catch (IllegalArgumentException e) {
                        Log.i("JM", "IllegalArgumentException");
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        Log.i("JM", "IllegalAccessException");
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        Log.i("JM", "InvocationTargetException");
                        e.printStackTrace();
                    }
                }
                else {

                    tmp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID_SECURE);
                }

            } catch (IOException e) {
                if (D)
                    Log.e("JM", "Socket Type: " + mSocketType + "create() failed", e);
            }
            mmSocket = tmp;
            Log.i("JM", "mmSocket.toString : " + mmSocket.toString());

        }

        public void run() {
            if (D)
                Log.d("JM", "BEGIN mConnectThread SocketType:" + mSocketType);
            setName("ConnectThread");
            // Always cancel discovery because it will slow down a connection
            mAdapter.cancelDiscovery();
            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a successful
                // connection or an exception
                if (!mmSocket.isConnected())
                    mmSocket.connect();
            } catch (IOException e) {
                // Close the socket
                try {
                    Log.i("JM", "mmSocket.close()");
                    mmSocket.close();
                } catch (IOException e2) {
                    if (D)
                        Log.e("JM", "unable to close() " + mSocketType + " socket during connection failure", e2);
                }
                connectionFailed();
                return;
            }
            // Reset the ConnectThread because we're done
            synchronized (BluetoothService.this) {
                mConnectThread = null;
            }
            // Start the connected thread
            connected(mmSocket, mmDevice, mSocketType);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                if (D)
                    Log.e("JM", "close() of connect " + mSocketType + " socket failed", e);
            }
        }
    }

    // ========================== ConnectedThread ==========================
    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     *
     * @param socket
     *            The BluetoothSocket on which the connection was made
     * @param device
     *            The BluetoothDevice that has been connected
     */
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device, final String socketType) {
        if (D)
            Log.d("JM", "connected, Socket Type:" + socketType);
        // Cancel the thread that completed the connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        // Cancel the accept thread because we only want to connect to one
        // device
		/*
		 * if (mSecureAcceptThread != null) {
		 * mSecureAcceptThread.cancel(); mSecureAcceptThread = null;
		 * } if (mInsecureAcceptThread != null) {
		 * mInsecureAcceptThread.cancel(); mInsecureAcceptThread =
		 * null; }
		 */
        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket, socketType);
        mConnectedThread.start();

        // Send the name of the connected device back to the UI Activity
        Message msg = mHandler.obtainMessage(MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(DEVICE_NAME, device.getName());
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        pRcvBuffer = 0;
        setState(STATE_CONNECTED);
    }

    /**
     * This thread runs during a connection with a remote device. It handles all
     * incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket, String socketType) {
            if (D)
                Log.d("JM", "create ConnectedThread: " + socketType);
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                if (D)
                    Log.e("JM", "temp sockets not created", e);
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            if (D)
                Log.d("JM", "BEGIN mConnectedThread");
            byte[] buffer = new byte[4];
            byte chr;
            int bytes, checksum;
            int i;
            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    i = 0;
                    int proto = Util.byteArrayToInt(buffer);
                    Log.i("JM", "proto : " + String.valueOf(proto));
                    RcvBuffer = buffer;
                    if (mCompleteCallback != null) {
                        mCompleteCallback.onBtComplete(MESSAGE_READ, pRcvBuffer, -1, RcvBuffer);
                    }
                    mHandler.obtainMessage(MESSAGE_READ, pRcvBuffer, -1,RcvBuffer).sendToTarget();
                    pRcvBuffer = 0;

                } catch (IOException e) {
                    if (D)
                        Log.e("JM", "disconnected", e);
                    connectionLost();
                    // Start the service over to restart listening mode
                    BluetoothService.this.start();
                    break;
                }
            }
        }

        /**
         * Write to the connected OutStream.
         *
         * @param buffer
         *            The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);
                // Share the sent message back to the UI Activity
                // mHandler.obtainMessage(MainActivity.MESSAGE_WRITE, -1, -1,
                // buffer).sendToTarget();
            } catch (IOException e) {
                if (D)
                    Log.e("JM", "Exception during write", e);
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                if (D)
                    Log.e("JM", "close() of connect socket failed", e);
            }
        }
    }

    // ========================== Connection Exception
    // ==========================
    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private void connectionFailed() {
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(TOAST, "Unable to connect device");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
        // Start the service over to restart listening mode
        BluetoothService.this.start();
    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    private void connectionLost() {
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(TOAST, "Device connection was lost");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
        // Start the service over to restart listening mode
        BluetoothService.this.start();
    }

    // ========================== House Keeping ==========================
    /**
     * Start the chat service. Specifically start AcceptThread to begin a
     * session in listening (server) mode. Called by the Activity onResume()
     */
    public synchronized void start() {
        if (D)
            Log.d("JM", "start");
        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        setState(STATE_LISTEN);
    }

    /**
     * Stop all threads
     */
    public synchronized void stop() {
        if (D)
            Log.d("JM", "stop");
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        setState(STATE_NONE);
    }

    /**
     * Set the current state of the chat connection
     *
     * @param state
     *            An integer defining the current connection state
     */
    private synchronized void setState(int state) {
        if (D)
            Log.d("JM", "setState() " + mState + " -> " + state);
        mState = state;
        // Give the new state to the Handler so the UI Activity can update
        mHandler.obtainMessage(MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    /**
     * Return the current connection state.
     */
    public synchronized int getState() {
        return mState;
    }

    // ========================== Out Stream ==========================
    /**
     * Write to the ConnectedThread in an unsynchronized manner
     *
     * @param out
     *            The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED)
                return;
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);
    }

    public BtCompleteCallbacks getCompleteCallback() {
        return mCompleteCallback;
    }

    public void setCompleteCallback(BtCompleteCallbacks mCompleteCallback) {
        this.mCompleteCallback = mCompleteCallback;
    }

    public void setBluetooth() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(mContext, "Bluetooth is not available", Toast.LENGTH_LONG).show();
        }

        //TODO : 메인으로 넘기기
//		if (!mBluetoothAdapter.isEnabled()) {
//			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//			mContext.startActivityForResult(enableIntent, BluetoothService.REQUEST_ENABLE_BT);
//			Log.i("JM", "isEnable");
//		}
//		else {
//			if (mActivite.mGlobal.mBtService == null)
//				setupService();
//		}

    }

    public void setBluetoothFind() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        mContext.registerReceiver(mReceiver, filter);

        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        mContext.registerReceiver(mReceiver, filter);

        boolean pairflag = false;

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {

            for (BluetoothDevice device : pairedDevices) {
                mPairedDevicesName.add(device.getName());
                mPairedDevicesAddress.add(device.getAddress());
                Log.i("JM", "pairedDevice : " + device.getName() + "// pairedDevice Address : " + device.getAddress());


                pairflag = true;
                connectDevice(device.getAddress(), true);


            }
            if (!pairflag) {
                if (mBluetoothAdapter.isDiscovering())
                    mBluetoothAdapter.cancelDiscovery();

                mBluetoothAdapter.startDiscovery();

            }
        }
        else {
            Log.i("JM", "no Device paired");

            if (mBluetoothAdapter.isDiscovering())
                mBluetoothAdapter.cancelDiscovery();

            mBluetoothAdapter.startDiscovery();
        }

    }

    public void connectDevice(String aAddress, boolean secure) {
        // Get the device MAC address
        String address = aAddress;
        lastTryMAC = address;
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(aAddress);
        connect(device, false);
        //Toast.makeText(mActivite, "device connected", Toast.LENGTH_LONG).show();
    }


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i("JM", "action :" + action);
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    if (!mNewDevicesAddress.contains(device.getAddress())) {
                        //device.fetchUuidsWithSdp();
                        mNewDevicesName.add(device.getName());
                        mNewDevicesAddress.add(device.getAddress());
                    }
                    Log.i("JM", "newDevice : " + device.getName() + "// Address : " + device.getAddress());

                }
                // When discovery is finished, change the Activity title
            }
            else
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.i("JM", "newDevice : x");

            }
        }
    };



}
