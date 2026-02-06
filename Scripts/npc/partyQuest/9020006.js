var status = -1;

function start() {
    action (1, 0, 0);
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
            reward = false;
            var say = "이대로 포기하고 나가시는 건가요?";
            if (cm.getMapId() == 921160700) {
                if (cm.getMap().getMonsterById(9300454) == null) {
                    say = "도와주셔서 정말 감사드립니다. 덕분에 이 곳을 벗어날 수 있게 되었습니다.";
                    reward = true;
                }
            }
            say += "\r\n#L1##d1. 이곳에서 나가고 싶습니다.";
            cm.sendSimple(say);
            break;
        }
        case 1: {
            if (!reward) {
                cm.dispose();
                cm.warp(921160000, 0);
                return;
            }
            cm.sendNext("\r\n정말 감사했습니다. 여전히 위험한 곳이니 조심해서 가세요.");
            break;
        }
        case 2: {
            cm.dispose();
            var v1 = 1;
            if (cm.getParty() != null) {
                v1 = cm.getParty().getMembers().size();
            }
            //Packages.handling.channel.handler.DueyHandler.addNewItemToDb(4001534, 5, cm.getPlayer().getId(), "파티 퀘스트", "", true);
            Packages.handling.channel.handler.DueyHandler.addNewItemToDb(4310050, 1, cm.getPlayer().getId(), "파티 퀘스트", "", true);
            cm.getClient().sendPacket(Packages.tools.packet.CField.receiveParcel("탈출", true));
            cm.gainExp(2999999);
            cm.warp(921160000, 0);
            break;
        }
    }
}