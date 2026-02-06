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
            qm.sendNext("그래요... 정말 최후의 방법입니다...");
            break;
        }
        case 1: {
            qm.askAcceptDecline("#d창백한 강시#k를... #e#r포획#k#n하도록 하죠. 준비 되셧나요?");
            break;
        }
        case 2: {
            qm.sendNext("#i2270042# #d#z2270042##k\r\n\r\n강시를 포획 할 수 있는 이 특별한 밧줄을 드리겠습니다. 이 밧줄은 마늘과 팥죽을 넣고 끓인 물에 인삼을 담구어 부적을 넣고 수 백번 꼬아 만든 물건이랍니다.");
            break;
        }
        case 3: {
            qm.sendNextPrev("이 밧줄을 이용해 창백한 강시를 포획해 와주세요... 함께 가고 싶지만... 그럴 수 없답니다.");
            break;
        }
        case 4: {
            qm.sendNextPrev("그는 강시 소굴에서 나타나고 딱 한 마리면 충분합니다! 딱 한 마리요!");
            break;
        }
        case 5: {
            qm.dispose();
            if (qm.getInventory(2).getNumFreeSlot() < 1) {
                qm.getPlayer().getClient().sendPacket(Packages.tools.packet.CWvsContext.crossHunterQuestResult(2, 0));
                return;
            }
            qm.forceStartQuest();
            qm.gainItem(2270042, 1);
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
            qm.sendNext("정말 믿을 수 없군요... 강시를 포획해 오신 겁니까??");
            break;
        }
        case 1: {
            qm.sendNextPrev("잠시 강시를 잡아 주시겠어요...? 자... 착하지... 김~치~~!!");
            break;
        }
        case 2: {
            qm.sendNextPrev("#d#h ##k님께도 기념으로 무언가를 드리겠습니다. 하하.\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#i3014010# #d#z3014010# 1개#k\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 852,991 exp");
            break;
        }
        case 3: {
            qm.dispose();
            if (qm.getInventory(3).getNumFreeSlot() < 1) {
                qm.getPlayer().getClient().sendPacket(Packages.tools.packet.CWvsContext.crossHunterQuestResult(2, 0));
                return;
            }
            qm.forceCompleteQuest();
            qm.gainItem(4034706, -1);
            qm.gainItem(3014010, 1);
            qm.gainExp(852991);
            break;
        }
    }
}