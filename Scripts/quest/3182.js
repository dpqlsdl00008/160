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
            qm.sendAcceptDecline("#b머트의 편지#k는 그의 가족에게 잘 전해줬네. 한 가지, 내 부탁을 들어 주었으면 좋겠네만...");
            break;
        }
        case 1: {
            qm.dispose();
            qm.gainItem(2430159, 1);
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
            qm.sendNext("자네로군... 내 편지는 알케스터님에게 잘 전해 드렸나?");
            break;
        }
        case 1: {
            qm.sendNextPrev("뭐? 내 저주가 풀렸다고? 그럴 리가...");
            break;
        }
        case 2: {
            qm.sendNextPrev("아니... 정말이로군... 이제 더 이상 춥지도 않아! 아프지도 않고! 움직이기도 자유로워! 하하하... 정말 고맙네...");
            break;
        }
        case 3: {
            qm.sendNextPrev("알케스터님에게 큰 빚을 지었군... 물론 자네에게도 말야... 답례로 내게 #b사자 왕의 노블 메달#k이나 #b사자 왕의 로얄 메달#k을 가져 오면 내가 이 성에서 찾은 #r장비#k나 #r무기#k로 바꿔 주지.");
            break;
        }
        case 4: {
            qm.sendNextPrev("뭐? 공짜로는 안 되냐고? 세상에 공짜가 어딨어?");
            break;
        }
        case 5: {
            qm.dispose();
            qm.forceCompleteQuest();
            break;
        }
    }
}