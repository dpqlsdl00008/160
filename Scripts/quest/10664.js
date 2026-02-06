var status = -1;

function end(mode, type, selection) {
    if (mode == -1) {
        qm.dispose();
    } else {
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {
            qm.sendNext("안녕!! #e"+qm.getPlayer().name+"#n 나는 #b#e카산드라#n#k야 일단 #r"+qm.getPlayer().getLevel()+"#k을 달성한걸 축하해! 다름이아니라 작은 선물을 준비했어!!");
	} else if (status == 1) {
            qm.gainItem(3010025, 1);
            qm.forceCompleteQuest();
            qm.sendNextPrev("테스트용 선물이야~~! 정신오픈때는 아마 달라지겠지~? 그럼 잘가~!!!\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#i3010025# 단풍나무 아래서");
            qm.dispose();
	}
    }
}
