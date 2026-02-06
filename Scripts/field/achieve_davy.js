var status = -1;

function start() {
    status = -1;
    action (1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        if (status == 0) {
            cm.dispose();
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            cm.dispose();
            switch (cm.getMapId()) {
                case 925100000: {
                    cm.floatMessage("제한된 시간 내에 모든 몬스터를 퇴치하고 해적선에 탑승하게!", 5120020);
                    cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CField.achievementRatio(0));
                    break;
                }
                case 925100100: {
                    cm.floatMessage("이 곳은 데비존의 봉인이 있는 곳이네. 봉인을 풀기 위해서는 내게 말을 걸어주게.", 5120020);
                    cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CField.achievementRatio(25));
                    break;
                }
                case 925100400: {
                    cm.floatMessage("제한된 시간 내에 몬스터로부터 열쇠를 얻어서 모든 문을 닫게!", 5120020);
                    cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CField.achievementRatio(50));
                    break;
                }
                case 925100500: {
                    cm.floatMessage("자, 이제 마지막이네. 빨간코 해적단의 선장 데비존을 물리치게!", 5120020);
                    cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CField.achievementRatio(75));
                    break;
                }
            }
        }
    }
}