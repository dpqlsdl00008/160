function start() {
    cm.getPlayer().addHP(-25);
    cm.getPlayer().client.getSession().write(Packages.tools.packet.CField.UIPacket.IntroLock(false));
    cm.sendNext("끼긱~~ 끼긱~~ ??");
    cm.dispose();
}