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
            qm.sendAcceptDecline("기사 등급 시험을 보길 원한다면 에레브로 오십시오. 각 기사 단장들이 당신의 능력을 테스트 하고 그 결과 자격이 된다면 당신을 정식 기사로 임명해 줄 겁니다. 그럼...");
            break;
        }
        case 1: {
            qm.dispose();
            qm.forceStartQuest();
            break;
        }
    }
}