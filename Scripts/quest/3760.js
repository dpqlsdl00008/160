var status = -1;

function start(mode, type, selection) {
	qm.sendOk("Go to #bChief Tatamo#k in Leafre and bring back a Dragon Moss Extract.");
	qm.forceStartQuest();
	qm.dispose();
}

function end(mode, type, selection) {
    	status++;
	if (status == 0) {
		if (qm.haveItem(4032531,1)) {
			qm.sendNext("Great! Please wait till I mix these ingredients together...");
		} else {
			qm.sendOk("용족의 서식지에서만 자라나는 이끼들을 모아 액체화 한것으로 아주 드물게 발견이 되어 구하기가 쉽지는 않습니다만, 다행이 하프링거 족들의 각종 질병 치료에 유용한 특성 때문에 대대로 채취를 해서 보관하고 있을 것입니다. 촌장 타타모에게 부탁을 해보세요.");
			qm.forceStartQuest();
			qm.dispose();
		}
	} else {
		qm.teachSkill(qm.getPlayer().getStat().getSkillByJob(1026, qm.getPlayer().getJob()), 1, 0); // Maker
		qm.gainExp(11000);
		qm.removeAll(4032531);
		qm.sendOk("There we go! You have learned the Soaring skill and will be able to fly, using great amounts of MP.");
		qm.forceCompleteQuest();
		qm.dispose();
	}
}
	