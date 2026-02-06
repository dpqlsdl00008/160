var status = -1;

function action(mode, type, selection) {
    status++;
    if (cm.getInfoQuest(23999).indexOf("exp2=1") != -1) {
        cm.sendNext("준하고 반은 찾은 거야? 특히 반은 잘 숨으니까 열심히 찾아야 할껄?");
        cm.dispose();
        return;
    }
    switch (status) {
        case 0: {   
            cm.sendNext("어라? 들킨 거야? 에헤헤.. 내가 너무 찾기 쉬운데 숨었나?");
            break;
        }
        case 1: {
            cm.sendNextPrev("준하고 반은 찾은 거야? 반은 어지간하면 찾기 어려울걸? 찾을 수 있는데는 전부 찾아봐.\r\n\r\n#fUI/UIWindow.img/QuestIcon/8/0# 5 exp");
            break;
        }
        case 2: {
            cm.dispose();
            cm.gainExp(5);
            if (cm.getInfoQuest(23999).equals("")) {
                cm.updateInfoQuest(23999, "exp2=1");
            } else {
                cm.updateInfoQuest(23999, cm.getInfoQuest(23999) + ";exp2=1");
            }
            break;
        }
    }
}