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
            cm.sendSimple("#b데비존#k을 물리치고 저를 구해주셔서 정말 감사합니다. 무엇을 도와드릴까요?\r\n#d#L0#1. 이곳에서 퇴장한다.");
            break;
        }
        case 1: {
            cm.dispose();
            //Packages.handling.channel.handler.DueyHandler.addNewItemToDb(4001455, 5, cm.getPlayer().getId(), "파티 퀘스트", "", true);
            Packages.handling.channel.handler.DueyHandler.addNewItemToDb(4310050, 1, cm.getPlayer().getId(), "파티 퀘스트", "", true);
            cm.getClient().sendPacket(Packages.tools.packet.CField.receiveParcel("해적 데비존", true));
            cm.gainExp(999999);
            cm.warp(251010404, 0);
            break;
        }
    }
}