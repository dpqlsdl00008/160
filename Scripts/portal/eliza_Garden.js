function enter(pi){
    if (pi.isQuestFinished(3114) == true) {
        pi.getPlayer().dropMessage(5, "무너진 기둥의 잔해 때문에 더 이상 들어 갈 수 없다.");
        return;
    }
    pi.warp(920020000, "out00");
    pi.getPlayer().dropMessage(5, "무너진 기둥의 잔해 사이로 나를 이끄는 힘이 느껴진다.");
}