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
            qm.sendNext(""...음. 너 표정이 창백한걸? 혹시 귀신이라도... 봤구나! 우리 스승님의 귀신! 여기 자주 나오시거든. 사람들이 무서워하니 좀 안 그러셨으면 좋겠는데.");
            break;
        }
        case 1: {
            qm.sendNextPrev("응? 스승님이 너한테 유품을 건네주라고 했다고? 오, 그런 말은 잘 안 하시는데 왠일이지? 네가 마음에 드셨나 보다야. 잠깐만 기다려봐.");
            break;
        }
        case 2: {
            qm.dispose();
            qm.forceCompleteQuest();
            break;
        }
    }
}