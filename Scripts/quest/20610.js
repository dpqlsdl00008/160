var status = -1;

function start(mode, type, selection) {
    if (mode == -1) {
        qm.dispose();
    } else {
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {
            qm.askAcceptDecline("그간 스킬은 많이 익히셨습니까? 어느 정도 익숙해지셨을 테니... 이제 또 #b새로운 스킬#k을 익혀 보셔야죠?");
        } else if (status == 1) {
            qm.forceStartQuest();
	    qm.dispose();
        }
    }
}