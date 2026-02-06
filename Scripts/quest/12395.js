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
            qm.sendNext("안녕하세요, #b"+qm.getPlayer().name+"#k님 벌써 레벨 "+qm.getPlayer().getLevel()+"을 달성하셨군요! 그 동안의 모험을 통해 축적된 경험으로 #b 두 번째 어빌리티#k를 얻을 수 있게 되었답니다. 지금 제가 그 두 번째 힘을 개방시켜 드릴게요.");
	} else if (status == 1) {
            qm.testInner(2);
            qm.forceStartQuest();
            qm.forceCompleteQuest();
            qm.sendNextPrev("자~! 더욱 강력해진 힘, 두 번째 어빌리티를 개방해 드렸습니다. 캐릭터 스탯창을 통해서 확인해보세요~!");
            qm.dispose();
	}
    }
}
