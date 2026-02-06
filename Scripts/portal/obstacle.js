function enter(pi) {
    if (pi.isQuestActive(2314) == true && pi.isQuestFinished(2314) == false) {
        pi.forceCustomDataQuest(2314, "1");
        pi.getPlayer().getClient().sendPacket(Packages.tools.packet.CField.NPCPacket.getNPCTalk(1300003, 0, "\r\n이것은... 일종의 #b버섯포자#k가 마법의 힘으로 변이되어 강력한 방어막을 형성 하고 있는 것 같다. 물리적 힘이나 공격으로는 뚫을 수 없을 것 같다. #b내무대신#k에게 돌아가 보고하자.", "00 01", 2, 0));
        return;
    }
    if (pi.isQuestActive(2324) == true) {
        pi.warp(106020500, "left00");
        return;
    }
    pi.warp(106020400, "left00");
}