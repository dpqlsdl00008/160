package handling;

public enum ChatType {
    
    Normal(0),
    Whisper(1),
    GroupParty(2),
    GroupFriend(3),
    GroupGuild(4),
    GroupAlliance(5),
    GameDesc(6),
    Tip(7),
    Notice(8),
    Notice2(9),
    AdminChat(10),
    SystemNotice(11),
    SpeakerChannel(12),
    SpeakerWorld(13),
    SpeakerWorldGuildSkill(-1),
    unk_1(14),
    ItemSpeaker(15),
    unk_2(16),
    unk_3(17),
    ItemSpeakerItem(18),
    SpeakerBridge(19),
    SpeakerWorldExPreview(20),
    ;
    
    private short val;

    ChatType(short val) {
        this.val = val;
    }

    ChatType(int i) {
        this((short) i);
    }

    public short getValue() {
        return val;
    }
}
