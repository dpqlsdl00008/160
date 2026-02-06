package server.maps;

import client.MapleClient;
import tools.packet.CField;

import tools.packet.CCashShop;

public class MapleMapEffect {

    private String msg = "";
    private int itemId = 0;
    private boolean active = true;
    private boolean jukebox = false;

    public MapleMapEffect(String msg, int itemId) {
        this.msg = msg;
        this.itemId = itemId;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setJukebox(boolean actie) {
        this.jukebox = actie;
    }

    public boolean isJukebox() {
        return this.jukebox;
    }

    public byte[] makeDestroyData() {
        return jukebox ? CCashShop.playCashSong(0, "") : CField.floatNotice(null, 0, false);
    }

    public byte[] makeStartData() {
        return jukebox ? CCashShop.playCashSong(itemId, msg) : CField.floatNotice(msg, itemId, active);
    }

    public void sendStartData(MapleClient c) {
        c.getSession().write(makeStartData());
    }
}
