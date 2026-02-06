package connector.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import connector.connectorClient;

public class nettyEncoder extends MessageToByteEncoder<byte[]> {
    @Override
    protected void encode(ChannelHandlerContext ctx, byte[] pData, ByteBuf buffer) throws Exception {
        final connectorClient client = ctx.channel().attr(connectorClient.CLIENTKEY).get();

        /*if (client != null) {
            final Lock mutex = client.getLock();

            mutex.lock();
            try {
                int i = pData.length;
                byte[] a = {(byte) (i & 0xFF), (byte) ((i >>> 8) & 0xFF), (byte) ((i >>> 16) & 0xFF), (byte) ((i >>> 24) & 0xFF)};
                buffer.writeBytes(a);
                System.out.println(bytesToHex(a));
                buffer.writeBytes(pData);
                System.out.println(bytesToHex(pData));
            } finally {
                mutex.unlock();
            }
        } else {
            buffer.writeByte((byte) 0xFF);
            buffer.writeBytes(pData);
        }*/
        buffer.writeBytes(pData);
        ctx.flush();
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }
}
