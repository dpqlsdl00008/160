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
            qm.sendNext("음? 듀나미스 서포트 임무를 어쩌고 이리로 온 겁니까? ...네? 듀나미스의 임무는 종료했다고요? 이미 에레브로 떠났다는 쪽지만 남겨져 있었다니... 무슨 말을 하시는 겁니까? #r듀나미스는 에레브로 돌아오지 않았는데요?#k");
            break;
        }
        case 1: {
            qm.sendAcceptDecline("아무래도 뭔가 이상하군요... 철두 철미한 듀나미스가 연락처도 남기지 않은 것부터 수상해도 당신을 서포트로 붙였는데 이런 일까지 생기다니... 마지막에 듀나미스의 흔적을 발견한 것이 검은 마녀의 동굴이었다고 하셨죠?");
            break;
        }
        case 2: {
            qm.dispose();
            qm.forceCompleteQuest();
            qm.sendNext("다시 한 번 #b검은 마녀의 동굴#k로 가서 듀나미스가 남긴 다른 흔적은 없는지 확인해 주십시오. 혹시 뭔가 놓친 게 있을지도 모르니까요. 이쪽에서도 총력을 다해 그를 찾도록 하겠습니다.");
            break;
        }
    }
}