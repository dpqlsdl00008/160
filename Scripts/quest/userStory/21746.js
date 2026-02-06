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
            qm.sendNext("봉인석... 그것은 아주 오래 전부터 무릉이 지켜왔던 것... 그걸 노리는 자가 이제와서야 나타나다니...");
            break;
        }
        case 1: {
            qm.sendNextPrevS("봉인석에 대해 알려 주십시오.", 2);
            break;
        }
        case 2: {
            qm.sendAcceptDecline("그럴 수는 없다네. 이 그림자 무사란 자가 위험한 것은 사실이나 자네가 그보다 위험할지 어찌 알겠나? 그러니 시험이 필요하겠군... #b시험#k을 치르겠나?");
            break;
        }
        case 3: {
            qm.dispose();
            if (qm.getPlayerCount(925040001) < 1) {
                qm.resetMap(925040001);
                qm.warp(925040001, 0);
                qm.forceStartQuest();
            } else {
                var quest = Packages.server.quest.MapleQuest.getInstance(21746).getName();
                qm.getPlayer().dropMessage(1, "이미 다른 유저가 '" + quest + "' 퀘스트를 진행 중에 있습니다.\r\n\r\n잠시 후에 다시 시도하여 주시거나 다른 채널을 이용하여 주세요.");
            }
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
            qm.sendYesNo("워떤감? 족자 복원은 잘 되어가는감? 어디, 그럼 뭐라고 쓰여 있는지 한 번 볼꺼나?");
            break;
        }
        case 1: {
            qm.sendNext("히에에엑! 이게 뭐시여!");
            break;
        }
        case 2: {
            qm.dispose();
            qm.gainItem(4032343, -1);
            qm.forceCompleteQuest();
            break;
        }
    }
}