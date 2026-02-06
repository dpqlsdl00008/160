var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            qm.dispose();
            return;
        }
        if (status == 2) {
            qm.sendNext("... 어리석은 겁쟁이 인간...");
            qm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            qm.sendNext("우리는 세상을 떠도는 늑대의 일족. 잃어버린 아이를 찾으러 왔다. 그 아이를 그대가 기르고 있다는 소문을 들었다... 그 간 아이를 돌봐 준 것에 감사한다. 이제 아이를 돌려달라.");
            break;
        }
        case 1: {
            qm.sendNextPrevS("류호는 친구다. 돌려 줄 수 없다.", 2);
            break;
        }
        case 2: {
            qm.sendAcceptDecline("그런가... 그대의 뜻을 이해한다. 하지만 그렇다고 그냥 돌아 갈 순 없다. 그대를 시험해 보겠다. 과연 그대에게 늑대를 부릴 자격이 있는지... #r늑대의 시험을 받으라!#k");
            break;
        }
        case 3: {
            qm.dispose();
            if (qm.getPlayerCount(914030000) < 1) {
                qm.resetMap(914030000);
                qm.warp(914030000, 0);
                qm.forceStartQuest();
            } else {
                var quest = Packages.server.quest.MapleQuest.getInstance(21614).getName();
                qm.getPlayer().dropMessage(1, "이미 다른 유저가 '" + quest + "' 퀘스트를 진행 중에 있습니다.\r\n\r\n잠시 후에 다시 시도하여 주시거나 다른 채널을 이용하여 주세요.");
            }
            break;
        }
    }
}