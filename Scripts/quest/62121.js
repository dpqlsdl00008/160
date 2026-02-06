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
            qm.sendNext("오우~ 아직 여기 있었냐해? 피난민들이 점점 더 몰려들고 있다해... 게다가 이 도시의 요리사는 나 하나 뿐이니 재료가 부족해도 구하러 갈 수 없다해...");
            break;
        }
        case 1: {
            qm.askAcceptDecline("자네가 혹시 재료를 좀 구해다 줄 수 있겠나해? 대신 자네에게도 내 요리를 나눠주지!");
            break;
        }
        case 2: {
            qm.sendNextPrev("예원 정원의 모든 사람들은 #i2010047# #d#z2010047##k을 좋아하지. 하지만 그것을 만드려면 달걀이 필요하네. 특히 오리 알로 만든 차예단은 정말 별미이지!");
            break;
        }
        case 3: {
            qm.sendNextPrev("그럼 난 여기서 음식을 준비하고 있겠다해!");
            break;
        }
        case 4: {
            qm.dispose();
            qm.forceStartQuest();
            break;
        }
    }
}

function end(mode, type, selection) {
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
            qm.sendNext("오호! 벌써 오리 알을 구해온겐가?");
            break;
        }
        case 1: {
            qm.sendNextPrev("힘들었겠군, 자 여기 너의 것은 내가 조금 남겨 두었다해. 차예단 좋아하나?\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#i2010047# #d#z2010047# 10개#k\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 1,125,949 exp");
            break;
        }
        case 2: {
            qm.dispose();
            if (qm.getInventory(2).getNumFreeSlot() < 1) {
                qm.getPlayer().getClient().sendPacket(Packages.tools.packet.CWvsContext.crossHunterQuestResult(2, 0));
                return;
            }
            qm.forceCompleteQuest();
            qm.gainItem(4009358, -50);
            qm.gainItem(2010047, 10);
            qm.gainExp(1125949);
            break;
        }
    }
}