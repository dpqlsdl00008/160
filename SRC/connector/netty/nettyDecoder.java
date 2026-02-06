package connector.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import connector.connectorClient;
import tools.data.ByteArrayByteStream;
import tools.data.LittleEndianAccessor;

import java.util.List;

public class nettyDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> list) throws Exception {
        final connectorClient client = ctx.channel().attr(connectorClient.CLIENTKEY).get();

        if (client == null) {
            return;
        }

        if (buffer.readableBytes() < 4) {
            return;
        }
        final int packetHeader = buffer.readShort();
        buffer.markReaderIndex();
        byte[] decoded = new byte[buffer.readableBytes()];
        buffer.readBytes(decoded);
        buffer.markReaderIndex();
        list.add(new LittleEndianAccessor(new ByteArrayByteStream(decoded)));
    }
}
