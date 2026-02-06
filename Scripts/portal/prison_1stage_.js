function enter(pi) {
    pi.getPlayer().getMap().startSimpleMapEffect("서둘러 다음 맵으로 이동해주세요.", 5120053);
    pi.getPlayer().getMap().broadcastMessage(Packages.tools.packet.CField.showEffect("quest/party/clear"));
    pi.getPlayer().getMap().broadcastMessage(Packages.tools.packet.CField.playSound("Party1/Clear"));
}