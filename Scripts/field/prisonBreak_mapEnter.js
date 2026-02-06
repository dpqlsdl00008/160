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
                case 921160100: {
                    cm.floatMessage("쉿! 조용히 장애물들을 피해서 탑을 벗어나셔야 합니다.", 5120053);
                    cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CField.achievementRatio(12));
                    break;
                }
                case 921160200: {
                    cm.floatMessage("경비병들을 모두 물리치셔야 해요. 그렇지 않으면 그들이 다른 경비병까지 불러올꺼에요.", 5120053);
                    cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CField.achievementRatio(24));
                    break;
                }
                case 921160300: {
                    cm.floatMessage("감옥으로의 접근을 막기 위해 그들이 미로를 만들어 놨어요. 공중 감옥으로 통하는 문을 찾으세요!", 5120053);
                    cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CField.achievementRatio(36));
                    break;
                }
                case 921160350: {
                    var pMember = cm.getParty().getMembers();
                    if (pMember != null) {
                        var it = pMember.iterator();
                        while (it.hasNext()) {
                            var cUser = it.next();
                            var ccUser = cm.getChannelServer().getMapFactory().getMap(cm.getMapId()).getCharacterByName(cUser.getName());
                            if (ccUser != null) {
                                cm.showFieldEffect(true, "quest/party/clear");
                                cm.playSound(true, "Party1/Clear");
                                ccUser.dropMessage(6, "공중 감옥으로 통하는 문을 찾았습니다!");
                            }
                        }
                    }
                    break;
                }
                case 921160400: {
                    cm.floatMessage("문을 지키고 있는 경비병들을 모두 처치하세요!", 5120053);
                    cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CField.achievementRatio(48));
                    break;
                }
                case 921160500: {
                    cm.floatMessage("이것이 마지막 장애물이군요. 장애물을 통과해 공중 감옥으로 와주세요.", 5120053);
                    cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CField.achievementRatio(60));
                    break;
                }
                case 921160600: {
                    cm.floatMessage("경비병을 처치하고 감옥 열쇠를 되찾아 감옥 문을 열어주세요.", 5120053);
                    cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CField.achievementRatio(72));
                    break;
                }
                case 921160700: {
                    cm.floatMessage("교도관을 물리치고 우리에게 자유를 되찾아주세요!", 5120053);
                    cm.getPlayer().getClient().sendPacket(Packages.tools.packet.CField.achievementRatio(84));
                    break;
                }
            }
        }
    }
}