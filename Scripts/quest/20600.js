var status = -1;

function start(mode, type, selection) {
    if (mode == -1) {
        qm.dispose();
    } else {
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {
            qm.askAcceptDecline("#h #. 혹시 레벨이 90을 넘었다고 수련에 소홀해진 거 아닙니까? 확실히 당신은 강하지만 아직 수련은 끝나지 않았습니다. 기사단장들을 본받으세요. 검은 마법사에 대비하여 끊임없이 수련을 하는 그들을 말입니다.");
        } else if (status == 1) {
            qm.forceStartQuest();
	    qm.dispose();
        }
    }
}