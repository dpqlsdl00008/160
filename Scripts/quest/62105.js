var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            qm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            qm.sendNext("앗! #d#h ##k님 아니십니까?");
            break;
        }
        case 1: {
            qm.sendNextPrev("저는 모험가님 덕분에 고백에도 성공하고 리프레에 잘 다녀왔습니다. 게다가 타타모 촌장님께 축복도 받고 예원 정원에서 그녀와 함께 쭉 지내기로 했답니다.");
            break;
        }
        case 2: {
            qm.sendNextPrev("정말 감사했습니다! 이건 도와주신 것에 비해 소소하지만 예원 정원에서 요긴하게 쓰이는 물건이랍니다. 그럼 즐거운 여행되시기 바랍니다!\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#i4310177# #d#z4310177# 3개#k");
            break;
        }
        case 3: {
            qm.dispose();
            if (qm.getInventory(4).getNumFreeSlot() < 1) {
                qm.getPlayer().getClient().sendPacket(Packages.tools.packet.CWvsContext.crossHunterQuestResult(2, 0));
                return;
            }
            qm.forceCompleteQuest();
            qm.gainItem(4310177, 3);
            break;
        }
    }
}