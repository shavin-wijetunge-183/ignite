package com.cache.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ignite.cache.query.annotations.QuerySqlField;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Organization {

    /** Organization ID (indexed). */
    @QuerySqlField(index = true)
    private Long id;

    /** Organization name (indexed). */
    @QuerySqlField(index = true)
    private String name;

    /** Type. */
    private OrganizationType type;

    /** Last update time. */
    private Timestamp lastUpdated;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public OrganizationType getType() {
		return type;
	}

	public void setType(OrganizationType type) {
		this.type = type;
	}

	public Timestamp getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Timestamp lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public Organization(Long id, String name, OrganizationType type, Timestamp lastUpdated) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
		this.lastUpdated = lastUpdated;
	}

	public Organization() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
    
    
}
