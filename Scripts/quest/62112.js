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
            qm.askAcceptDecline("시험은 간단합니다. 예원 정원의 셰프 #d젠 황#k에게 가셔서 #d팥죽#k과 #d마늘#k을 구해오는 겁니다.");
            break;
        }
        case 1: {
            qm.sendNext("너무 간단하죠? 얼른 가서 구해와주세요.");
            break;
        }
        case 2: {
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
            qm.sendNext("반갑다해! 아니, 아직은 반가워하면 안 되는 건가...");
            break;
        }
        case 1: {
            qm.sendNextPrev("여기에 머물고 싶다면 내 레스토랑에서 내 요리를 먹어야 한다해. 나는 이 동네에서 유일한 요리사이고 다들 이 레스토랑에서만 밥을 먹지. 여기서 무언가를 먹지 않는다면 당신을 강시로 오해하고 사람들이 너를 죽일지도 몰라.");
            break;
        }
        case 2: {
            qm.sendNextPrevS("도대체 왜요?", 2);
            break;
        }
        case 3: {
            qm.sendNextPrev("#d젠 롱#k에게 설명을 들으면 이해가 될꺼다해.");
            break;
        }
        case 4: {
            qm.dispose();
            if (qm.getInventory(4).getNumFreeSlot() < 2) {
                qm.getPlayer().getClient().sendPacket(Packages.tools.packet.CWvsContext.crossHunterQuestResult(2, 0));
                return;
            }
            qm.gainItem(4034643, 1);
            qm.gainItem(4034656, 1);
            qm.forceCompleteQuest();
            qm.forceStartQuest(62113);
            break;
        }
    }
}