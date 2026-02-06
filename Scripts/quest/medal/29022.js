var status = -1;

function end(mode, type, selection) {
    if (qm.canHold(1142188, 1)) {
	qm.sendOk("우리 왕궁을 어려움으로부터 구해줘서 고맙네. 그대의 활양상은 대신들을 통해서 익히 들어왔다네. 그대야말로 진정한 수호자라 할 수 있지. 그대를 <#b버섯왕국의 수호자#k>로 임명하겠네.\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#i1142188# #z1142188# 1개#fUI/UIWindow.img/QuestIcon/8/0#2000 exp");
	qm.gainItem(1142188, 1);
	qm.qainExp(2000);
	qm.forceStartQuest();
	qm.forceCompleteQuest();
    } else {
	qm.sendOk("인벤토리 공간이 충분하지 않습니다.");
	qm.dispose();
}