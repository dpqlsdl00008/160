var status = -1;
//
function start(mode, type, selection) {
	if (mode == 1) {
		status++;
	} else if (mode == 0 && type == 0) {
		status--;
	} else {
		qm.sendOk("협력할 마음이 없다니 아쉽군. 자, 그럼 다음에 또 보세.");
		qm.dispose();
		return;
	}
	if (qm.isQuestFinished(3455)) {
		if (status == 0) {
			qm.sendSimple("다. 다시 온 건가. 인간 친구. 무척 반갑지만 당장은 자네와 대화할 시간이 없으니 잠시만 자리를 비켜 주지 않겠나?\r\n\r\n#L0##b(초음파 해독기를 작동 한다.)#k#l")
		} else if (status == 1) {
			qm.sendSimple("제노에 대해서 물어봐야 대답할 말이 없다. 제노는 원로님들 담당이라 이 몸은 잘 모른다.(이 인간, 위험하다. 뭔가 낌새를 눈치챈 게 분명해. 어디서 정보가 샌 거지?)\r\n\r\n#L0##b(초음파 해독기 작동 중)#k#l")
		} else if (status == 2) {
			qm.sendSimple("그보다 인간 친구야 말로 어떻게 된 건가? 설마 지구방위본부의 위선자들에게 협력하는 건 아니겠지?(역시 김박사. 그 위험한 인간의 소행인가? ...어쨌든 최대한 이 사실에 대해선 감춰야 해.)\r\n\r\n#L0##b(초음파 해독기 작동 중)#k#l")
		} else if (status == 3) {
			qm.sendSimple("그레이들은 인간을 해칠 마음이 조금도 없다. 보다 가까워지고 싶을 뿐. 자네도 잘 알지 않나?(제노를 제어하던 콘솔을 망가뜨리다니, 그레이들의 수치다. 이런 사실을 인간들에게 알려줘 비웃음 받을 필요는 없지!)\r\n\r\n#L0##b(초음파 해독기 작동 중)#k#l")
		} else if (status == 4) {
			qm.sendSimple("그레이들의 지배를 두려워하지 말게. 그레이는 인간의 편이네. 인간을 위대한 길로 이끌 존재, 그것이 바로 그레이라네.(그레이는 항상 인간보다 우월한 존재여야 한다. 그러니 절대 이런 사실에 대해 알려주지 않겠어.)\r\n\r\n#L0##b(초음파 해독기 작동 종료.)#k#l")
		} else if (status == 5) {
			qm.askAcceptDecline("자... 이제 그레이에게 협력할 마음이 들었나?");
		} else if (status == 6) {
			qm.forceCompleteQuest(3456);
			qm.gainItem(4031927, -1);
			qm.dispose();
		}
	} else {
		if (status == 0) {
			qm.sendSimple("다. 다시 온 건가. 인간 친구. 무척 반갑지만 당장은 자네와 대화할 시간이 없으니 잠시만 자리를 비켜 주지 않겠나?\r\n\r\n#L0##b(초음파 해독기를 작동 한다.)#k#l")
		} else if (status == 1) {
			qm.sendSimple("제노에 대해서 물어봐야 대답할 말이 없다. 제노는 원로님들 담당이라 이 몸은 잘 모른다.(점점 인간의 음식에 미각이 길들여지는 것 같아 큰일이다. 아니, 미각뿐 아니라 모든 면에서 인간과 닮아가는 것 같다.)\r\n\r\n#L0##b(초음파 해독기 작동 중)#k#l")
		} else if (status == 2) {
			qm.sendSimple("그보다 인간 친구야 말로 어떻게 된 건가? 설마 지구방위본부의 위선자들에게 협력하는 건 아니겠지?(심지어 미의 기준도 점점 인간에 맞춰지고 있는 것 같다. 인간 중 최고로 잘생긴 건 역시 요원 M이겠지.)\r\n\r\n#L0##b(초음파 해독기 작동 중)#k#l")
		} else if (status == 3) {
			qm.sendSimple("그레이들은 인간을 해칠 마음이 조금도 없다. 보다 가까워지고 싶을 뿐. 자네도 잘 알지 않나?(아니, 이런 생각은 그만두자! 프린스 그레이님께서 이 사실을 알게 되시면 크게 분노하실 거야!)\r\n\r\n#L0##b(초음파 해독기 작동 중)#k#l")
		} else if (status == 4) {
			qm.sendSimple("그레이들의 지배를 두려워하지 말게. 그레이는 인간의 편이네. 인간을 위대한 길로 이끌 존재, 그것이 바로 그레이라네.(그보다 건설적인 생각을 하자. 그레이 슈트 세탁이라던가, 우주식량 건조라던가... 조만간 주부습진에 걸리고 말거야.)\r\n\r\n#L0##b(초음파 해독기 작동 종료.)#k#l")
		} else if (status == 5) {
			qm.askAcceptDecline("자... 이제 그레이에게 협력할 마음이 들었나?");
		} else if (status == 6) {
			qm.forceStartQuest();
			qm.gainItem(4031927, -1);
			qm.dispose();
		}
	}
	/**/
}

function end(mode, type, selection) {
	if (mode == 1) {
		status++;
	} else if (mode == 0 && type == 0) {
		status--;
	} else {
		qm.dispose();
		return;
	}
	qm.sendOk("?");
	qm.forceCompleteQuest();
	qm.dispose();
}