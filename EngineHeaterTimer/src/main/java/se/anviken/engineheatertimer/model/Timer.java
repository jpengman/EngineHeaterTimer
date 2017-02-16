package se.anviken.engineheatertimer.model;

import java.io.Serializable;
import java.sql.Time;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The persistent class for the timers database table.
 * 
 */
@Entity
@Table(name = "timers")
@NamedQuery(name = "Timer.findAll", query = "SELECT t FROM Timer t")
@XmlRootElement
public class Timer implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "timer_id")
	private int timerId;

	@Column(nullable = false, columnDefinition = "TINYINT", length = 1)
	private boolean active;

	@Column(nullable = false, columnDefinition = "TINYINT", length = 1)
	private boolean friday;

	@Column(nullable = false, columnDefinition = "TINYINT", length = 1)
	private boolean monday;

	@Column(nullable = false, columnDefinition = "TINYINT", length = 1)
	private boolean saturday;

	@Column(nullable = false, columnDefinition = "TINYINT", length = 1)
	private boolean sunday;

	@Column(nullable = false, columnDefinition = "TINYINT", length = 1)
	private boolean thursday;

	private Time time;

	@Column(nullable = false, columnDefinition = "TINYINT", length = 1)
	private boolean tuesday;

	@Column(nullable = false, columnDefinition = "TINYINT", length = 1)
	private boolean wednesday;

	public Timer() {
	}

	public int getTimerId() {
		return this.timerId;
	}

	public void setTimerId(int timerId) {
		this.timerId = timerId;
	}

	public boolean getActive() {
		return this.active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean getFriday() {
		return this.friday;
	}

	public void setFriday(boolean friday) {
		this.friday = friday;
	}

	public boolean getMonday() {
		return this.monday;
	}

	public void setMonday(boolean monday) {
		this.monday = monday;
	}

	public boolean getSaturday() {
		return this.saturday;
	}

	public void setSaturday(boolean saturday) {
		this.saturday = saturday;
	}

	public boolean getSunday() {
		return this.sunday;
	}

	public void setSunday(boolean sunday) {
		this.sunday = sunday;
	}

	public boolean getThursday() {
		return this.thursday;
	}

	public void setThursday(boolean thursday) {
		this.thursday = thursday;
	}

	public Time getTime() {
		return this.time;
	}

	public void setTime(Time time) {
		this.time = time;
	}

	public boolean getTuesday() {
		return this.tuesday;
	}

	public void setTuesday(boolean tuesday) {
		this.tuesday = tuesday;
	}

	public boolean getWednesday() {
		return this.wednesday;
	}

	public void setWednesday(boolean wednesday) {
		this.wednesday = wednesday;
	}

}