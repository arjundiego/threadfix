package com.denimgroup.threadfix.service.scans;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.denimgroup.threadfix.data.entities.ChannelType;

class SimpleVuln {
	private String path, parameter, genericVuln, genericVulnId, notes;
	private Set<String> appscanNativeIds, fortifyNativeIds;
	private Integer lineNumber = null;
	
	public static SimpleVuln buildSimpleVuln(String[] args, int lineNumber) {
		if (args.length != 6) {
			throw new IllegalArgumentException();
		}
		
		return new SimpleVuln(args[0], args[1], args[2], args[3], args[4], args[5], lineNumber);
	}
	
	public SimpleVuln(String path, String parameter, String genericVulnId,
			String appscanNativeId, String fortifyNativeId, String notes, int lineNumber) {
		this.path = path;
		this.parameter = parameter;
		this.genericVulnId = genericVulnId;
		this.notes = notes;
		this.lineNumber = lineNumber;
		this.fortifyNativeIds = setContaining(fortifyNativeId);
		this.appscanNativeIds = setContaining(appscanNativeId);
		
		if (path == null) {
			this.path = "";
		}
		if (parameter == null) {
			this.parameter = "";
		}
		if (genericVulnId == null) {
			this.genericVuln = "";
		}
	}
	
	public SimpleVuln(JSONObject vulnObject) throws JSONException {
		JSONObject surfaceLocation = vulnObject.getJSONObject("surfaceLocation");
		
		appscanNativeIds = new HashSet<>();
		fortifyNativeIds = new HashSet<>();
		
		if (surfaceLocation != null) {
			if (surfaceLocation.has("path")) {
				path = surfaceLocation.getString("path");
			}
			
			if (surfaceLocation.has("parameter")) {
				parameter = surfaceLocation.getString("parameter");
			}
		}
		
		JSONArray findings = vulnObject.getJSONArray("findings");
		
		JSONObject genericVulnObject = vulnObject.getJSONObject("genericVulnerability");
		
		genericVuln = genericVulnObject.getString("name");
		genericVulnId = genericVulnObject.getString("id");
		
		for (int j = 0; j < findings.length(); j ++) {
			JSONObject finding = findings.getJSONObject(j);
			String channelName = finding.getJSONObject("channelVulnerability")
				.getJSONObject("channelType")
				.getString("name");
			switch (channelName) {
				case ChannelType.APPSCAN_DYNAMIC: appscanNativeIds.add(finding.getString("nativeId")); break;
				case ChannelType.FORTIFY: fortifyNativeIds.add(finding.getString("nativeId")); break;
			}
		}
	}
	
	private Set<String> setContaining(String... strings) {
		return new HashSet<>(Arrays.asList(strings));
	}
	
	public Set<String> getAppscanNativeIds() {
		return appscanNativeIds;
	}
	
	public Set<String> getFortifyNativeIds() {
		return fortifyNativeIds;
	}
	
	public String getPath() {
		return path;
	}

	public String getParameter() {
		return parameter;
	}

	public String getGenericVuln() {
		return genericVuln;
	}
	
	public String getGenericVulnId() {
		return genericVulnId;
	}
	
	public String getNotes() {
		return notes;
	}
	
	public Integer getLineNumber() {
		return lineNumber;
	}
	
	public String toString() {
		return "{ "+ path + ", "  + genericVuln + ", " + parameter + ", " + appscanNativeIds + " }";
	}
	
	public boolean equals(Object other) {
		if (other == null || !(other instanceof SimpleVuln)) {
			return false;
		}
		
		SimpleVuln otherVuln = (SimpleVuln) other;
		
		return this.hashCode() == otherVuln.hashCode();
	}
	
	public int hashCode() {
		return (path + "-" + parameter + "-" + genericVuln).hashCode();
	}
}