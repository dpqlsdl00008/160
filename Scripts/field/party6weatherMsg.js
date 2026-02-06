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
                case 930000000: {
                    cm.floatMessage("중앙의 포탈을 타고 입장해. 지금 너에게 변신 마법을 걸게.", 5120023);
                    break;
                }
                case 930000010: {
                    cm.floatMessage("본인이 누군지 헷갈리지 않도록 자신의 모습을 확인 해!", 5120023);
                    cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CField.achievementRatio(0));
                    break;
                }
                case 930000100: {
                    cm.floatMessage("트리로드 때문에 숲이 오염됬어. 트리로드를 모두 없애줘!", 5120023);
                    cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CField.achievementRatio(17));
                    break;
                }
                case 930000200: {
                    cm.floatMessage("중앙의 웅덩이 위에서 몬스터를 없앤 후 웅덩이에서 나온 희석된 독으로 가시 덤불을 없애!", 5120023);
                    cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CField.achievementRatio(34));
                    break;
                }
                case 930000300: {
                    cm.floatMessage("다들 어디 가버린거야? 포탈을 타고 내가 있는 곳까지 와!", 5120023);
                    cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CField.achievementRatio(51));
                    break;
                }
                case 930000400: {
                    cm.floatMessage("나에게 정화의 구슬을 받은 다음 몬스터들을 캐치해서 몬스터 구슬 20개를 파티장이 가져와!", 5120023);
                    cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CField.achievementRatio(0));
                    break;
                }
                case 930000500: {
                    cm.floatMessage("괴인의 책상 앞에 있는 상자들을 열고 보라색 마력석을 가져와!", 5120023);
                    cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CField.achievementRatio(68));
                    break;
                }
                case 930000600: {
                    cm.floatMessage("괴인의 제단 위에 보라색 마력석을 올려놔 봐!", 5120023);
                    cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CField.achievementRatio(85));
                    break;
                }
            }
        }
    }
}