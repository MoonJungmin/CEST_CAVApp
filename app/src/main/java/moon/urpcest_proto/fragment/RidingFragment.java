package moon.urpcest_proto.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import moon.urpcest_proto.R;
import moon.urpcest_proto.activity.RidingActivity;
import moon.urpcest_proto.global.mGlobal;
import moon.urpcest_proto.model.CrewCreateDeleteTCP_Model;
import moon.urpcest_proto.model.CrewJoinTCP_Model;
import moon.urpcest_proto.model.CrewListTCP_Model;

import static com.google.android.gms.internal.zzhl.runOnUiThread;

public class RidingFragment extends Fragment{

    private ListView mCWListView;
    private ListAdapter mCWListAdapter;
    private ArrayList<String> mCWListData = new ArrayList<String>();
    mGlobal mGlobalVar = null;
    protected LayoutInflater mInflater = null;


    private Button mBtn_CrewCreate;
    private Button mBtn_CrewJoin;


    private static final int CODE_RIDING = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_riding, container, false);
        mGlobalVar = (mGlobal)getActivity().getApplicationContext();
        mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        v = init(v);
        return v;
    }

    private View init(View v){
        mCWListView = (ListView)v.findViewById(R.id.cw_listView);
        mCWListView.setTextFilterEnabled(false);
        mCWListView.setCacheColorHint(0);
        mCWListView.setDivider(null);
        mCWListAdapter = new ListAdapter(getActivity(), R.layout.layout_bluetooth_item, mCWListData);
        mCWListView.setAdapter(mCWListAdapter);

        mCWListAdapter.clear();
        mGlobalVar.initCrewList();
        JSONObject json = new JSONObject();
        try {
            json.put("FLAG", "5");
            json.put("CR_STATE_NO", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        CrewListTCP_Model tp = new CrewListTCP_Model(json.toString(), mGlobalVar);
        tp.run();

        mCWListAdapter.clear();
        for (int i = 0; i < mGlobalVar.mCrewList.size(); ++i) {
            String tmp = (i+1) + ". Owner ID : " + mGlobalVar.mCrewList.get(i).getmCrew_Owner_ID() + "(" + mGlobalVar.mCrewList.get(i).getmCrew_Members_no().size() + ")";
            mCWListData.add(tmp);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                mCWListAdapter.notifyDataSetChanged();
            }
        });

        mBtn_CrewCreate = (Button)v.findViewById(R.id.btn_crew_create);
        mBtn_CrewCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JSONObject putData = new JSONObject();
                try {
                    putData.put("FLAG", "3");
                    putData.put("CR_OWNER_ID", mGlobalVar.mMember.getId());
                    putData.put("CR_OWNER_NO", mGlobalVar.mMember.getIdx());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                CrewCreateDeleteTCP_Model tp = new CrewCreateDeleteTCP_Model(putData.toString(), mGlobalVar, true);
                tp.run();
                while(true)
                {
                    if(tp.endFlag == true)
                        break;
                }
                mGlobalVar.mCrew.setmCrew_Owner_no(mGlobalVar.mMember);
                mGlobalVar.mCrew.setmCrew_Owner_ID(mGlobalVar.mMember.getId());
                Intent intent = new Intent(getActivity(), RidingActivity.class);
                startActivityForResult(intent, CODE_RIDING);
            }
        });
        mBtn_CrewJoin = (Button)v.findViewById(R.id.btn_crew_join);
        mBtn_CrewJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        return v;
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
            return mCWListData.size();
        }

        public Object getItem(int position) {
            return mCWListData.get(position);
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
            textview_1.setText(mCWListData.get(position));
            textview_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("JM", "v click : " + position);

                    JSONObject putData = new JSONObject();
                    try {
                        putData.put("FLAG", "4");
                        putData.put("CR_MEM_NO", mGlobalVar.mMember.getIdx());
                        putData.put("CR_NO", mGlobalVar.mCrewList.get(position).getmCrew_no());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    CrewJoinTCP_Model tp = new CrewJoinTCP_Model(putData.toString(), mGlobalVar);
                    tp.run();
                    while(true)
                    {
                        if(tp.endFlag == true)
                            break;
                    }
                    mGlobalVar.mCrew = mGlobalVar.mCrewList.get(position);
                    Intent intent = new Intent(getActivity(), RidingActivity.class);
                    startActivityForResult(intent, CODE_RIDING);
                }
            });
            return v;
        }
    }

    public boolean allowBackPressed(){
        return true;
    }




}
