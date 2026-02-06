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
            qm.sendYesNo("이 푸른 빛의 기둥이 유적 발굴지의 몬스터들을 난폭하게 만든 것 일까요? 무슨 일이 생길지 모르니 제가 가까이 가서 만져볼게요. 준비되셨나요?");
            break;
        }
        case 1: {
            qm.sendNext("혹시, 위험한 일이 발생하면 저를 꼭 지켜주세요. 그럼, 하나... 둘... 셋!");
            break;
        }
        case 2: {
            qm.dispose();
            if (qm.getPlayerCount(931050410) != 0) {
                qm.sendNextPrev("잠시만요, 잠시 후에 다시 시도해봐요.");
                return;
            }
            qm.resetMap(931050410);
            qm.warp(931050410, 0);
            qm.spawnNpc(9073000, -627, 215);
            qm.forceStartQuest();
            break;
        }
    }
}