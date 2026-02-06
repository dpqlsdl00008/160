var status = -1;

function action(mode, type, selection) {
    status++;
    if (cm.getInfoQuest(23999).indexOf("exp4=1") != -1) {
        cm.sendNext("어차피 들킨 거 사탕이나 먹어야겟다.");
        cm.dispose();
        return;
    }
    switch (status) {
        case 0: {   
            cm.sendNext("우웅? 들켯잖아? 내 작은 몸을 이렇게 쉽게 찾아내다니, 만만치 않은걸?\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 3 exp");
            break;
        }
        case 1: {
            cm.dispose();
            cm.gainExp(3);
            if (cm.getInfoQuest(23999).equals("")) {
                cm.updateInfoQuest(23999, "exp4=1");
            } else {
                cm.updateInfoQuest(23999, cm.getInfoQuest(23999) + ";exp4=1");
            }
            break;
        }
    }
}