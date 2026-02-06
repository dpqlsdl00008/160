var status = -1;

function action(mode, type, selection) {
    cm.dispose();
    for (i = 1; i < 6; i++) {
        if (cm.getInventory(i).getNumFreeSlot() < 2) {
            cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CWvsContext.crossHunterQuestResult(2, 0));
            return;
        }
    }

    var say = "\r\n#Cyellow#핫 타임 선물 상자#k에서 아이템을 획득하였습니다.#Cgreen#";
    say += "\r\n";
    say += "\r\n#fUI/UIWindow.img/QuestIcon/4/0#";
    say += "\r\n#i2435457# #z2435457# 5개";
    say += "\r\n#i2028048# #z2028048# 5개";
    say += "\r\n#i2450000# #z2450000# 1개";
    say += "\r\n#i2431256# #z2431256# 3개";

    cm.sendNext(say);

    cm.gainItem(2431156, -1); // 상자 삭제
    cm.gainItem(2435457, 5); // 골드애플 10개
    cm.gainItem(2028048, 5); // 의문의 메소 주머니 5개
    cm.gainItem(2450000, 1);  // 사냥꾼의 행운 1개
    cm.gainItem(2431256, 3); // 신비한 의자 상자
}