package server.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import server.util.HttpRequest;
import server.util.WriteHttpResponse;


public class ServerHandler extends SimpleChannelInboundHandler<HttpRequest> {

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, HttpRequest request) throws Exception {
		WriteHttpResponse.write(ctx, request, request.getHttpRequestHash());
	}
}