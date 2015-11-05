package moon.urpcest_proto.datatype;

import java.util.ArrayList;

/**
 * Created by Moon on 2015-08-28.
 */
public class TypeCrew {
    private int mCrew_no;
    private String mCrew_Owner_ID;
    private TypeMember mCrew_Owner_no;
    private ArrayList<TypeMember>mCrew_Members_no = new ArrayList<TypeMember>();

    private String mCrew_Riding_Data;

    private float mCrew_Riding_Distance = 0;
    private String mCrew_Riding_Time = "00:00:00";
    private float mCrew_Riding_Speed = 0;




    public String getmCrew_Riding_Data() {
        return mCrew_Riding_Data;
    }

    public void setmCrew_Riding_Data(String mCrew_Riding_Data) {
        this.mCrew_Riding_Data = mCrew_Riding_Data;
    }

    public int getmCrew_no() {
        return mCrew_no;
    }

    public void setmCrew_no(int mCrew_no) {
        this.mCrew_no = mCrew_no;
    }

    public String getmCrew_Owner_ID() {
        return mCrew_Owner_ID;
    }

    public void setmCrew_Owner_ID(String mCrew_Owner_ID) {
        this.mCrew_Owner_ID = mCrew_Owner_ID;
    }

    public TypeMember getmCrew_Owner_no() {
        return mCrew_Owner_no;
    }

    public void setmCrew_Owner_no(TypeMember mCrew_Owner_no) {
        this.mCrew_Owner_no = mCrew_Owner_no;
    }

    public ArrayList<TypeMember> getmCrew_Members_no() {
        return mCrew_Members_no;
    }

    public void setmCrew_Members_no(ArrayList<TypeMember> mCrew_Members_no) {
        this.mCrew_Members_no = mCrew_Members_no;
    }

    public float getmCrew_Riding_Distance() {
        return mCrew_Riding_Distance;
    }

    public void setmCrew_Riding_Distance(float mCrew_Riding_Distance) {
        this.mCrew_Riding_Distance = mCrew_Riding_Distance;
    }

    public String getmCrew_Riding_Time() {
        return mCrew_Riding_Time;
    }

    public void setmCrew_Riding_Time(String mCrew_Riding_Time) {
        this.mCrew_Riding_Time = mCrew_Riding_Time;
    }

    public float getmCrew_Riding_Speed() {
        return mCrew_Riding_Speed;
    }

    public void setmCrew_Riding_Speed(float mCrew_Riding_Speed) {
        this.mCrew_Riding_Speed = mCrew_Riding_Speed;
    }




}
