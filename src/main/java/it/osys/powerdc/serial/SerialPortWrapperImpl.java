package it.osys.powerdc.serial;

import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.serotonin.modbus4j.serial.SerialPortWrapper;

import jssc.SerialPort;
import jssc.SerialPortException;

public class SerialPortWrapperImpl implements SerialPortWrapper {

	private static final Logger LOG = LoggerFactory.getLogger(SerialPortWrapperImpl.class);

	private String commPortId;
	private int baudRate;
	private int dataBits;
	private int stopBits;
	private int parity;

	private SerialPort port;

	public SerialPortWrapperImpl(String commPortId, int baudRate, int dataBits, int stopBits, int parity) {

		this.commPortId = commPortId;
		this.baudRate = baudRate;
		this.dataBits = dataBits;
		this.stopBits = stopBits;
		this.parity = parity;

		port = new SerialPort(this.commPortId);

	}

	@Override
	public void close() throws Exception {
		port.closePort();
		LOG.debug("Serial port {} closed", port.getPortName());
	}

	@Override
	public void open() {
		try {
			port.openPort();
			port.setParams(this.getBaudRate(), this.getDataBits(), this.getStopBits(), this.getParity());
			LOG.debug("Serial port {} opened", port.getPortName());
		} catch (SerialPortException ex) {
			LOG.error("Error opening port : {} for {} ", port.getPortName(), ex);
		}
	}

	@Override
	public InputStream getInputStream() {
		return new SerialInputStream(port);
	}

	@Override
	public OutputStream getOutputStream() {
		return new SerialOutputStream(port);
	}

	@Override
	public int getBaudRate() {
		return baudRate;
	}

	@Override
	public int getDataBits() {
		return dataBits;
	}

	@Override
	public int getStopBits() {
		return stopBits;
	}

	@Override
	public int getParity() {
		return parity;
	}

}
