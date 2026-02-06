package tools.data;

import constants.GameConstants;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import tools.HexTool;

public class OutPacket {

    private final ByteArrayOutputStream baos;
    private static final Charset MS949 = Charset.forName("MS949");

    public OutPacket() {
        this(32);
    }

    public OutPacket(final int size) {
        this.baos = new ByteArrayOutputStream(size);
    }

    public final byte[] getPacket() {
        if (GameConstants.showPacket == true) {
            System.out.println("S : " + HexTool.toString(baos.toByteArray()));
            System.out.println("[Code] " + Thread.currentThread().getStackTrace()[2]);
        }
        return baos.toByteArray();
    }

    public final String toString() {
        return HexTool.toString(baos.toByteArray());
    }

    public final void writeZeroBytes(final int i) {
        for (int x = 0; x < i; x++) {
            baos.write((byte) 0);
        }
    }

    public final void write(final byte[] b) {
        for (int x = 0; x < b.length; x++) {
            baos.write(b[x]);
        }
    }

    public final void write(final boolean b) {
        baos.write(b ? 1 : 0);
    }

    public final void write(final byte b) {
        baos.write(b);
    }

    public final void write(final int b) {
        baos.write((byte) b);
    }

    public final void EncodeByte(final byte[] b) {
        for (int x = 0; x < b.length; x++) {
            baos.write(b[x]);
        }
    }

    public final void EncodeByte(final boolean b) {
        baos.write(b ? 1 : 0);
    }

    public final void EncodeByte(final byte b) {
        baos.write(b);
    }

    public final void EncodeByte(final int b) {
        baos.write((byte) b);
    }

    public final void writeShort(final int i) {
        baos.write((byte) (i & 0xFF));
        baos.write((byte) ((i >>> 8) & 0xFF));
    }

    public final void EncodeShort(final int i) {
        baos.write((byte) (i & 0xFF));
        baos.write((byte) ((i >>> 8) & 0xFF));
    }

    public final void writeInt(final int i) {
        baos.write((byte) (i & 0xFF));
        baos.write((byte) ((i >>> 8) & 0xFF));
        baos.write((byte) ((i >>> 16) & 0xFF));
        baos.write((byte) ((i >>> 24) & 0xFF));
    }

    public final void EncodeInt(final int i) {
        baos.write((byte) (i & 0xFF));
        baos.write((byte) ((i >>> 8) & 0xFF));
        baos.write((byte) ((i >>> 16) & 0xFF));
        baos.write((byte) ((i >>> 24) & 0xFF));
    }

    public void writeAsciiString(String s) {
        write(s.getBytes(MS949));
    }

    public void writeAsciiString(String s, int max) {
        write(s.getBytes(MS949));
        for (int i = s.getBytes(MS949).length; i < max; ++i) {
            write(0);
        }
    }

    public void writeMapleAsciiString(String s) {
        writeShort(s.getBytes(MS949).length);
        writeAsciiString(s);
    }

    public void EncodeString(String s) {
        writeShort(s.getBytes(MS949).length);
        writeAsciiString(s);
    }

    public final void writePos(final Point s) {
        writeShort(s.x);
        writeShort(s.y);
    }

    public void writeIntPos(Point s) {
        writeInt(s.x);
        writeInt(s.y);
    }

    public final void writeRect(final Rectangle s) {
        writeInt(s.x);
        writeInt(s.y);
        writeInt(s.x + s.width);
        writeInt(s.y + s.height);
    }

    public final void writeLong(final long l) {
        baos.write((byte) (l & 0xFF));
        baos.write((byte) ((l >>> 8) & 0xFF));
        baos.write((byte) ((l >>> 16) & 0xFF));
        baos.write((byte) ((l >>> 24) & 0xFF));
        baos.write((byte) ((l >>> 32) & 0xFF));
        baos.write((byte) ((l >>> 40) & 0xFF));
        baos.write((byte) ((l >>> 48) & 0xFF));
        baos.write((byte) ((l >>> 56) & 0xFF));
    }

    public final void EncodeLong(final long l) {
        baos.write((byte) (l & 0xFF));
        baos.write((byte) ((l >>> 8) & 0xFF));
        baos.write((byte) ((l >>> 16) & 0xFF));
        baos.write((byte) ((l >>> 24) & 0xFF));
        baos.write((byte) ((l >>> 32) & 0xFF));
        baos.write((byte) ((l >>> 40) & 0xFF));
        baos.write((byte) ((l >>> 48) & 0xFF));
        baos.write((byte) ((l >>> 56) & 0xFF));
    }

    public final void writeReversedLong(final long l) {
        baos.write((byte) ((l >>> 32) & 0xFF));
        baos.write((byte) ((l >>> 40) & 0xFF));
        baos.write((byte) ((l >>> 48) & 0xFF));
        baos.write((byte) ((l >>> 56) & 0xFF));
        baos.write((byte) (l & 0xFF));
        baos.write((byte) ((l >>> 8) & 0xFF));
        baos.write((byte) ((l >>> 16) & 0xFF));
        baos.write((byte) ((l >>> 24) & 0xFF));
    }

    public void writeBoolean(final boolean b) {
        write(b ? 1 : 0);
    }

    public void Fill(int nValue, int nLenth) {
        for (int i = 0; i < nLenth; i++) {
            EncodeByte(nValue);
        }
    }
}
