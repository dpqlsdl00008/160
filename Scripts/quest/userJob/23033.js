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
            if (qm.isQuestActive(23033) == false) {
                qm.dispose();
                qm.forceStartQuest();
            } else {
                qm.sendNext("에너지 전송 장치를 없애고 돌아왔군! 정말 잘했어! 이걸로 우리 마을의 에너지 부족 현상도 조금은 해소 될 거야! 이제야 숨통 좀 트이겠군! 대단해! 넌 마을을 위해 정말 큰 공을 세운 거야!");
            }
            break;
        }
        case 1: {
            qm.sendYesNo("이 정도로 능력이 있다면 더 이상 망설일 것 없지. 너무 위험해서 좀 더 후에 전수할 생각이었지만... 더 강력한 배틀 메이지의 스킬을 너에게 전수해 주겠어! 너라면 그 정도는 감당할 수 있을 것 같군!");
            break;
        }
        case 2: {
            if (qm.isQuestActive(23033) == true) {
                qm.changeJob(3211);
                qm.forceCompleteQuest();
            }
            qm.sendNext("널 전직시켰어. 이제 더 이상 예전의 네가 아니야. 광기에 가까운 위험천만한 스킬의 세계가 펼쳐질 거야. 컨트롤하기 쉽진 않겠지만... 후후, 그 위험한 임무도 해낸 너라면 어렵지 않게 다룰 수 있으리라 믿어.");
            break;
        }
        case 3: {
            qm.dispose();
            qm.sendNextPrev("그럼 다음 수업에 보자. 그때까지 레지스탕스로서 멋지게 활약하길 기대하지.");
            break;
        }
    }
}