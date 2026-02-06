var status = -1;

function start(mode, type, selection) {
    if (mode == 0) {
	if (status == 0) {
	    qm.dispose();
	    return;
	}
	status--;
    } else {
	status++;
    }
    if (status == 0) {
	qm.sendNext("　\r\n벌써 30 레벨이 되었군. #b묵현#k이 되기 위해 나를 찾아온건가? 그렇다면 잘 찾아왔어.");
    } else if (status == 1) {
	qm.sendNextPrev("　\r\n#b묵현#k에 대해 간단하게 설명을 해주지. 묵현은 #r중국 메이플 스토리(CMS)#k의 직업으로 #b권포#k라는 무기를 사용하며 주 스탯은 #bSTR#k이야.");
    } else if (status == 2) {
	qm.sendNextPrev("　\r\n묵현은 #b버프 스킬#k이 적은 반면 #b공격 스킬#k을 극대화 시켜주는 #r패시브 스킬#k이 특화 된 직업이야.");
    } else if (status == 3) {
	qm.askAcceptDecline("각 종 세부적인 스킬에 대해서는 모험을 하며 차차 알아보기로 하고 그럼 지금 바로 #b묵현#k이 되겠어?");
    } else if (status == 4) {
	var rsp = (qm.getPlayerStat("LVL") - 30) * 3;
	if (qm.getPlayerStat("RSP") > rsp) {
	    qm.sendNext("　\r\n음... SP를 아직 다 사용하지 않은 것 같은데? SP가 너무 많아 남아 아직 2차 전직을 할 수 없어.");
	    qm.dispose();
	    return;
	}
	qm.changeJob(530);
	qm.sendNext("　\r\n축하해. 이제 넌 묵현이 되었어. 너에게 방금 묵현이 익힐 수 있는 스킬들이 적혀 있는 책을 주었어. 그 책에는 여러 가지 묵현과 관련된 스킬들이 들어 있어. 게다가 소비 아이템 보관함 갯수도 늘려주었어. 한 줄씩 늘어나 있을 거야. 최대 HP, MP도 늘려주었고 말이야... 한번 확인 해 봐. 앞으로도 더욱 정진해서 더욱 강력한 해적이 되도록 해.");
	qm.forceCompleteQuest();
	qm.dispose();
    }
}