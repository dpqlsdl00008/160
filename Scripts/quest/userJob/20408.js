var status = -1;

function start(mode, type, selection) {
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
            qm.sendNext("#h0#.. 먼저 감사의 인사부터 드릴게요. 고마워요. 당신이 아니었더라면 전.. 검은 마녀의 저주로부터 무사할 수 없었을 거예요. 정말 감사 드려요.");
            break;
        }
        case 1: {
            qm.sendNextPrev("이번 일로 확실해진 사실이 하나 있어요. 그것은.. 당신이 그 동안 누구보다 열심히 노력하여 강해졌다는 것. 그리고 기사단을 위해 그 힘을 최선을 다해 사용해 주었다는 것. 그것이지요.");
            break;
        }
        case 2: {
            qm.sendAcceptDecline("그간 당신의 노력과 업적, 공로를 인정해.. 당신께 새로운 직위를 내리려 해요. 부디.. 받아주시겠어요?");
            break;
        }
        case 3: {
            qm.dispose();
            qm.changeJob(qm.getJob() + 1);
            qm.teachSkill(10001005, 1, 1);
            qm.forceCompleteQuest();
            qm.sendNext("뭐... 농담처럼 말하긴 했지만, 당신이 기사단의 실력자 중 하나인건 분명 사실입니다. 그러니 이러한 중요한 임무를 맡긴 거고요. 활약 기대하겠습니다.");
            break;
        }
    }
}