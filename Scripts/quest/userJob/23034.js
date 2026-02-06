var status = -1;

function end(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            qm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            if (qm.isQuestActive(23034) == false) {
                qm.dispose();
                qm.forceStartQuest();
            } else {
                qm.sendNext("에너지 전송 장치를 없애고 돌아왔구나! 후후, 역시! 내 눈은 틀림없어. 너라면 해낼 거라고 믿었지. 이걸로 우리 마을의 에너지 부족 현상도 한동안은 괜찮을 거야. 넌 마을을 위해 정말 큰 공을 세웠어.");
            }
            break;
        }
        case 1: {
            qm.sendYesNo("네 능력이 이쯤 되었으니 걱정하지 않고 수업을 다음 단계로 넘기겠어. 다른 사람들은 아직 위험하다고 말렸지만... 너라면 스킬에 눌리지 않고 충분히 더 강한 와일드 헌터로 거듭날 수 있을 거야!");
            break;
        }
        case 2: {
            if (qm.isQuestActive(23034) == true) {
                qm.changeJob(3311);
                qm.forceCompleteQuest();
            }
            qm.sendNext("널 전직시켰어. 이제 더 이상 예전의 네가 아니야. 더 강력하고 빠르고 화려한 스킬의 세계가 펼쳐질 거야. 라이딩까지 타고 다니니 사용하기 쉽지야 않겠지만... 겁날 게 뭐가 있겠어? 그 아슬아슬한 임무도 해낸 너인데 말이야!");
            break;
        }
        case 3: {
            qm.dispose();
            qm.sendNextPrev("그럼 다음 수업에 보자. 그때까지 레지스탕스로서 멋지게 활약하길 기대하지.");
            break;
        }
    }
}