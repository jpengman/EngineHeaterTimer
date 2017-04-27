package se.anviken.engineheatertimer.rest;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import se.anviken.engineheatertimer.model.Setting;
import se.anviken.engineheatertimer.model.Timer;

/**
 * 
 */
@Stateless
@Path("/trigger")
public class TriggerEndpoint {
	private static final String TEMP_START = "tempStart";
	private static final String EXTRA_TIME = "extraTime";
	private static final String MIN_TIME = "minTime";
	private static final String MAX_TIME = "maxTime";
	private static final String MAX_TIME_AT_TEMP = "maxTImeAtTemp";

	@PersistenceContext(unitName = "EngineHeaterTimer-persistence-unit")
	private EntityManager em;

	@GET
	@Produces("application/json")
	public boolean getState() {
		Map<String, Integer> settings = loadSettings();
		long runningTime = getRunningTime(settings);
		if (runningTime == 0) {
			return false;
		}
		LocalTime from = LocalTime.now().minusMinutes(settings.get(EXTRA_TIME));
		LocalTime to = LocalTime.now().plusMinutes(runningTime);
		TypedQuery<Timer> query = em.createQuery("SELECT DISTINCT t FROM Timer t", Timer.class);
		boolean state = false;
		for (Timer timer : query.getResultList()) {
			LocalTime time = timer.getTime().toLocalTime();
			if (timer.getActive() && time.isAfter(from) && time.isBefore(to)) {
				state = true;
			}
		}
		return state;
	}

	@GET
	@Path("/getrunningtime")
	@Produces("application/json")
	public long getRunningTime() {
		Map<String, Integer> settings = loadSettings();
		return getRunningTime(settings);
	}

	private long getRunningTime(Map<String, Integer> settings) {
		long runningTime = 0;
		int outsideTemperature = getOutsideTemperature();
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

	private int getOutsideTemperature() {

		return -5;
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
