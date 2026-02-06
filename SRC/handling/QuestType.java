package handling;

import java.util.Arrays;

public enum QuestType {
    
    Unk_0(0),
    Unk_1(1),
    Unk_2(2),
    Unk_3(3),
    Unk_4(4),
    Unk_5(5),
    QuestRes_Start_QuestTimer(6),
    QuestRes_End_QuestTimer(7),
    QuestRes_Start_TimeKeepQuestTimer(8),
    QuestRes_End_TimeKeepQuestTimer(9),
    QuestRes_Act_Success(10),
    QuestRes_Act_Failed_Unknown(11),
    QuestRes_Act_Failed_Inventory(12),
    QuestRes_Act_Failed_Meso(13),
    Unk_14(14),
    QuestRes_Act_Failed_Equipped(15),
    QuestRes_Act_Failed_OnlyItem(16),
    QuestRes_Act_Failed_TimeOver(17),
    Unk_18(18),
    Unk_19(19),
    ;

    private byte val;

    QuestType(int val) {
        this.val = (byte) val;
    }

    public byte getValue() {
        return val;
    }

    public static QuestType getQTFromByte(byte type) {
        return Arrays.stream(values()).filter(qt -> qt.getValue() == type).findAny().orElse(null);
    }
}
