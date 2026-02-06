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
            qm.sendNext("분석이 모두 완료 되었네...! 하지만 특별히 검출 된 것이 없었다네...");
            break;
        }
        case 1: {
            qm.askAcceptDecline("#i4034647# #d#z4034647##k\r\n\r\n우선 이 보고서를 가져가보게!");
            break;
        }
        case 2: {
            qm.dispose();
            if (qm.getInventory(4).getNumFreeSlot() < 1) {
                qm.getPlayer().getClient().sendPacket(Packages.tools.packet.CWvsContext.crossHunterQuestResult(2, 0));
                return;
            }
            qm.forceStartQuest();
            qm.gainItem(4034647, 1);
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
            qm.sendNext("돌아 오셨군요! 선생님은 뭐라고 하시던가요?");
            break;
        }
        case 1: {
            qm.sendNextPrev("네? 아무것도 검출 된 게 없다구요...? 이상하네 분명 바이러스가 검출 되어야 하는데....\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 852,991 exp");
            break;
        }
        case 2: {
            qm.dispose();
            qm.forceCompleteQuest();
            qm.gainItem(4034647, -1);
            qm.gainExp(852991);
            break;
        }
    }
}