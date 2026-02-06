importPackage(Packages.client);

var status = -1;

function start(mode, type, selection) {
    if (mode == -1 || (mode == 0 && status == 0)) {
	qm.dispose();
	return;
    }
    else if (mode == 0)
	status--;
    else            
	status++;
    var q = qm.getQuestRecord(2288);
    var check = q.getCustomData();
    if (status == 0) {
        qm.sendSimple("자 그럼 보기부터 드릴테니 원하시면 들어보시고 이 중에 골라 주세요. #b한 번만#k 들려드리니 잘 듣고 골라 주세요.\r\n\\t#b#L1# 1번 노래 듣기#l\r\n\\t#L2# 2번 노래 듣기#l\r\n\\t#L3# 3번 노래 듣기#l\r\n\\r\n\\t#e#L4# 정답 입력하러 가기#l");
    } else if(status == 1) {
        if (selection == 1) {
	    qm.playSound(false, "quest2288/" + check.substring(0, 1));
	    qm.sendOk("익숙한데...");
	    status = -1;
	} else if (selection == 2) {
	    qm.playSound(false, "quest2288/" + check.substring(1, 2));
	    qm.sendOk("이거였나?");
	    status = -1;
	} else if(selection == 3) {
	    qm.playSound(false, "quest2288/" + check.substring(2, 3));
	    qm.sendOk("들었어요?");
	    status = -1;
	} else if(selection == 4) {
	    qm.sendGetNumber("자, 그럼 이제 정답을 말해 주세요. 기회는 단 한번이니 꼭 신중하게 답변해 주세요. 아래 창에 1,2,3 숫자 중 한글자만 입력해 주세요\r\n",1,1,3);
	}
    } else if(status == 2) {
	if (selection == 1) {
	    qm.sendOk("음악을 제대로 듣지 못하셨군요.");
	    qm.dispose();
	} else if(selection == 2) {
	    qm.sendOk("한 번 더 기회를 드리죠.");
	    qm.dispose();
	} else if(selection == 3) {
	    qm.sendOk("그가 연주한 곡은 바로 이거였군요. 뭐 제 곡은 아니었지만, 이제야 모든 궁금증이 풀렸어요 정말 감사해요.");
	    qm.gainExp(32500);
	    qm.forceCompleteQuest();
	    qm.dispose();
	}
    }
}