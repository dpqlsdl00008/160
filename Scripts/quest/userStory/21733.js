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
            qm.sendNext("이봐, 어디 있어? 급한 일이 생겼어!");
            break;
        }
        case 1: {
            qm.sendNextPrevS("#b(이봐...? 트루는 항상 영웅님이라고 날 불렀던 것 같은데...)#k", 2);
            break;
        }
        case 2: {
            qm.sendAcceptDecline("아주 중요한 정보가 있어! 빨리 #b리스항구정보상점#k로 와!");
            break;
        }
        case 3: {
            qm.sendNext("정말 급하다니까! 최대한 빨리 와야해!");
            break;
        }
        case 4: {
            qm.dispose();
            qm.forceStartQuest(21762, "2");
            qm.forceStartQuest();
            break;
        }
    }
}

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
            qm.sendNext("아... 살다 보니 이런 일이 다 있군. 설마하니 인형사가 여기에 쳐들어 올 줄이야. 평소에 수련을 좀 해둘 걸 그랬나. 완전히 당해버렸네.");
            break;
        }
        case 1: {
            qm.sendNextPrevS("미안하다. 나 때문에...", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("응? 영웅님이 미안할 게 뭐가 있겠어. 녀석들이 이렇게 나올 줄 당신도 몰랐을 거 아냐. 사과할 거 없어. 아니, 오히려 이건 녀석들이 약점을 노출한 거야");
            break;
        }
        case 3: {
            qm.sendNextPrevS("약점?", 2);
            break;
        }
        case 4: {
            qm.sendNextPrev("인형사가 잃어버린 그 문서. 그것이 가짜라면 인형사가 동료까지 데리고 이렇게 성급하게 굴 리가 없지. 그 문서대로 정말 블랙윙의 목적이 빅토리아 아일랜드의 봉인석이라는 게 확실해 졌어.");
            break;
        }
        case 5: {
            qm.sendNextPrevS("그렇지만 네 위치도 노출 되었는데...", 2);
            break;
        }
        case 6: {
            qm.sendYesNo("걱정 마. 이번에는 리린이 보내기로 한 물건을 받으려고 나갔다가 봉변을 당했지만, 평소엔 이렇게 방심하지 않는다고. 정보상인을 우습게 봐서는 곤란하지. 적어도 도망칠 길 하나는 확실히 마련해 두니까 말이야. 이제 좀 믿음 가?");
            break;
        }
        case 7: {
            qm.sendNext("블랙윙은 아무리 날뛰어봤자 당신이 강해지는 걸 막을 수는 없을 거야. 계속 강해지자고. 검은 마법사를 물리칠 때까지. 나도 최선을 다해 정보를 모아 볼게.");
            break;
        }
        case 8: {
            qm.dispose();
            qm.teachSkill(21100000, qm.getPlayer().getTotalSkillLevel(21100000), 20);
            qm.forceCompleteQuest();
            break;
        }
    }
}