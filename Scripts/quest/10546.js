var status = -1;
var rist = [];

function start(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else if (mode == 0 && type == 0) {
	status--;
    } else {
	qm.sendOk("흥! 큰 맘 먹고 구경시켜 주려고 했는데 싫다면 어쩔 수 없지.");
	qm.dispose();
	return;
    }
    var q = qm.getQuestRecord(2018040601);
    var value = q.getCustomData();
    var time = new Date();
    if (value == null) {
	q.setCustomData(time.getFullYear() + time.getMonth() + time.getDate() + "");
    }
    var qq = qm.getQuestRecord(2018040602);
    var value2 = qq.getCustomData();
    if (value2 == null) {
	qq.setCustomData("0");
    }
    if (status == 0) {
	qm.sendYesNo("날씨가 정말 춥지? 내가 재밌는 물건을 가져와 봤어. 어때? 한번 구경해볼래?");
    } else if (status == 1) {
	if (qm.haveItem(4220154, 1)) {
	    qm.gainItem(4220154, -1);
	} else {
	    if (!qm.canHold(4220154, 1)) {
		qm.sendOk("인벤토리가 부족합니다.");
		qm.dispose();
		return;
	    }
	}
	qm.sendOk("이건 따뜻한 온도계라는 거야. 온도계를 15℃까지 올려서 가져오면 내가 좋은 선물을 줄게. 온도계는 #b자신의 레벨 ±10 안의 몬스터#k를 사냥하면 온도계가 천천히 올라갈거야 참 100레벨 이후 몬스터는 자신의 레벨과는 관계 없이 올라간다구.");
	qm.gainItem(4220154, 1);
	qm.forceStartQuest(10546, "0.0");
	qm.dispose();
    }
}

function end(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else if (mode == 0 && type == 0) {
	status--;
    } else {
	qm.sendOk("온도계를 15℃까지 올려서 가져오면 내가 좋은 선물을 줄게. 온도계는 #b자신의 레벨 ±10 안의 몬스터#k를 사냥하면 온도계가 천천히 올라갈거야 참 120레벨 이후 몬스터는 자신의 레벨과는 관계 없이 올라간다구. 열심히 해봐.");
	qm.dispose();
	return;
    }
    var q = qm.getQuestRecord(2018040601);
    var value = q.getCustomData();
    var time = new Date();
    if (value == null) {
	q.setCustomData(time.getFullYear() + time.getMonth() + time.getDate() + "");
    }
    var qq = qm.getQuestRecord(2018040602);
    var value2 = qq.getCustomData();
    if (value2 == null) {
	qq.setCustomData("0");
    }
    if (q.getCustomData() != time.getFullYear() + time.getMonth() + time.getDate()) {
	qq.setCustomData("0");
	q.setCustomData(time.getFullYear() + time.getMonth() + time.getDate() + "");
    }
    if (java.lang.Integer.parseInt(qq.getCustomData()) >= 3) {
	qm.sendOk("이미 오늘 모든 온도계를 채웠어.");
	qm.dispose();
	return;
    }
    if (status == 0) {
	qm.sendYesNo("벌써 15℃를 다 채운 거야? 빠른데? 좋아 그럼 선물을 주지!\r\n#r인벤토리 공간을 충분히 확보하세요.#k");
    } else if (status == 1) {
	var reward = Math.floor(Math.random() * rist.length);
	qm.sendOk("소정의 선물을 줬으니 확인해봐.");
	qm.gainItem(4310001, 2);
	//qm.gainItem(rist[reward], 1);
	qm.gainItem(4220154, -1);
	qq.setCustomData(java.lang.Integer.parseInt(qq.getCustomData()) + 1 + "");
	qm.forceCompleteQuest();
	qm.dispose();
    }
}