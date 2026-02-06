var status = -1;

function action(mode, type, selection) {
    status++;
    if (cm.getInfoQuest(23999).indexOf("exp3=1") != -1) {
        cm.sendNext("에헤헤... 더 잘 숨을껄.");
        cm.dispose();
        return;
    }
    switch (status) {
        case 0: {   
            cm.sendNext("어머, 들켯다! 우와... 엄청 잘 찾으시네요. 대단해요!\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 3 exp");
            break;
        }
        case 1: {
            cm.dispose();
            cm.gainExp(3);
            if (cm.getInfoQuest(23999).equals("")) {
                cm.updateInfoQuest(23999, "exp3=1");
            } else {
                cm.updateInfoQuest(23999, cm.getInfoQuest(23999) + ";exp3=1");
            }
            break;
        }
    }
}