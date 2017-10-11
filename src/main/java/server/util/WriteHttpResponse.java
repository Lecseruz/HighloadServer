package server.util;

import config.IOUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import org.apache.commons.lang3.ArrayUtils;
import server.cache.ServerMemoryCache;

import java.io.FileInputStream;

public class WriteHttpResponse {

	public static void write(ChannelHandlerContext ctx, HttpRequest request, long requestHash) {

		if (isCached(requestHash)) {
			writeCached(ctx, (Byte[]) ServerMemoryCache.getInstance().get(requestHash));
		} else {

			HttpResponse httpResponse = new HttpResponse.HttpResponseBuilder().httpRequest(request).build();

			addToCache(httpResponse, requestHash);

			writeNonCached(ctx, httpResponse);
		}
	}

	private static void writeNonCached(ChannelHandlerContext ctx, HttpResponse httpResponse) {
		ctx.write(Unpooled.copiedBuffer(httpResponse.getHttpHeaders().getHttpHeaders().toString().getBytes()));

		final FileInputStream is = httpResponse.getFileInputStream();
		if (is != null && !httpResponse.getMethodName().equals("HEAD")) {
			ChannelFuture future;
			future = ctx.writeAndFlush(new DefaultFileRegion(is.getChannel(), 0, httpResponse.getFileLenght()));
			future.addListener(ChannelFutureListener.CLOSE);
		} else {
			ctx.flush();
			ctx.close();
		}
	}

	private static void writeCached(ChannelHandlerContext ctx, Byte[] bytes) {
		byte[] response = ArrayUtils.toPrimitive(bytes);
		ctx.writeAndFlush(Unpooled.copiedBuffer(response)).addListener(ChannelFutureListener.CLOSE);
	}

	private static void addToCache(HttpResponse httpResponse, long requestHash) {
		ServerMemoryCache cache = ServerMemoryCache.getInstance();

		if (cache.get(requestHash) == null) {
			int i = 0;
			Byte[] response = null;

			byte[] headers = httpResponse.getHttpHeaders().getHttpHeaders().toString().getBytes();

			if (httpResponse.getFileInputStream() != null && !httpResponse.getMethodName().equals("HEAD")) {
				if (httpResponse.getFile().length() - headers.length <= IOUtil.ONE_MB) {
					byte[] file = IOUtil.converInputStreamToByteArray(httpResponse.getFileInputStream());
					response = new Byte[headers.length + file.length];
					for (byte b : headers)
						response[i++] = b;
					for (byte b : file)
						response[i++] = b;
				} else {
					return;
				}
			} else {
				response = new Byte[headers.length];
				for (byte b : headers)
					response[i++] = b;
			}
			cache.put(requestHash, response);
		}
	}

	private static boolean isCached(long hash) {
		return ServerMemoryCache.getInstance().get(hash) != null ? true : false;
	}

}
