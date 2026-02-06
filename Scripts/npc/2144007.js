function start() {
    if (cm.isQuestActive(31173) == true && cm.haveItem(4033081, 1) == false) {
        cm.gainItem(4033081, 1);
        cm.sendNextS("\r\n다행히 알은 무사하군. 알이 다치지 않도록 잘 돌아 와 주게.", 4, 2144006);
    }
    cm.dispose();
}