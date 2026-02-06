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
            qm.sendNext("바빠, 영웅님? 그 동안 온갖 방법으로 빅토리아 아일랜드를 샅샅히 뒤지다 보니 재미있는 정보를 하나 발견했어. 바로 인형사에 관한 건데...");
            break;
        }
        case 1: {
            qm.sendNextPrev("알고 있어? 영웅님이 인형사를 혼내준 이후로 이블아이의 굴에 있던 입구가 작동을 안 하고 있었잖아? 인형사 녀석, 대신 #b대신 다른쪽에 거처를 마련한 모양#k이더군.");
            break;
        }
        case 2: {
            qm.sendAcceptDecline("슬리피우드에 있는 #b깊은숲사냥터2#k에 #b작은 나무집#k에 인형사가 들어가는 걸 봤다는 정보가 있어. 아마도 확실한 것 같아. 바로 가서 #r인형사#k를 퇴치해 버리라고.");
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
            qm.sendNext("인형사를 퇴치하고 돌아온 모양이군.. 그런데 왜 그렇게 표정이 안 좋아? 무슨 일이라도 있어?");
            break;
        }
        case 1: {
            qm.sendNextPrevS("빅토리아 아일랜드의 봉인석에 관한 정보는 전혀 없었다.", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("아하! 그거 말이로군. 후후후... 그거라면 걱정할 거 없어.");
            break;
        }
        case 3: {
            qm.dispose();
            qm.gainExp(17100);
            qm.forceCompleteQuest();
            break;
        }
    }
}