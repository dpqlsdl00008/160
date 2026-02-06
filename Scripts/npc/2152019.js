function action(mode, type, selection) {
    if (cm.isQuestActive(23005) == true && cm.haveItem(4032783) == true) {
	cm.sendNext("게시판에 전단지를 붙였다.");
	cm.forceStartQuest(23006, "1");
	cm.gainItem(4032783, -1);
    }
    cm.dispose();
}