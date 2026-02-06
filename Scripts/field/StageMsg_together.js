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
                case 910340100: {
                    cm.floatMessage("파티원들은 클로토를 찾아가서 그녀가 말한 갯수만큼 리게이터를 물리치고 쿠폰을 모아라!", 5120017);
                    cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CField.achievementRatio(0));
                    break;
                }
                case 910340200: {
                    cm.floatMessage("다음 단계로 가는 문을 열 수 있는 줄 3개를 찾아서 매달려라!", 5120017);
                    cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CField.achievementRatio(20));
                    break;
                }
                case 910340300: {
                    cm.floatMessage("다음 단계로 가는 문을 열 수 있는 발판 3개를 찾아라.", 5120017);
                    cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CField.achievementRatio(40));
                    break;
                }
                case 910340400: {
                    cm.floatMessage("사악한 커즈아이를 모두 해치워라!!", 5120017);
                    cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CField.achievementRatio(60));
                    break;
                }
                case 910340500: {
                    cm.floatMessage("킹슬라임을 해치워라!!", 5120017);
                    cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CField.achievementRatio(80));
                    break;
                }
            }
        }
    }
}