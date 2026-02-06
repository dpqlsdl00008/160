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
            qm.askAcceptDecline("쪽지에 적힌 대로라면 기숙사 어딘가에 숨겨져 있지 않을까? 남학생들의 기숙사는 2층의 양 끝에 있다고 들었어. 그 곳을 조사해서 무엇이 나오는지 봐 줘.");
            break;
        }
        case 1: {
            qm.sendNext("나는 여기에서 기다리고 있을게, #h #.\r\n#b(2층의 양쪽 기숙사를 확인해보자.)#k");
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