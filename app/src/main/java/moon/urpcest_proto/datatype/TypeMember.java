package moon.urpcest_proto.datatype;

public class TypeMember {
    private boolean isLogin = false;
    private String mIdx;
    private String mId;
    private String mName;
    private String mBeaconAddr;
    private String mArduionAddr;
    private String mCrewRidingData;

    public TypeMember() {
    }

    public TypeMember(String aIdx, String aId, String aName, String aBeaconAddr) {
        mIdx = aIdx;
        mId = aId;
        mName = aName;
        mBeaconAddr = aBeaconAddr;
    }

    public void setData(String aIdx, String aId, String aName, String aBeaconAddr) {
        mIdx = aIdx;
        mId = aId;
        mName = aName;
        mBeaconAddr = aBeaconAddr;
    }

    public void setId(String aId) {
        this.mId = aId;
    }

    public void setIdx(String aIdx) {
        this.mIdx = aIdx;
    }

    public void setName(String aName) {
        this.mName = aName;
    }

    public void setBeaconAddr(String aBeaconAddr) {
        this.mBeaconAddr = aBeaconAddr;
    }

    public void setLogin(boolean isLogin) {
        this.isLogin = isLogin;
    }

    public String getId() {
        return mId;
    }

    public String getIdx() {
        return mIdx;
    }

    public String getName() {
        return mName;
    }

    public String getBeaconAddr() {
        return mBeaconAddr;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public String getmArduionAddr() {
        return mArduionAddr;
    }

    public void setmArduionAddr(String mArduionAddr) {
        this.mArduionAddr = mArduionAddr;
    }
    public String getmCrewRidingData() {
        return mCrewRidingData;
    }

    public void setmCrewRidingData(String mCrewRidingData) {
        this.mCrewRidingData = mCrewRidingData;
    }
}
