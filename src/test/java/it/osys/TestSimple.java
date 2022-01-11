package it.osys;

import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.code.DataType;
import com.serotonin.modbus4j.code.RegisterRange;
import com.serotonin.modbus4j.locator.NumericLocator;
import com.serotonin.modbus4j.locator.StringLocator;
import com.serotonin.modbus4j.serial.SerialPortWrapper;

import it.osys.powerdc.serial.SerialPortWrapperImpl;
import jssc.SerialPort;

public class TestSimple {

	public static final int SERIAL_BAUDRATE = 9600;
	public static final String SERIAL_PORT = "/dev/ttyUSB0";

	public static void main(String[] args) {

		ModbusFactory factory = new ModbusFactory();

		SerialPortWrapper params = new SerialPortWrapperImpl(SERIAL_PORT, SERIAL_BAUDRATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
				SerialPort.PARITY_NONE);

		ModbusMaster master = factory.createRtuMaster(params);
		master.setTimeout(1000);
		master.setRetries(0);
		master.setMultipleWritesOnly(false);

		try {

			master.init();

		} catch (Exception e) {
			System.out.println("Modbus Master Init Error: " + e.getMessage());
			return;
		}

		try {

			// for (int i = 0; i < 3; i++) {

			System.out.println("Output enable: " + master.getValue(PowerDC.outputEnableRW));
			System.out.println("Protection Status: " + master.getValue(PowerDC.protectStatusR));
			System.out.println("Current Voltage Setpoint: " + master.getValue(PowerDC.currVoltageSetpointRW));
			System.out.println("Current Current Setpoint: " + master.getValue(PowerDC.currCurrentSetpointRW));

			System.out.println("Current Voltage: " + master.getValue(PowerDC.currVoltageR));
			System.out.println("Current Current: " + master.getValue(PowerDC.currCurrentR));
			System.out.println("Current Power: " + master.getValue(PowerDC.currPowerR));

			// master.setValue(PowerDC.currVoltageSetpointRW, 400);
			// master.setValue(PowerDC.currCurrentSetpointRW, 200);
			master.setValue(PowerDC.outputEnableRW, false);

			// Thread.sleep(1000);

			// master.setValue(bl, 1);
			// NumericLocator nl = new NumericLocator(2,
			// RegisterRange.HOLDING_REGISTER, 13,
			// DataType.TWO_BYTE_INT_UNSIGNED);
			// master.setValue(nl, "1");

			// System.out.println("Reg. 1001 Value:" + master.getValue(bl));

			// }

			// System.out.println("Leggo :" +
			// master.getValue(BaseLocator.holdingRegister(1, 18,
			// DataType.FOUR_BYTE_INT_UNSIGNED)));

			for (int i = 1; i < 88; i++) {
				System.out.println("Leggo registro " + i + ": "
						+ master.getValue(new NumericLocator(1, RegisterRange.HOLDING_REGISTER, i, DataType.TWO_BYTE_INT_UNSIGNED)));
			}

		} catch (Exception e) {
			// TODO: handle exception

			e.printStackTrace();

		} finally {
			master.destroy();
		}

	}

}
