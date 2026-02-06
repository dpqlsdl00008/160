var status = -1;

function start(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            qm.dispose();
            return;
        }
        if (status == 2) {
            qm.sendNext("... 뭐? 거절이라고? 이 자식! 주인이라고 좀 신경 써주려고 했더니 필요 없다 이거야?");
            qm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            qm.sendNext("이 봐, 아란. 얼마 전 부터 내 옆에서 왠 심상치 않은 기운이 느껴지는데? 혹시 누군가 아주 강력한 자와 함께 있는 거야?");
            break;
        }
        case 1: {
            qm.sendNextPrevS("아니, 아무도 없다.", 2);
            break;
        }
        case 2: {
            qm.sendAcceptDecline("그래? 그럼 이 기운은 뭐지? 너하고 굉장히 비슷해... 이거 신경 쓰이는 걸. #b리엔#k으로 잠깐만 와봐. 한 번 확인해 봐야겠어.");
            break;
        }
        case 3: {
            qm.dispose();
            qm.forceStartQuest();
            qm.sendNext("직접 보면 이 기운이 뭔 지 알 수 있겠지.");
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
            qm.sendNext("뭐야, 역시 네 옆에 아주 강력한 녀석이 하나 붙어 있었잖아? 왜 모르는 척 한 거야?");
            break;
        }
        case 1: {
            qm.sendNextPrevS("강력한 녀석이라니 누굴 말하는 건가?", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("누구라니. 네 옆에 있는 그 녀석. 그 늑대 녀석 말이야. 이름이 류호라고 한던가?");
            break;
        }
        case 3: {
            qm.sendNextPrevS("류호가 강하다고?", 2);
            break;
        }
        case 4: {
            qm.sendNextPrev("뭐야? 몰랐던 거야? 그 녀석 은혈랑이잖아? 은혈랑은 주인과 함께 성장하는 늑대. 주인이 강해지면 함께 강해지지.");
            break;
        }
        case 5: {
            qm.sendNextPrevS("그래서 류호가 강하다는 건가...", 2);
            break;
        }
        case 6: {
            qm.sendNextPrev("그래. 네 영향을 받아 아주 강하면서도 너와 비슷한 기운을 풍기는군... 이제 각성해도 좋을 것 같은데.");
            break;
        }
        case 7: {
            qm.sendNextPrevS("각성이라니 그건 뭔가?", 2);
            break;
        }
        case 8: {
            qm.sendNextPrev("일정한 힘을 갖춘 은혈랑만이 할 수 있는 변신... 이랄까. 너 예전엔 이런 거 다 알고 있었잖아.");
            break;
        }
        case 9: {
            qm.sendNextPrevS("기억나지 않는다.", 2);
            break;
        }
        case 10: {
            qm.sendNextPrev("하아... 그럼 은혈랑을 각성 시킬 수도 없겠군. 뭐, 어쩔 수 없지. 주인의 부족함은 이 내가 채워 줘야 겠군.");
            break;
        }
        case 11: {
            qm.sendNextPrev("은혈랑 류호를 각성시켰다. 아마도 은혈랑의 역사를 뒤져봐도 이 녀석만큼 강한 녀석은 없을 거야. 네 늑대니까 당연한 일이지만 말이야. 예전 류호도 이 정도는 아니였던 것 같은데...");
            break;
        }
        case 12: {
            qm.sendNextPrevS("... 예전 류호?", 2);
            break;
        }
        case 13: {
            qm.sendNextPrev("예전에 네가 데리고 있던 류호 말이야? ... 뭐야. 몰랐던 거야? 알고서 일부러 같은 이름으로 지은 줄 알았는데 아니였던 건가? ... 류호를 소중히 여겨. 주인을 절대 배신하지 않는 고고한 늑대... 분명 너에게 도움이 될 거야. 예전에 그랬던 것 처럼.");
            break;
        }
        case 14: {
            qm.dispose();
            qm.gainItem(1902018, 1);
            qm.forceCompleteQuest();
            break;
        }
    }
}