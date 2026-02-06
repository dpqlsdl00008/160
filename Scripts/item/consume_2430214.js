var value = 20200000;
var date = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) % 100 + "/" + Packages.tools.StringUtil.getLeftPaddedStr(java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) + "", "0", 2) + "/" + java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH);

function action(mode, type, selection) {
    cm.dispose();
    if (cm.getPlayer().getOneInfoQuest(value, "fatigue_use").equals("5") && cm.getPlayer().getOneInfoQuest(value, "fatigue_date").equals(date)) {
        cm.sendNext("\r\n피로 회복제는 하루에 #e#r5회#k#n 이상 사용 할 수 없습니다.");
        return;
    }
    cm.gainItem(2430214, -1);
    if (cm.getPlayer().getOneInfoQuest(value, "fatigue_date").equals(date) == false) {
        cm.getPlayer().updateOneInfoQuest(value, "fatigue_use", "1");
        cm.getPlayer().updateOneInfoQuest(value, "fatigue_date", date);
    } else {
        var v5 = parseInt(cm.getPlayer().getOneInfoQuest(value, "fatigue_use"));
        cm.getPlayer().updateOneInfoQuest(value, "fatigue_use", (v5 + 1) + "");
    }
    var itemFatigue = 30;
    var reduceFatigue = (cm.getPlayer().getFatigue() - itemFatigue);
    if (reduceFatigue < 0) {
        reduceFatigue = 0;
    }
    cm.getPlayer().setFatigue(reduceFatigue);
    cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CUserLocal.chatMsg(Packages.handling.ChatType.GameDesc, "피로 회복제 - 카페인 드링크를 사용하여 피로도를 회복하였습니다. (" + cm.getPlayer().getOneInfoQuest(value, "fatigue_use") + "/5)"));
}