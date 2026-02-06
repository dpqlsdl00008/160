var status = -1;

function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
        status++;
    } else {
        cm.dispose();
        return;
    }
    switch (status) {
        case 0: {
            var say = "몬스터 파크는 잘 즐겼나? 보상을 챙겨 주었네, 크크.\r\n#e";
            v1 = 0;
            if (cm.getParty() != null) {
                v1 = cm.getParty().getMembers().size();
            }
            say += "\r\n아이템 보상 : #i" + 4310020 + "# #d#z" + 4310020 + "# " + v1 + "개#k";
            v3 = 3;
            if (cm.getMapId() < 954010500) {
                v3 = 5;
            }
            if (cm.getMapId() < 953020500) {
                v3 = 10;
            }
            v2 = (cm.getPlayer().getNeededExp() / 100) * v3;
            if (cm.getParty() != null) {
                v2 += ((cm.getPlayer().getNeededExp() / 100) * cm.getParty().getMembers().size());
            }
            say += "\r\n경험치 보상 : #d" + cm.getPlayer().getNum(v2) + "#k";
            say += "\r\n\r\n#r오늘 입장 횟수 1회#k를 사용 하였습니다.#n#k";
            cm.sendNextS("\r\n" + say, 4, 9071000, 9071000);
            break;
        }
        case 1: {
            cm.dispose();
            Packages.handling.channel.handler.DueyHandler.addNewItemToDb(4310020, 1, cm.getPlayer().getId(), "몬스터 파크", "", true);
            cm.getClient().sendPacket(Packages.tools.packet.CField.receiveParcel("몬스터 파크", true));
            cm.gainExp(v2);
            cm.warp(951000000, 0);
            if (v3 != 2000004 && v3 != 2000005) {
                Packages.handling.world.World.Broadcast.broadcastMessage(Packages.tools.packet.CWvsContext.serverNotice(6, v3, cm.getPlayer().getName() + "님께서 몬스터 파크에서 " + "[{" + Packages.server.MapleItemInformationProvider.getInstance().getItemInformation(v3).name + "}]" + " " + v4 + "개를 획득하였습니다.", false));
            }
            break;
        }
    }
}