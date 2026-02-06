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
            qm.askAcceptDecline("마침 잘 왔다해!! 요새 마을 사람들이 모두 지쳐 있어 특별한 식사를 준비하려고 하는데 한 번 더 도와줄 수 있겠나?");
            break;
        }
        case 1: {
            qm.sendNext("정말 고맙다해! #i4009361# 식사의 마지막 재료인 #d#z4009361##k를 구해다 주게, 양고기는 외각의 버려진 농원에 있는 양들에게 구할 수 있다해. 50개의 양 고기를 나에게 가져와 주게!");
            break;
        }
        case 2: {
            qm.sendNextPrev("난 양고기로 양꼬치를 만들 계획이다해!... 생각만 해도 군침이 도는군...!");
            break;
        }
        case 3: {
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
            qm.sendNextS("#Cgray#(그릴에서 맛있는 고기가 지글거리는 소리가 들린다.)#k", 2);
            break;
        }
        case 1: {
            qm.sendNextPrev("자, 수고 많았다해! 양꼬치가 정말 맛있게 익었다해!\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#i2010048# #d#z2010048# 10개#k\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 1,501,265 exp");
            break;
        }
        case 2: {
            qm.dispose();
            if (qm.getInventory(2).getNumFreeSlot() < 1) {
                qm.getPlayer().getClient().sendPacket(Packages.tools.packet.CWvsContext.crossHunterQuestResult(2, 0));
                return;
            }
            qm.forceCompleteQuest();
            qm.gainItem(4009361, -50);
            qm.gainItem(2010048, 10);
            qm.gainExp(1501265);
            break;
        }
    }
}