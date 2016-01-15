package org.cnrs.jdev;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Content {

	String uri;

	String id;
	Date created;
	Boolean cached;
	List<String> qualities = new ArrayList<String>();

	public List<String> getQualities() {
		return qualities;
	}

	public void setQualities(List<String> qualities) {
		this.qualities = qualities;
	}

	public Boolean getCached() {
		return cached;
	}

	public void setCached(Boolean cached) {
		this.cached = cached;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

}
