var status = -1;
function start(mode, type, selection) {
    status++;
	if (mode != 1) {
	    if(type == 1 && mode == 0)
		    status -= 2;
		else{
		}
	}
	if (status == 0) {
qm.sendNext("흠. 요새 혁이에 대한 소문이 안 좋은 것 같다는 생각은 했지만 이렇게 팬들이 직접 나서기까지 할줄이야... 그 정도면 엄청 행실이 나쁘다는 거잖아요? 이거 걱정이군요.");

	} else if (status == 1) {
qm.sendNextPrev("그런데 혁이 녀석이 좀 이상하긴 했어요. 그 녀석하고는 하도 오래된 친구라 그리 예의를 차리지 않는데 갑자기 막 예의를 차라질않나, 옛날 얘기를 해도 못 알아듣지를 않나.");
	} else if (status == 2) {
qm.sendNextPrev("얘가 스트레스를 많이 받아서 그런가 보다 했는데... 흠. 언제부터 그랬더라? 분명히 몇 달 전까지만 해도 새로 좋은 기타를 샀다면서 엄청 자랑했는데... 그 이후부터인가?");
	
	} else if (status == 3) {
qm.sendNext("그 기타가 혁이가 변한 것과 관련이 있는지는 잘 모르겠어요. 그냥 생각나는 게 그거 뿐이긴 한데... 그게 되게 비싸고 좋은 기타라는데 뭔가 꺼림칙했거든요. 커닝스퀘어에 도는 괴담도 있고... ");
	
} else if (status == 4) {
qm.sendNextPrev("무슨 괴담이냐고요? 아주 오래 전 커닝스퀘어에서 자신의 음악성을 인정받지 못하고 쓸쓸히 죽어간 어떤 가수가 그에 대한 원한으로 새롭게 떠오르는 신인가수를 파멸시킨다는 그런 괴담이에요.");
	
} else if (status == 5) {
qm.sendNextPrev("그 가수가 기타를 쓴다고 해서 그런지 괜히 기타만 보면 기분이 별로더라고요. 가난한 가수인데 어디서 돈을 마련했는지 기타만큼은 엄청 비싼 거를 썼다고 하던데... 에이, 혁이랑은 관련 없겠죠?");

} else if (status == 6) {
qm.sendAcceptDecline("그래도 혹시 모르니 혁이 녀석을 한 번 찾아가 봐주세요. 뭐 근본이 나쁜 녀석은 잘 아니니까 잘 애기하면 정신을 차릴거에요.");


} else if (status == 7) {
qm.evanTutorial("UI/tutorial/singerHyuk/0/0", -1);
qm.forceStartQuest();
qm.dispose();
} else if (status == 5) {

		qm.dispose();
	}
}
function end(mode, type, selection) {
	qm.dispose();
}
