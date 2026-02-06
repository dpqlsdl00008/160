var status = -1;
var nItem = [
4310010, // 사자 왕의 로얄 메달
4310177, // 예원 정원 코인
];

function action(mode, type, selection) {
    cm.dispose();
    cm.sendNext("\r\n아직 보상을 못 정했어요... ㅠㅠ... 일단 가지고 계셔들 주세요...");
    /*
    for (i = 1; i < 6; i++) {
        if (cm.getInventory(i).getNumFreeSlot() < 2) {
            cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CWvsContext.crossHunterQuestResult(2, 0));
            return;
        }
    }

    var nChance = Packages.server.Randomizer.nextInt(nItem.length - 1);
    var nReward = nItem[nChance];
    var say = "\r\n#Cyellow#데일리 기프트 보상 상자#k에서 아이템을 획득하였습니다.#Cgreen#";
    say += "\r\n";
    say += "\r\n#fUI/UIWindow.img/QuestIcon/4/0#";
    say += "\r\n#i" + nReward + "# #z" + nReward + "# 1개";
    cm.sendNext(say);
    cm.gainItem(2438145, -1);
    cm.gainItem(nReward, 1);
    */
}