package moon.urpcest_proto.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import moon.urpcest_proto.datatype.TypeCrew;
import moon.urpcest_proto.datatype.TypeMember;

/**
 * Created by Moon on 2015-08-28.
 */
public class JSON_Parser {

    public static TypeMember getTypeMember(String src) {
        TypeMember dst = new TypeMember();
        try {
            JSONArray jArr = new JSONArray(src);
            //JSONObject json = new JSONObject(src);
            //JSONArray jArr = json.getJSONArray("data");
            Log.i("JM", "json : " + jArr.getJSONObject(0).toString());
            dst.setId(jArr.getJSONObject(0).getString("mb_id"));
            dst.setIdx(jArr.getJSONObject(0).getString("mb_no"));
            dst.setName(jArr.getJSONObject(0).getString("mb_name"));
            dst.setBeaconAddr(jArr.getJSONObject(0).getString("mb_bc_addr"));
            dst.setmCrewRidingData(jArr.getJSONObject(0).getString("mb_crew_no"));
            dst.setmArduionAddr(jArr.getJSONObject(0).getString("mb_ad_addr"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dst;
    }

    public static TypeCrew getTypeCrew(String src) {
        TypeCrew a= new TypeCrew();
        return a;
    }

}
