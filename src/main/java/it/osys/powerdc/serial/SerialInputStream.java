package it.osys.powerdc.serial;

import jssc.SerialPort;

import java.io.IOException;
import java.io.InputStream;

public class SerialInputStream extends InputStream {

	private SerialPort serialPort;
	private int defaultTimeout = 500;

	public SerialInputStream(SerialPort sp) {
		serialPort = sp;
	}

	@Override
	public int read() throws IOException {

		byte[] buf = new byte[1];

		try {

			buf = serialPort.readBytes(1, defaultTimeout);
			return buf[0];

		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	@Override
	public int read(byte[] buf) throws IOException {
		return read(buf, 0, buf.length);
	}

	@Override
	public int read(byte[] buf, int offset, int length) throws IOException {

		if (buf.length < offset + length)
			length = buf.length - offset;

		int available = this.available();
		if (available > length)
			available = length;

		try {

			byte[] readBuf = serialPort.readBytes(available);
			System.arraycopy(readBuf, 0, buf, offset, readBuf.length);
			return readBuf.length;

		} catch (Exception e) {
			throw new IOException(e);
		}

	}

	@Override
	public int available() throws IOException {

		try {

			int ret = serialPort.getInputBufferBytesCount();
			if (ret >= 0)
				return ret;

			throw new IOException("Error checking available bytes from the serial port.");

		} catch (Exception e) {
			throw new IOException("Error checking available bytes from the serial port.", e);
		}

	}

}
