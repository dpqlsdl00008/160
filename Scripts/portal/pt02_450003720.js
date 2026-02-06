function enter(pi) {
    if (pi.getPlayer().getOneInfoQuest(34302, "try").equals("")) {
        pi.getPlayer().updateOneInfoQuest(34302, "try", "0");
    }
    var v1 = parseInt(pi.getPlayer().getOneInfoQuest(34302, "try"));
    if (v1 > 4) {
        pi.warp(450003100, "out00");
        return;
    }
    pi.getPlayer().getClient().sendPacket(Packages.tools.packet.CField.NPCPacket.getNPCTalk(3003201, 0, "\r\n#Cgray#(오른쪽 안개 너머로 나가봐야 한다.)#k", "00 01", 2, 0));
}