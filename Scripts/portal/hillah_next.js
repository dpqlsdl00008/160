function enter(pi) {
    var eScript = pi.getEventManager("expedition_hillah");
    if (eScript == null) {
        pi.getPlayer().dropMessage(1, "error");
        return;
    }
    if ((pi.getMapId() == 262030100 && eScript.getProperty("state").equals("1")) || (pi.getMapId() == 262030200 && eScript.getProperty("state").equals("2"))) {
        if (pi.getMonsterCount(pi.getMapId()) != 0) {
            pi.getMap().broadcastMessage(Packages.tools.packet.CWvsContext.getTopMsg("힐라가 있는 탑 최상층으로 가기 위해서는 스켈레톤 나이트들을 모두 물리쳐야 합니다."));
        } else {
            eScript.setProperty("state", pi.getMapId() == 262030100 ? "2" : "3");
            pi.getMap().broadcastMessage(Packages.tools.packet.CWvsContext.getTopMsg("블러드투스가 우리의 침입을 눈치챘습니다!!! 블러드투스를 물리치세요."));
            pi.summonEffect(43, 874, 196);
            pi.spawnMonster(8870003, 3, 874, 196)
        }
    } else {
        if (pi.getMonsterCount(pi.getMapId()) != 0) {
            pi.getMap().broadcastMessage(Packages.tools.packet.CWvsContext.getTopMsg("블러드투스의 방해로 다음 장소로 이동 할 수 없습니다."));
        } else {
            pi.warp(pi.getMapId() + 100, "out00");
        }
    }
}