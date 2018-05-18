package se.anviken.engineheatertimer.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import se.anviken.engineheatertimer.model.Setting;
import se.anviken.engineheatertimer.model.Timer;

/**
 * 
 */
@Stateless
@Path("/trigger")
public class TriggerEndpoint {
	private static final String ONE_TIME_TIMER_START = "one.time.timer.start";
	private static final String TEMP_START = "tempStart";
	private static final String EXTRA_TIME = "extraTime";
	private static final String MIN_TIME = "minTime";
	private static final String MAX_TIME = "maxTime";
	private static final String MAX_TIME_AT_TEMP = "maxTimeAtTemp";

	private static final SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@PersistenceContext(unitName = "EngineHeaterTimer-persistence-unit")
	private EntityManager em;

	@GET
	@Produces("application/json")
	public String getState()
			throws ClientProtocolException, IOException, UnsupportedOperationException, ParseException {
		Map<String, Integer> settings = loadSettings();
		long runningTime = getRunningTime(settings);
		if (runningTime == 0) {
			return "off";
		}
		// Check if onetimetimer is active
		if (isOneTimeTimerActive(runningTime, settings.get(EXTRA_TIME))) {
			return "on";
		}

		// check if scheduled timer is triggered
		LocalTime from = LocalTime.now().minusMinutes(settings.get(EXTRA_TIME));
		LocalTime to = LocalTime.now().plusMinutes(runningTime);
		TypedQuery<Timer> query = em.createQuery("SELECT DISTINCT t FROM Timer t", Timer.class);
		String state = "off";
		for (Timer timer : query.getResultList()) {
			LocalTime time = timer.getTime().toLocalTime();
			if (timer.getActive() && time.isAfter(from) && time.isBefore(to)) {
				state = "on";
			}
		}
		return state;
	}

	@GET
	@Path("/isonetimetimeractive")
	@Produces("application/json")
	public boolean isOneTimeTimerActive()
			throws FileNotFoundException, IOException, UnsupportedOperationException, ParseException {
		Map<String, Integer> settings = loadSettings();
		long runningTime = getRunningTime(settings);
		return isOneTimeTimerActive(runningTime, settings.get(EXTRA_TIME));

	}

	private boolean isOneTimeTimerActive(long runningTime, Integer extraTime)
			throws FileNotFoundException, IOException {

		Timestamp oneTimeTimerStart = null;
		Properties statusProps = getStatusProps();
		String oneTimeTimerString = statusProps.getProperty(ONE_TIME_TIMER_START);
		if (oneTimeTimerString != null) {
			oneTimeTimerStart = Timestamp.valueOf(oneTimeTimerString);
		}
		if (oneTimeTimerStart != null) {
			Timestamp OneTimeTimerStop = new Timestamp(
					oneTimeTimerStart.getTime() + TimeUnit.MINUTES.toMillis(runningTime + extraTime));
			Timestamp now = new Timestamp(System.currentTimeMillis());
			if (OneTimeTimerStop.compareTo(now) > 0) {
				return true;
			}
		}
		return false;
	}

	@GET
	@Path("/getrunningtime")
	@Produces("application/json")
	public long getRunningTime()
			throws ClientProtocolException, IOException, UnsupportedOperationException, ParseException {
		Map<String, Integer> settings = loadSettings();
		return getRunningTime(settings);
	}

	@GET
	@Path("/setonetimetimer")
	@Produces("application/json")
	public boolean setOneTimeTimer()
			throws ClientProtocolException, IOException, UnsupportedOperationException, ParseException {
		Properties statusProps = getStatusProps();
		statusProps.setProperty(ONE_TIME_TIMER_START,
				timestampFormat.format(new Timestamp(System.currentTimeMillis())));
		saveStatusProps(statusProps);
		return true;
	}

	@GET
	@Path("/toggleonetimetimer")
	@Produces("application/json")
	public boolean toggleOneTimeTimer()
			throws ClientProtocolException, IOException, UnsupportedOperationException, ParseException {
		if (isOneTimeTimerActive()) {
			cancelOneTimeTimer();
		} else {
			setOneTimeTimer();
		}
		return true;
	}

	@GET
	@Path("/cancelonetimetimer")
	@Produces("application/json")
	public boolean cancelOneTimeTimer()
			throws ClientProtocolException, IOException, UnsupportedOperationException, ParseException {
		Properties statusProps = getStatusProps();
		statusProps.remove(ONE_TIME_TIMER_START);
		saveStatusProps(statusProps);
		return true;
	}

	private void saveStatusProps(Properties statusProps) {
		OutputStream output = null;
		try {
			output = new FileOutputStream("statusFile.properties");
			statusProps.store(output, null);

		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private Properties getStatusProps() throws IOException, FileNotFoundException {
		File statusFile = new File("statusFile.properties");
		statusFile.createNewFile();
		FileInputStream fis = new FileInputStream(statusFile);
		Properties statusProps = new Properties();
		statusProps.load(fis);
		fis.close();
		return statusProps;
	}

	@GET
	@Path("/getoutsidetemp")
	@Produces("application/json")
	public Double gettemp() throws ClientProtocolException, IOException, UnsupportedOperationException, ParseException {
		return getOutsideTemperature();
	}

	private long getRunningTime(Map<String, Integer> settings)
			throws ClientProtocolException, IOException, UnsupportedOperationException, ParseException {
		long runningTime = 0;
		double outsideTemperature = getOutsideTemperature();
		if (settings.get(TEMP_START) < outsideTemperature) {
			return runningTime;
		}
		if (settings.get(MAX_TIME_AT_TEMP) > outsideTemperature) {
			runningTime = settings.get(MAX_TIME);
		} else {
			double timeSpan = settings.get(MAX_TIME) - settings.get(MIN_TIME);
			double tempSpan = settings.get(TEMP_START) - settings.get(MAX_TIME_AT_TEMP);
			double temp = settings.get(TEMP_START) - outsideTemperature;
			double percent = temp / tempSpan;
			runningTime = Math.round(settings.get(MIN_TIME) + percent * timeSpan);
		}
		return runningTime;
	}

	private Double getOutsideTemperature()
			throws ClientProtocolException, IOException, UnsupportedOperationException, ParseException {
		CredentialsProvider provider = new BasicCredentialsProvider();
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("web", "Hemlig123!");
		provider.setCredentials(AuthScope.ANY, credentials);

		CloseableHttpClient httpclient = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();
		;
		HttpGet httpget = new HttpGet("http://localhost:8080/OWManager-0.0.1-SNAPSHOT/rest/data/temperature/14/");
		CloseableHttpResponse response = httpclient.execute(httpget);
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject = (JSONObject) jsonParser
				.parse(new InputStreamReader(response.getEntity().getContent(), Charset.defaultCharset()));
		response.close();

		return (Double) jsonObject.get("temperature");
	}

	private Map<String, Integer> loadSettings() {
		TypedQuery<Setting> findAllQuery = em.createQuery("SELECT DISTINCT s FROM Setting s ORDER BY s.settingId",
				Setting.class);

		Map<String, Integer> settings = new HashMap<String, Integer>();
		for (Setting setting : findAllQuery.getResultList()) {
			settings.put(setting.getParmeter(), Integer.parseInt(setting.getValue()));
		}
		return settings;
	}
}
