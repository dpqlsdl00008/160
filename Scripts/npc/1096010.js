function start() {
    if (cm.isQuestActive(2566) == true && cm.haveItem(4032985, 1) == false) {
        cm.gainItem(4032985, 1);
        cm.getPlayer().dropMessage(-1, "발화 장치를 발견하였다. 스토너에게 가져다 주자.");
    }
    cm.dispose();
}