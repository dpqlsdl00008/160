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
            qm.sendNext("정말 훌륭합니다! 이제 여기에다가 저의 특별한 가루를 넣으면... 짜잔!! 완성되었습니다!");
            break;
        }
        case 1: {
            qm.askAcceptDecline("#i2270041# #d#z2270041##k\r\n\r\n이걸 가지고 창백한 좀비에게 뿌리면 달아날 것입니다!!\r\n\r\n#Cgray#(단축 키에 놓고 해당 좀비를 공격 할 때 사용 해보자.)#k");
            break;
        }
        case 2: {
            qm.dispose();
            if (qm.getInventory(2).getNumFreeSlot() < 1) {
                qm.getPlayer().getClient().sendPacket(Packages.tools.packet.CWvsContext.crossHunterQuestResult(2, 0));
                return;
            }
            qm.forceStartQuest();
            qm.gainItem(2270041, 10);
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
            qm.sendNext("돌아왔군요 영웅님! 창백한 좀비는 도망가던가요?");
            break;
        }
        case 1: {
            qm.sendNextPrev("그... 그럴 리 가요 ... 아무래도 그들이 면역이 생겨 그런것 같습니다!\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 682,393 exp");
            break;
        }
        case 2: {
            qm.dispose();
            qm.forceCompleteQuest();
            qm.removeAll(2270041);
            qm.removeAll(4034272);
            qm.gainExp(682393);
            break;
        }
    }
}