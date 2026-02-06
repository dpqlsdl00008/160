function act() {
    var rState = 0;
    for (i = 0; i < 7; i++) {
        if (!rm.getMap().getAllReactorsThreadsafe().get(i).getName().contains("moonflower")) {
            continue;
        }
        rState += rm.getMap().getAllReactorsThreadsafe().get(i).getState();
    }
    rm.gainItem(4001453, -1);
    rm.showInfoOnScreen("달맞이꽃 " + rState + " / 6");
    if (rState == 6) {
        rm.getMap().setMobGen(9300058, false);
        rm.getMap().setMobGen(9300059, false);
        rm.getMap().setMobGen(9300062, true);
        rm.getMap().setMobGen(9300063, true);
        rm.getMap().setMobGen(9300064, true);
        rm.getMap().killAllMonsters(true);
        rm.getMap().respawn(false);
        rm.spawnMonster(9300061, -183, -433);
        rm.floatMessage("월묘는 공격을 받지 않는 상태에서만 떡을 찧을 수 있다네! 월묘의 떡 10개를 모아주게!", 5120016);
        rm.getPlayer().getClient().sendPacket(Packages.tools.packet.CField.achievementRatio(50));
    }
}