function start() {
    cm.getPlayer().getStat().heal(cm.getPlayer());
    cm.sendNext("\r\n다, 다친 곳이 있으면 사양말고 말씀해 주세요! 브, 블랙윙을 처단하기 위해 다친 거라면 치, 치료해 드려야죠!");
    cm.dispose();
}