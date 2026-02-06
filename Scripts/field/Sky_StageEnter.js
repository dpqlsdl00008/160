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
                case 240080100: {
                    cm.floatMessage("모든 플라잉 호크와 플라잉 이글을 퇴치하라!", 5120026);
                    cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CField.achievementRatio(0));
                    break;
                }
                case 240080200: {
                    cm.floatMessage("플라잉 와이번과 플라잉 그리폰을 퇴치하라!", 5120026);
                    cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CField.achievementRatio(20));
                    break;
                }
                case 240080300: {
                    cm.floatMessage("드래고니카를 무찌르고, 천둥의 둥지로 진입하라!", 5120026);
                    cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CField.achievementRatio(40));
                    break;
                }
                case 240080400: {
                    cm.floatMessage("3분 내에 모든 파티원들이 장애물들을 돌파하고, 천공의 둥지로 진입하라!", 5120026);
                    cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CField.achievementRatio(60));
                    break;
                }
                case 240080500: {
                    cm.floatMessage("미나르 마을을 괴롭히는 드래곤 라이더를 무찔러라!", 5120026);
                    cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CField.achievementRatio(80));
                    break;
                }
            }
        }
    }
}