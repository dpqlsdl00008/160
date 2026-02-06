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
            qm.sendNext("레벨 10이 된 걸 보니 꽤나 노력하신 모양이군요. 좋습니다. 당신은 이제 노블레스에서 벗어나, 정식으로 수련기사가 될 자격이 있음을 인정합니다. 하지만, 그 전에 먼저 한 가지 묻고 싶습니다. 당신은 어떤 기사의 길을 갈지 결정했습니까?");
        } else if (status == 1) {
            qm.sendNextPrev("기사의 길은 한 가지가 아닙니다. 당신 앞에 준비된 길은 모두 다섯 가지... 어떤 길을 선택하건 자유입니다만, 적어도 후회하는 일은 없어야겠지요. 그러나 먼저 당신께 보여드리죠. 당신이 기사가 된 모습을...");
        } else if (status == 2) {
            qm.sendSimple("어떻습니까? 기사단장이 된 자신의 모습을 미리 보시겠습니까? 이미 어떤 기사가 될지 정해 놓았다면 보지 않아도 상관 없습니다.\r\n#b#L0#기사단장이 된 모습을 보고 싶어요.#l\r\n#L1#기사단장이 된 모습을 보지 않아도 괜찮아요.");
        } else if (status == 3) {
	    if (selection == 1) {
	        qm.sendYesNo("기사단장이 된 모습을 보지 않으셔도 괜찮습니까? 그렇다면 바로 어떤 기사가 될 지, 선택의 기회를 드리겠습니다.");
	    } else {
		qm.warp(913040100, 0);
	        qm.forceCompleteQuest(20100);
	    }
	} else if (status == 4) {
	    qm.forceStartQuest();
	    qm.forceCompleteQuest(20100);
            qm.sendNext("당신이 가야 할 길을 이미 확실히 알고 계신 모양이군요. 그렇다면 좋습니다. 이제 남은 것은 당신의 선택뿐.");
	} else if (status == 5) {
            qm.sendPrev("왼편에 기사단장들이 당신을 기다리고 있습니다. 그들의 말을 잘 듣고 무엇이든 당신이 원하는 길로 가십시오. 그 모든 것이 당신의 길이니...");
            qm.dispose();
        }
    }
}