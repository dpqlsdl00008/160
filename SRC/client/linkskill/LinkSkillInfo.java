package client.linkskill;

public class LinkSkillInfo {
    private final int RchrId;
    private final int OchrId;
    private final int skillid;
    private final long starttime;
    private final long length;

    private boolean canChange = false;

    public LinkSkillInfo(int r, int o, int skillid, long starttime, long length) {
        this.RchrId = r;
        this.OchrId = o;
        this.skillid = skillid;
        this.starttime = starttime;
        this.length = length;
    }

    public long getLength() {
        return length;
    }

    public long getStartTime() {
        return starttime;
    }

    public int getRchrId() {
        return RchrId;
    }

    public int getOchrId() {
        return OchrId;
    }

    public int getSkillId() {
        return skillid;
    }

    public boolean getChangable() {
        return canChange;
    }

    public void setChangable(boolean can) {
        this.canChange = getLength() + getStartTime() - System.currentTimeMillis() <= 0 || can;
    }
}