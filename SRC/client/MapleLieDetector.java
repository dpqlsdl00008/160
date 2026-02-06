package client;

import scripting.LieDetectorScript;
import server.Timer.EtcTimer;
import server.maps.MapleMap;
import tools.HexTool;
import tools.Pair;
import tools.packet.CWvsContext;

public class MapleLieDetector {

    public MapleCharacter chr;
    public byte type;
    public int attempt;
    public String tester, answer;
    public long startLie;
    public boolean inProgress, passed;
    public boolean sent = false;

    public MapleLieDetector(final MapleCharacter c) {
        this.chr = c;
        reset();
    }

    public final boolean startLieDetector(final String tester, final boolean isItem, final boolean anotherAttempt) {
        if (!anotherAttempt && (chr.isClone() || (isPassed() && isItem) || inProgress() || attempt == 2)) {
            return false;
        }
        final Pair<String, String> captcha = LieDetectorScript.getImageBytes();
        if (captcha == null) {
            return false;
        }
        final byte[] image = HexTool.getByteArrayFromHexString(captcha.getLeft());
        this.answer = captcha.getRight();
        this.tester = tester;
        this.inProgress = true;
        this.type = (byte) (isItem ? 0 : 1);
        this.attempt++;
        startLie = System.currentTimeMillis();

        chr.getClient().sendPacket(CWvsContext.LieDetector(null, (byte) 7, (byte) 4, image));
        if (!sent) {
            EtcTimer.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    if (!passed && chr != null) {
                        if (attempt >= 2) {
                            final MapleCharacter search_chr = chr.getMap().getCharacterByName(tester);
                            if (search_chr != null && search_chr.getId() != chr.getId()) {
                                search_chr.getClient().sendPacket(CWvsContext.LieDetector(null, (byte) 12, (byte) 0, null));
                            }
                            chr.getClient().sendPacket(CWvsContext.LieDetector(null, (byte) 8, (byte) 1, null));
                            final MapleMap to = chr.getMap().getReturnMap();
                            chr.changeMap(to, to.getPortal(0));
                            //end();
                            end();
                        } else {
                            startLieDetector(tester, isItem, true);
                        }
                    }
                }
            }, 1000 * 60);
        } else {
            return false;
        }
        return true;
    }

    public final boolean canstartLie(long now) {
        return startLie > 0 && startLie + 1000L * 60L * 10L < now;
    }

    public final int getAttempt() {
        return attempt;
    }

    public final byte getLastType() {
        return type;
    }

    public final String getTester() {
        return tester;
    }

    public final String getAnswer() {
        return answer;
    }

    public final boolean inProgress() {
        return inProgress;
    }

    public final boolean isPassed() {
        return passed;
    }

    public final void end() {
        this.inProgress = false;
        this.passed = true;
        this.attempt = 0;
        this.sent = false;
    }

    public final void reset() {
        this.tester = "";
        this.answer = "";
        this.attempt = 0;
        this.inProgress = false;
        this.passed = false;
    }

    public final boolean getSent() {
        return sent;
    }
}
