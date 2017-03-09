package se.anviken.engineheatertimer.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import se.anviken.engineheatertimer.model.Setting;

/**
 * 
 */
@Stateless
@Path("/trigger")
public class TriggerEndpoint {
	private static final String TEMP_START = "tempStart";
	@PersistenceContext(unitName = "EngineHeaterTimer-persistence-unit")
	private EntityManager em;

	@GET
	@Produces("application/json")
	public List<Setting> listAll(@QueryParam("start") Integer startPosition, @QueryParam("max") Integer maxResult) {
		Map<String, Integer> settings = loadSettings();
		double outsideTemperature = getOutsideTemperature();
		if (settings.get(TEMP_START) > outsideTemperature) {
			return null;
		}

		return null;
	}

	private double getOutsideTemperature() {

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
