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
            qm.askAcceptDecline("3층에도 양 끝에 기숙사가 있다고 들었어. 그럼 그곳을 조사... 잠깐, 숙녀들의 방을 마음대로 뒤져도 되는 걸까?");
            break;
        }
        case 1: {
            qm.sendNext("이, 이건 어디까지나 조사를 위해서니까...\r\n(쿠디는 왠지 모르게 얼굴이 빨개졌다.)\r\n\r\n나 대신 3층의 기숙사를 둘러봐줘. 나는 여기에서 기다리고 있을게.");
            break;
        }
        case 2: {
            qm.dispose();
            qm.forceForfeitQuest(32135);
            qm.forceStartQuest();
            break;
        }
    }
}