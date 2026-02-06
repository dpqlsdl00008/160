function enter(pi) {
    if (pi.isQuestActive(2324) == true && pi.isQuestFinished(2324) == false) {
        pi.getPlayer().getClient().sendPacket(Packages.tools.packet.CField.NPCPacket.getNPCTalk(1300003, 0, "\r\n이쯤에서 #b덩굴 가시 제거제#k를 사용하면 될 것 같다.", "00 01", 2, 0));
    }
}