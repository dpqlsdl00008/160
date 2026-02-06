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
                case 923040100: {
                    cm.floatMessage("제 목소리 들리세요? 흉폭해진 몬스터를 모두 물리쳐주세요!!", 5120052);
                    cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CField.achievementRatio(0));
                    break;
                }
                case 923040200: {
                    cm.floatMessage("준비해 온 산소가 부족해요. 몬스터를 처치하고 공기방울 20개를 구해주세요!", 5120052);
                    cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CField.achievementRatio(25));
                    break;
                }
                case 923040300: {
                    cm.floatMessage("갑자기 몬스터들이 공격하기 시작해요! 앞으로 1분간 저를 지켜주세요!!", 5120052);
                    cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CField.achievementRatio(50));
                    break;
                }
                case 923040400: {
                    cm.floatMessage("아니 저렇게 커다란 물고기가... 저것이 바로 피아누스? 그것도 2마리나!! 우리를 공격해요! 물리쳐주세요!!", 5120052);
                    cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CField.achievementRatio(75));
                    break;
                }
            }
        }
    }
}