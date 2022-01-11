package it.osys.powerdc.provider;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.QueryApi;
import com.influxdb.client.WriteApi;

@ApplicationScoped
public class InfluxDbClient {

	private static final Logger LOGGER = LoggerFactory.getLogger("InfluxDbClient");

	public final static String MEASUREMENT_SENSOR = "sensor";

	@ConfigProperty(name = "influxdb.url")
	String influxUrl;

	@ConfigProperty(name = "influxdb.token")
	String authToken;

	@ConfigProperty(name = "influxdb.orgid")
	String org;

	@ConfigProperty(name = "influxdb.bucket")
	String bucket;

	private InfluxDBClient influxDBClient;
	private WriteApi writeApi;

	@PostConstruct
	public void init() {
		influxDBClient = InfluxDBClientFactory.create(influxUrl, authToken.toCharArray(), org, bucket);
		writeApi = influxDBClient.makeWriteApi();
		LOGGER.info("Connection to  {} / {} / {} done.", influxUrl, org, bucket);
	}

	@Produces
	public InfluxDBClient getInfluxDBClient() {
		return influxDBClient;
	}

	@Produces
	public WriteApi getInfluxDBWriteApi() {
		return writeApi;
	}

	@Produces
	public QueryApi getInfluxDBQueryApi() {
		return influxDBClient.getQueryApi();
	}

}
