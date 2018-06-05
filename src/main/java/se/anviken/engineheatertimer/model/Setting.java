package se.anviken.engineheatertimer.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The persistent class for the settings database table.
 * 
 */
@Entity
@Table(name = "settings")
@NamedQuery(name = "Setting.findAll", query = "SELECT s FROM Setting s")
@XmlRootElement
public class Setting implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "setting_id")
	private int settingId;

	private String description;

	private String parmeter;

	private String value;

	public Setting() {
	}

	public int getSettingId() {
		return this.settingId;
	}

	public void setSettingId(int settingId) {
		this.settingId = settingId;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getParmeter() {
		return this.parmeter;
	}

	public void setParmeter(String parmeter) {
		this.parmeter = parmeter;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}