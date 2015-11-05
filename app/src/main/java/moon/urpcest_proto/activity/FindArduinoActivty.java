package moon.urpcest_proto.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import moon.urpcest_proto.R;
import moon.urpcest_proto.global.mGlobal;
import moon.urpcest_proto.model.BluetoothService;

public class FindArduinoActivty extends BaseActivity {

    private ListView mBtListView;
    private ListAdapter mBtListAdapter;
    private ArrayList<String> mBtListData = new ArrayList<String>();
    private Button mBtn_BtSearch;

    protected LayoutInflater mInflater = null;
    mGlobal mGlobalVar= null;
    private String mBtAddr = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mGlobalVar = (mGlobal)getApplicationContext();
        mGlobalVar.mBtGHander = mBtHandler;

        setContentView(R.layout.activity_find_arduino);
        mBtn_BtSearch = (Button)findViewById(R.id.btn_bluetooth_search);

        mBtListView = (ListView)findViewById(R.id.bt_listView);
        mBtListView.setTextFilterEnabled(false);
        mBtListView.setCacheColorHint(0);
        mBtListView.setDivider(null);
        mBtListAdapter = new ListAdapter(this, R.layout.layout_bluetooth_item, mBtListData);
        mBtListView.setAdapter(mBtListAdapter);

        mBtn_BtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtListAdapter.clear();
                for (int i = 0; i < mGlobalVar.mBtService.mNewDevicesName.size(); ++i) {
                    mBtListData.add(mGlobalVar.mBtService.mNewDevicesName.get(i));
                    Log.i("JM", "arduino : " + mGlobalVar.mBtService.mNewDevicesName.get(i));
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mBtListAdapter.notifyDataSetChanged();
                    }
                });

                mGlobalVar.mBtService.setBluetoothFind();
            }
        });


    }

    @Override
    public void onBackPressed() {
        Bundle extra = new Bundle();
        Intent intent = new Intent();
        extra.putString("Arduino", mBtAddr);
        intent.putExtras(extra);
        setResult(RESULT_OK, intent);
        finish();

        super.onBackPressed();
    }


    public class ListAdapter extends ArrayAdapter<Object> {
        private ArrayList<String> item;
        Context context;
        private int resourceId;

        public ListAdapter(Context context, int resourceID, ArrayList item) {
            super(context, resourceID, item);
            this.item = item;
            this.resourceId = resourceID;
            this.context = context;
        }

        public int getCount() {
            return mBtListData.size();
        }

        public Object getItem(int position) {
            return mBtListData.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                v = mInflater.inflate(R.layout.layout_bluetooth_item, null);
            }
            TextView textview_1 = (TextView) v.findViewById(R.id.bt_item_textview);
            textview_1.setText(mBtListData.get(position));
            textview_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mGlobalVar.mBtService.mBluetoothAdapter.cancelDiscovery();
                    Log.i("JM", "v click : " + position);
                    mGlobalVar.mBtService.DEVICE_NAME = mGlobalVar.mBtService.mNewDevicesName.get(position);
                    mGlobalVar.mBtService.connectDevice(mGlobalVar.mBtService.mNewDevicesAddress.get(position), false);
                    mBtAddr = mGlobalVar.mBtService.mNewDevicesAddress.get(position);
                }
            });
            return v;
        }
    }

    private final Handler mBtHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle extra = new Bundle();
            Intent intent = new Intent();
            switch (msg.what) {
                case BluetoothService.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTING:
                            break;
                        case BluetoothService.STATE_CONNECTED:
                            extra.putString("Arduino", mBtAddr);
                            intent.putExtras(extra);
                            setResult(RESULT_OK, intent);
                            finish();
                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
                            extra.putString("Arduino", mBtAddr);
                            intent.putExtras(extra);
                            setResult(RESULT_OK, intent);
                            finish();
                            break;
                    }
                    break;
                case BluetoothService.MESSAGE_WRITE:

                    break;
                case BluetoothService.MESSAGE_READ:
                    break;
            }
        }
    };
}
