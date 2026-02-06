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
            qm.sendNext("아직 그녀에게 제 진심을 전하지 못했어요, 그녀에게 꼭 주고싶은 것이 있는데...");
            break;
        }
        case 1: {
            qm.sendNextPrev("그건 암시장 상인들에게만 구할 수 있지만 구하러 갈 때마다 번번이 실패했어요...");
            break;
        }
        case 2: {
            qm.askAcceptDecline("#d#h ##k님께서 그 물건 찾는 걸 도와주실 수 있나요?");
            break;
        }
        case 3: {
            qm.sendNext("정말 감사합니다! 그 물건은 #d암시장 상인#k에게서 구할 수 있어요.");
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
            qm.sendNext("드디어 제 마음을 담은 편지와 선물을 전해주고 왔어요.");
            break;
        }
        case 1: {
            qm.sendNextPrev("모두 #h #님 덕분이에요! 이제 마음 편히 떠날 수 있겠네요!");
            break;
        }
        case 2: {
            qm.sendNextPrev("자 이제 떠나볼까요?\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 1,194,189 exp");
            break;
        }
        case 3: {
            qm.dispose();
            qm.forceCompleteQuest();
            qm.gainItem(4034655, -1);
            qm.gainExp(1194189);
        }
    }
}