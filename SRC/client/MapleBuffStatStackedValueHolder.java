package client;

public class MapleBuffStatStackedValueHolder {
    private final int skillid, value, localDuration, time;

    public MapleBuffStatStackedValueHolder (int skillId, int value, int c, int localDuration) {
        this.skillid = skillId;
        this.value = value;
        this.time = c;        
        this.localDuration = localDuration;   
    }
    
    public int getSkillId() {
        return skillid;
    }
    
    public int getValue() {
        return value;
    }

    public int getBuffLength() {
        return localDuration;
    }
    
    public int getTime() {
        return time;
    }
}
