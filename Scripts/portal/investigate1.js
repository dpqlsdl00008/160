function enter(pi) {
    if (pi.isQuestActive(2314) == true && pi.isQuestFinished(2314) == false) {
        pi.delayEffect("Effect/OnUserEff.img/normalEffect/mushroomcastle/chatBalloon1", 1);
    }
    if (pi.isQuestActive(2322) == true && pi.isQuestFinished(2322) == false) {
        pi.getPlayer().getClient().sendPacket(Packages.tools.packet.CField.NPCPacket.getNPCTalk(1300003, 0, "\r\n칼라 버섯 포자를 사용해 결계를 뚫을 수 있을 것 같다.", "00 01", 2, 0));
    }
}