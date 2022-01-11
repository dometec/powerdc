package it.osys;

import java.nio.charset.StandardCharsets;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.influxdb.client.WriteApi;
import com.influxdb.client.write.Point;
import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.code.DataType;
import com.serotonin.modbus4j.code.RegisterRange;
import com.serotonin.modbus4j.exception.ErrorResponseException;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.locator.NumericLocator;
import com.serotonin.modbus4j.serial.SerialPortWrapper;

import io.quarkus.scheduler.Scheduled;
import it.osys.powerdc.serial.SerialPortWrapperImpl;
import jssc.SerialPort;

@Singleton
public class PowerDC {

	private static final Logger LOGGER = LoggerFactory.getLogger("Command");

	public static final int SERIAL_BAUDRATE = 9600;
	public static final String SERIAL_PORT = "/dev/ttyUSB0";

	public static final NumericLocator outputEnableRW = new NumericLocator(1, RegisterRange.HOLDING_REGISTER, 1,
			DataType.TWO_BYTE_INT_UNSIGNED);

	public static final NumericLocator protectStatusR = new NumericLocator(1, RegisterRange.HOLDING_REGISTER, 2,
			DataType.TWO_BYTE_INT_UNSIGNED);

	public static final NumericLocator currVoltageR = new NumericLocator(1, RegisterRange.HOLDING_REGISTER, 16,
			DataType.TWO_BYTE_INT_UNSIGNED);
	public static final NumericLocator currCurrentR = new NumericLocator(1, RegisterRange.HOLDING_REGISTER, 17,
			DataType.TWO_BYTE_INT_UNSIGNED);
	public static final NumericLocator currPowerR = new NumericLocator(1, RegisterRange.HOLDING_REGISTER, 18,
			DataType.FOUR_BYTE_INT_UNSIGNED);

	public static final NumericLocator currVoltageSetpointRW = new NumericLocator(1, RegisterRange.HOLDING_REGISTER, 48,
			DataType.TWO_BYTE_INT_UNSIGNED);
	public static final NumericLocator currCurrentSetpointRW = new NumericLocator(1, RegisterRange.HOLDING_REGISTER, 49,
			DataType.TWO_BYTE_INT_UNSIGNED);

	private static final String MEASUREMENT_POWERDC = "measurement";

	@Inject
	protected WriteApi writeApi;

	private ModbusMaster master;

	public PowerDC() {

		LOGGER.info("Init ModbusFactory...");

		ModbusFactory factory = new ModbusFactory();

		SerialPortWrapper params = new SerialPortWrapperImpl(SERIAL_PORT, SERIAL_BAUDRATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
				SerialPort.PARITY_NONE);

		master = factory.createRtuMaster(params);
		master.setTimeout(2000);
		master.setRetries(1);
		master.setMultipleWritesOnly(false);

		try {

			master.init();

			LOGGER.info("ModbusFactory init.");

		} catch (Exception e) {
			LOGGER.error("Modbus Master Init Error: " + e.getMessage());
		}

	}

	@Scheduled(every = "1s")
	public void run() throws ModbusTransportException, ErrorResponseException {

		LOGGER.info("Sampling...");

		Point pointBuilder = Point.measurement(MEASUREMENT_POWERDC);
		pointBuilder.addField("output_enable", master.getValue(PowerDC.outputEnableRW));
		pointBuilder.addField("protection_status", master.getValue(PowerDC.protectStatusR));
		pointBuilder.addField("setpoint_voltage", master.getValue(PowerDC.currVoltageSetpointRW));
		pointBuilder.addField("setpoint_current", master.getValue(PowerDC.currCurrentSetpointRW));
		pointBuilder.addField("current", master.getValue(PowerDC.currCurrentR));
		pointBuilder.addField("voltage", master.getValue(PowerDC.currVoltageR));
		pointBuilder.addField("power", master.getValue(PowerDC.currPowerR));

		writeApi.writePoint(pointBuilder);

	}

	@Incoming("current")
	public void setCurrent(byte[] bSurrent) throws ModbusTransportException, ErrorResponseException {
		int current = Integer.parseInt(new String(bSurrent, StandardCharsets.UTF_8));
		LOGGER.info("Set Current: {} mA.", current);
		master.setValue(currCurrentSetpointRW, current);
	}

	@Incoming("voltage")
	public void setVoltage(byte[] bVoltage) throws ModbusTransportException, ErrorResponseException {
		int voltage = Integer.parseInt(new String(bVoltage, StandardCharsets.UTF_8));
		LOGGER.info("Set Voltage: {} mV.", voltage * 10);
		master.setValue(currVoltageSetpointRW, voltage);
	}

	@Incoming("output")
	public void setOutput(byte[] bOutput) throws ModbusTransportException, ErrorResponseException {
		int output = Integer.parseUnsignedInt(new String(bOutput, StandardCharsets.UTF_8));
		if (output > 1)
			output = 1;
		LOGGER.info("Set Output: {}.", output);
		master.setValue(outputEnableRW, output);
	}

}