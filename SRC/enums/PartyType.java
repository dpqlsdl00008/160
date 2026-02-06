package enums;

public enum PartyType {

    PartyReq_InviteParty(4),
    PartyReq_ApplyParty(7),
    PartyReq_CreateParty(12),
    PartyReq_CreatePortal(60),
    
    No(-1);

    private byte val;

    PartyType(int val) {
        this.val = (byte) val;
    }

    public static PartyType getByVal(byte type) {
        if (type >= 0 && type <= values().length) {
            return values()[type];
        }
        return null;
    }

    public byte getVal() {
        return val;
    }
}