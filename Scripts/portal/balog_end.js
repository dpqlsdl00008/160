function enter(pi) {
    pi.gainExp(999999);
    Packages.handling.channel.handler.DueyHandler.addNewItemToDb(4310031, 1, pi.getPlayer().getId(), "발록 원정대", "", true);
    pi.getClient().sendPacket(Packages.tools.packet.CField.receiveParcel("발록 원정대", true));
    pi.warp(105100100, 0);
}