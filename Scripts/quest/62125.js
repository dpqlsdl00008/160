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
            qm.sendNext("여태 아직 거기 있었나?");
            break;
        }
        case 1: {
            qm.askAcceptDecline("이 늙은이가 걱정되는 모양이군. 그렇다면 #d대왕 지네#k 잡는 것을 도와주지 않겠나?");
            break;
        }
        case 2: {
            qm.sendNext("어느 날 갑자기 송산으로 가는 길에 나타난 대왕 지네 때문에 주변 농작물과 동물들이 해를 입고 있어, 난 이곳을 떠날 수 없다네. 자네가 그것을 좀 물리쳐 주게!");
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
            qm.sendNext("지네는 사라졌나? 내 소중한 농장은 안전한거겠지?");
            break;
        }
        case 1: {
            qm.sendNextPrev("자네가 내 마음의 짐을 덜어 주었군, 고맙네.\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 1,705,983 exp");
            break;
        }
        case 2: {
            qm.dispose();
            qm.forceCompleteQuest();
            qm.gainExp(1705983);
            break;
        }
    }
}