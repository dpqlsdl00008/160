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
                case 926110000: {
                    cm.floatMessage("연구실을 수색해서 숨겨진 문을 찾아주세요!", 5120022);
                    cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CField.achievementRatio(0));
                    break;
                }
                case 926110001: {
                    cm.floatMessage("모든 몬스터를 퇴치해 주세요!", 5120022);
                    cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CField.achievementRatio(15));
                    break;
                }
                case 926110100: {
                    cm.floatMessage("몬스터를 퇴치하고 얻은 액체로 금이 간 비커를 채워주세요!", 5120022);
                    cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CField.achievementRatio(30));
                    break;
                }
                case 926110200: {
                    var say = "몬스터로부터 카드 키를 얻은 후, 연구실에 들어가서 실험 자료를 찾아주세요!";
                    if (cm.haveItem(4001134, 1) == true) {
                        say = "알카드노의 실험 자료를 획득하셨습니다. 줄리엣에게 실험 자료를 가져다 주세요.";
                    }
                    if (cm.haveItem(4001135, 1) == true) {
                        say = "제뉴미스트의 실험 자료를 획득하셨습니다. 줄리엣에게 실험 자료를 가져다 주세요.";
                    }
                    cm.floatMessage(say, 5120022);
                    cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CField.achievementRatio(45));
                    break;
                }
                case 926110201:
                case 926110202:
                case 926110203: {
                    cm.floatMessage("연구실에서 실험 자료를 찾아 줄리엣에게 가져다 주세요.", 5120022);
                    cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CField.achievementRatio(60));
                    break;
                }
                case 926110300: {
                    cm.floatMessage("4개의 보안 통로를 통과하세요!", 5120022);
                    cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CField.achievementRatio(75));
                    break;
                }
                case 926110301:
                case 926110302:
                case 926110303:
                case 926110304: {
                    cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CField.achievementRatio(75));
                    break;
                }
                case 926110400:
                case 926110401: {
                    cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CField.achievementRatio(90));
                    break;
                }
            }
        }
    }
}