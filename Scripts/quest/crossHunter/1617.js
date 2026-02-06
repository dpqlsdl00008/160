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
            qm.sendYesNo("갑작스러운 제안에 놀라셨겠군요. 죄송하지만, 당신이 우리와 함께 할 자격이 있는지를 먼저 확인해봐도 될까요? 자격도 없는 이에게 우리의 이야기를 함부로 누설할 수는 없으니까요. 괜찮으시겠습니까?");
            break;
        }
        case 1: {
            qm.sendNext("네 좋습니다. 그럼 간단한 테스트에 협조 부탁드립니다. 여기 있는 제 강아지 해피를 시간 내에 물리치시면 됩니다. 그럼 시작해보죠.");
            break;
        }
        case 2: {
            qm.dispose();
            if (qm.getPlayerCount(931050510) != 0) {
                qm.sendNextPrev("잠시만요, 잠시 후에 다시 시도해봐요.");
                return;
            }
            qm.resetMap(931050510);
            qm.warp(931050510, 0);
            qm.forceStartQuest();
            break;
        }
    }
}