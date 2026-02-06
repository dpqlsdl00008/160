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
            qm.sendNext("안녕하세요, #b#h0##k님. 벌써 레벨 30을 달성하셨군요! #b레벨 30#k을 달성하게 되면 특별한 힘, #b어빌리티#k를 얻을 수 있게 된답니다. 지금 제가 그 힘을 개방시켜 드릴게요.");
	} else if (status == 1) {
            qm.testInner(1);
            qm.forceStartQuest();
            qm.forceCompleteQuest();
            qm.sendNextPrev("자~! 당신의 새로운 힘, 어빌리티를 개방해 드렸습니다. 캐릭터 스탯창을 통해서 확인해보세요~!");
            qm.dispose();
	}
    }
}
