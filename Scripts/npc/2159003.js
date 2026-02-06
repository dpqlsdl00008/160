var status = -1;

function action(mode, type, selection) {
    status++;
    if (cm.getInfoQuest(23999).indexOf("exp1=1") != -1) {
        cm.sendNext("울라카하고 반은 찾은 거야? 특히 반은 잘 숨으니까 열심히 찾아야 할껄?");
        cm.dispose();
        return;
    }
    switch (status) {
        case 0: {
            cm.sendNext("앗! 걸렷다 칫.");
            break;
        }
        case 1: {
            cm.sendNextPrev("끄응.. 수레 안으로 더 숨으려고 했는데 머리가 안 들어가...");
            break;
        }
        case 2: {
            cm.sendNextPrev("울라카하고 반은 찾은거야? 특히 반은 잘 숨으니까 열심히 찾아야 할걸? \r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 5 exp");
            break;
        }
        case 3: {
            cm.dispose();
            cm.gainExp(5);
            if (cm.getInfoQuest(23999).equals("")) {
                cm.updateInfoQuest(23999, "exp1=1");
            } else {
                cm.updateInfoQuest(23999, cm.getInfoQuest(23999) + ";exp1=1");
            }
            break;
        }
    }
}