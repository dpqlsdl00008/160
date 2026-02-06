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
            qm.sendSimple("응? 무슨 일이니, 에반? 또 아빠 일이라도 도와주러 온 거니? 응? 이상한 돼지를 네가 혼내줬다고? 이런! 다치지는 않았니?\r\n#L0##b거뜬해요! 그 녀석들 별거 아니던걸요?#k");
            break;
        }
        case 1: {
            qm.sendAcceptDecline("휴우... 그럼 다행이구나. 하지만 위험할 수도 있으니 앞으로는 조심하렴. 아, 그보다... 마침 잘 됐구나. 아빠 심부름 하나만 해주지 않겠니?");
            break;
        }
        case 2: {
            if (qm.haveItem(4032455) == false) {
                qm.gainItem(4032455, 1);
            }
            if (qm.isQuestActive(22510) == false) {
                qm.forceStartQuest();
            }
            qm.sendNext("이상한 돼지들 때문에 차질이 생겨서 납품하기로 한 돼지고기들을 제때 가져다 주기 어려울 것 같다고 #b헤네시스 마을#k의 #b장로 스탄#k에게 전해 주렴.");
            break;
        }
        case 3: {
            qm.sendNextPrev(" 방금 건네준 편지에 내용은 다 쓰여 있으니 전해 주기만 하면 된단다. 원래대로라면 내가 갔겠지만, 에반 네가 위험한 돼지들을 퇴치할 정도로 강하다니 부탁해도 괜찮을 것 같구나.");
            break;
        }
    }
}