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
            qm.sendNext("많은 사람들이 자네 같았으면 좋겠구먼. 이젠 사람들을 믿을 수 없어. 갑자기 나타난 괴물들에게서 난 홀로 한 시도 쉬지 않고 정원을 지켰다네...");
            break;
        }
        case 1: {
            qm.sendNextPrevS("강시는 정말 위험해요! 감염이 될 수도 있다구요! 모두가 그것들 때문에 안전한 예원 정원으로 떠난거에요. 그들이 정말 원해서 달아난게 아니에요!", 2);
            break;
        }
        case 2: {
            qm.sendNextPrev("자네 말이 맞을지도 모르지... 그렇지만 이 농장과 난 한 평생을 지내온 몸이라 떠날 수는 없다네.");
            break;
        }
        case 3: {
            qm.askAcceptDecline("어쨋든 나는 이곳에 남아 꾸준히 농장을 가꾸겠네. 필요한 작물이 있다면 언제든지 찾아오게. 참, 젠 황에게 내 안부를 대신 전해주겠나?");
            break;
        }
        case 4: {
            qm.sendNext("조심히 돌아가게.");
            break;
        }
        case 5: {
            qm.dispose();
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
            qm.sendNext("돌아왔군! 도밍 첸은 같이 오지 않은 겐가? 뭐... 기대하긴 했지만 놀랄 일도 아니지.");
            break;
        }
        case 1: {
            qm.sendNextPrev("그 고집불통 늙은이가 괜찮다는 걸 알았으니 됐다해. 고맙다해.\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 1,705,983 exp");
            break;
        }
        case 2: {
            qm.dispose();
            qm.forceCompleteQuest();
            qm.gainExp(1705983);
            break;
        }
    }
}