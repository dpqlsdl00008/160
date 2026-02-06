function start() {
    if ((cm.getJob() > 1000 && cm.getJob() < 1513) && cm.getLevel() < 21) {
        cm.useItem(2022458);
    }
    cm.sendNext("\r\n메이플 월드를 위해 더욱 강해지길...");
    cm.dispose();
}