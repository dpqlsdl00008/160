var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status == 3 || status == 4) {
	    qm.dispose();
	    return;
	}
	status--;
    }
    if (status == 0) {
	qm.sendNext("어떻게 아셨는지 모르겠지만 바로 맞추셨어요. 얼마 전부터 남쪽 숲의 초록 버섯들이 포악해 지고 있거든요. 이상할 정도로 성격이 변한 초록 버섯들이 많아요. 아주 음침한 성격이 되어버렸어요.");
    } else if (status == 1) {
	qm.sendNextPrev("그런데 들리는 소문에 의하면, 이러한 현상이 꽤 여러 군데서 벌어지는 모양이에요. 그래서 자세히 알아봤더니 하나 같이 인형이라는 것과 관련이 있다고 하더라고요. 인형이라니... 정말 이상한 일이에요.");
    } else if (status == 2) {
	qm.sendNextPrev("소문이 진짜인지 거짓인지 모르지만 혹시 이번 초록 버섯과도 관련되어 있을지 몰라요. 초록 버섯이 포악해진 원인을 왜 물으시는지는 모르겠지만 궁금하시다면 함께 조사해 주시겠어요?");
    } else if (status == 3) {
	qm.sendYesNo("정말 소문처럼 초록 버섯이 변해버린 이유가 인형 때문인지... #r좌절한 초록 버섯 25마리#k를 퇴치하고 #b좌절한 초록 버섯의 인형#k을 찾아봐 주세요.");
    } else if (status == 4) {
	qm.forceStartQuest();
	qm.sendYesNo("감사해요. 초록 버섯이 살고 있는 비밀의 숲으로 제가 마법으로 보내드릴 수 있는데, 지금 가보시겠어요?");
    } else if (status == 5) {
	qm.warp(910100002, 0);
	qm.dispose();
    }
}