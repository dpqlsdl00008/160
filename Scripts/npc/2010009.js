var status = -1;

function start() {
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
        if (status == 2 && v1 == 1) {
            status = 0;
            action(1, 0, v2);
            return;
        }
        if (status == 3 && v2 == 1) {
            status = 1;
            action(1, 0, v3);
            return;
        }
        status--;
    }
    switch (status) {
        case 0: {
            var say = "안녕하세요? 레나리우라고 해요.#d";
            say += "\r\n#L0#길드 연합이 무엇인지 알려주세요.";
            say += "\r\n#L1#길드 연합을 만들려면 어떻게 해야 돼요?";
            say += "\r\n#L2#길드 연합을 만들고 싶어요.";
            say += "\r\n#L3#길드 연합의 길드 수를 늘리고 싶어요.";
            say += "\r\n#L4#길드 연합을 해체하고 싶어요.";
            cm.sendSimple(say);
            break;
        }
        case 1: {
            v1 = selection;
            switch (selection) {
                case 0: {
                    cm.sendNext("\r\n여러 개의 길드가 서로 모여서 만든 모임을 길드 연합이라고 해요. 저는 이렇게 만들어진 길드 연합을 관리하는 일을 하고 있답니다.");
                    break;
                }
                case 1: {
                    cm.sendNext("\r\n길드 연합을 만들려면 길드장 2명이 파티를 맺고 있어야 해요. 여기서 파티장이 길드 연합장이 된답니다.");
                    break;
                }
                case 2: {
                    if (cm.getParty() == null) {
                        cm.dispose();
                        cm.sendNext("\r\n파티원이 2명 일 때만 길드 연합을 등록 할 수 있어요.");
                        return;
                    }
                    if (cm.isLeader() == false) {
                        cm.dispose();
                        cm.sendNext("\r\n파티원이 2명 일 때만 길드 연합을 등록 할 수 있어요.");
                        return;
                    }
                    pMember = cm.getParty().getMembers();
                    if (pMember == null) {
                        cm.dispose();
                        cm.sendNext("\r\n파티원이 2명 일 때만 길드 연합을 등록 할 수 있어요.");
                        return;
                    }
                    if (pMember.size() != 2) {
                        cm.dispose();
                        cm.sendNext("\r\n파티원이 2명 일 때만 길드 연합을 등록 할 수 있어요.");
                        return;
                    }
                    if (cm.getPartyMembers().get(0).getGuildId() < 1) {
                        cm.dispose();
                        cm.sendNext("\r\n길드 마스터만 길드 연합을 등록 할 수 있어요.");
                        return;
                    }
                    if (cm.getPartyMembers().get(0).getGuildRank() > 1) {
                        cm.dispose();
                        cm.sendNext("\r\n길드 마스터만 길드 연합을 등록 할 수 있어요.");
                        return;
                    }
                    if (cm.getPartyMembers().get(1).getGuildId() < 1) {
                        cm.dispose();
                        cm.sendNext("\r\n길드 마스터만 길드 연합을 등록 할 수 있어요.");
                        return;
                    }
                    if (cm.getPartyMembers().get(1).getGuildRank() > 1) {
                        cm.dispose();
                        cm.sendNext("\r\n길드 마스터만 길드 연합을 등록 할 수 있어요.");
                        return;
                    }
                    if (cm.getGuild(cm.getPlayer().getGuildId()).getAllianceId() > 0) {
                        cm.dispose();
                        cm.sendNext("\r\n한 길드 연합에만 길드 연합을 등록 할 수 있어요.");
                        return;
                    }
                    if (cm.getGuild(cm.getPartyMembers().get(1).getGuildId()).getAllianceId() > 0) {
                        cm.dispose();
                        cm.sendNext("\r\n한 길드 연합에만 길드 연합을 등록 할 수 있어요.");
                        return;
                    }
                    cm.sendYesNo("어머~ 길드 연합을 만드시겠어요? 길드 연합을 등록하려면 #b5,000,000 메소#k가 필요해요.");
                    break;
                }
                case 3: {
                    if (cm.getPlayer().getGuildRank() != 1 || cm.getPlayer().getAllianceRank() != 1) {
                        cm.dispose();
                        cm.sendNext("\r\n길드 연합장만 길드 수를 늘릴 수 있어요.");
                        return;
                    }
                    cm.sendYesNo("길드 수를 늘리는데에는 수수료 #b5,000,000 메소#k가 소비됩니다. 정말 만들고 싶으세요?");
                    break;
                }
                case 4: {
                    if (cm.getPlayer().getGuildRank() != 1 || cm.getPlayer().getAllianceRank() != 1) {
                        cm.dispose();
                        cm.sendNext("\r\n길드 연합장만 길드 연합을 해체 할 수 있어요.");
                        return;
                    }
                    aList = Packages.handling.world.World.Alliance.getAlliance(cm.getGuild().getAllianceId());
                    var say = "길드 연합을 정말 해체하고 싶으세요? 신중하게 결정해 주시길 바랍니다.#d";
                    for (i = 0; i < aList.getCapacity(); i++) {
                        gList = Packages.handling.world.World.Guild.getGuild(aList.getGuildId(i));
                        say += "\r\n#L" + i + "#" + (i + 1) + ". " + gList.getName() + "";
                    }
                    cm.sendSimple(say);
                    break;
                }
            }
            break;
        }
        case 2: {
            v2 = v1;
            switch (v1) {
                case 0: {
                    status = -1;
                    action(1, 0, 0);
                    break;
                }
                case 1: {
                    cm.sendNextPrev("\r\n2명의 길드 마스터가 모였다면 #b5,000,000 메소#k가 필요해요. 이건 길드 연합을 등록하는 데 필요한 수수료에요.");
                    break;
                }
                case 2: {
                    cm.sendGetText("그럼 원하는 길드 연합 이름을 입력해 주세요.\r\n#Cgray#(영문 12자, 한글 6자까지)");
                    break;
                }
                case 3: {
                    cm.dispose();
                    if (cm.addCapacityToAlliance() == false) {
                        cm.sendNext("\r\n최대 길드 연합의 길드 수는 5개 까지 늘릴 수 있습니다. 또는 수수료가 부족하신 건 아닌지 확인해 주세요.");
                    }
                    break;
                }
                case 4: {
                    cGuild = Packages.handling.world.World.Guild.getGuild(aList.getGuildId(selection));
                    var say = "";
                    var con = Packages.database.DatabaseConnection.getConnection();
                    var ps = con.prepareStatement("SELECT * FROM guilds where guildid = ?");
                    ps.setString(1, cGuild.getId());
                    var rs = ps.executeQuery();
                    while (rs.next()) {
                        var ps2 = con.prepareStatement("SELECT * FROM characters WHERE id = ?");
                        leaderID = rs.getInt("leader");
                        ps2.setInt(1, leaderID);
                        var rs2 = ps2.executeQuery();
                        while (rs2.next()) {
                            leaderName = rs2.getString("name");
                        }
                        say += "#e길드#n : #b" + cGuild.getName() + "#k";
                        say += "\r\n#e길드 마스터#n : #b" + leaderName + "#k";
                    }
                    rs2.close();
                    ps2.close();
                    rs.close();
                    ps.close();
                    con.close();
                    cm.askAcceptDecline(say + "\r\n\r\n위 길드를 #r" + aList.getName() + " 연합#k에서 해체하겠습니다.");
                    break;
                }
            }
            break;
        }
        case 3: {
            v3 = v2;
            switch (v2) {
                case 1: {
                    cm.sendNextPrev("\r\n그리고 또 하나! 당연히 다른 길드 연합에 가입되어 있으면 새롭게 길드 연합을 만들지 못해요!");
                    break;
                }
                case 2: {
                    aName = cm.getText();
                    cm.sendYesNo("입력하신 길드 연합 이름은 #b" + cm.getText() + " 입니다. 정말 이 이름으로 길드 연합을 만들고 싶으세요?");
                    break;
                }
                case 4: {
                    if (sGuild.getLeaderId() != aList.getLeaderId()) {
                        cm.dispose();
                        Packages.handling.world.World.Alliance.setOldAlliance(sGuild.getId(), true, aList.getId());
                        sGuild.setAllianceId(0);
                        aList.removeGuild(sGuild.getId(), true);
                        aList.removeCapacity();
                        sGuild.broadcast(Packages.tools.MaplePacketCreator.serverNotice(1, "'" + aList.getName() + " 연합'에서 추방되었습니다."));
                        cm.sendNext("\r\n성공적으로 #b" + sGuild.getName() + " 길드#k를 #r" + aList.getName() + " 연합#k에서 추방하였습니다.");
                        return;
                    }
                    cm.askAcceptDecline("선택하신 #b" + sGuild.getName() + " 길드#k는 #r" + aList.getName() + " 연합#k의 연합장인 #b#h ##k님께서 속해져 있는 길드입니다. 해당 길드를 추방 또는 해체 시에 모든 연합은 해체됩니다.");
                    break;
                }
            }
            break;
        }
        case 4: {
            v4 = v3;
            switch (v3) {
                case 1: {
                    status = -1;
                    action(1, 0, 0);
                    break;
                }
                case 2: {
                    cm.dispose();
                    if (cm.createAlliance(aName) == false || cm.getMeso() < 5000000) {
                        cm.sendNext("\r\n사용 할 수 없는 이름이거나 이미 존재하는 이름입니다. 다른 이름을 입력해 주세요. 혹은 수수료 5,000,000 메소가 부족하신 건 아닌지도 확인해 주세요.");
                    } else {
                        cm.gainMeso(-5000000);
                    }
                    break;
                }
                case 4: {
                    cm.dispose();
                    cm.disbandAlliance();
                    break;
                }
            }
            break;
        }
    }
}