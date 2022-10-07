package com.unascribed.yttr.client;

import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;

import com.google.common.primitives.Shorts;

import net.minecraft.client.sound.AudioStream;

public class MonoAudioStream implements AudioStream {

	private final AudioStream delegate;
	
	public MonoAudioStream(AudioStream delegate) {
		this.delegate = delegate;
	}
	
	@Override
	public void close() throws IOException {
		delegate.close();
	}

	@Override
	public AudioFormat getFormat() {
		var af = delegate.getFormat();
		return new AudioFormat(af.getEncoding(), af.getSampleRate(), af.getSampleSizeInBits(), 1, af.getFrameSize(), af.getFrameRate(), af.isBigEndian(), af.properties());
	}

	@Override
	public ByteBuffer getBuffer(int size) throws IOException {
		var buf = delegate.getBuffer(size);
		var sbuf = buf.asShortBuffer();
		int len = buf.remaining();
		for (int i = 0; i < len/4; i++) {
			short l = sbuf.get(i*2);
			short r = sbuf.get((i*2)+1);
			short m = Shorts.saturatedCast((l+r)/2);
			sbuf.put(i, m);
		}
		buf.limit(len/2);
		return buf;
	}

}
