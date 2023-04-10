package com.cache.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ignite.cache.query.annotations.QuerySqlField;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Person  implements Serializable {

    private static final long serialVersionUID = 3235823406453902677L;
    
    public static PersonBuilder builder = new PersonBuilder();

    @QuerySqlField(index = true)
    private Long id;
    @QuerySqlField
    private String firstName;
    @QuerySqlField
    private String lastName;
    @QuerySqlField
    private int age;
    /** Organization ID (indexed). */
    @QuerySqlField(index = true)
    public Long orgId;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public Long getOrgId() {
		return orgId;
	}
	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	public static PersonBuilder builder() {
		builder.person=new Person();
		return builder;
	}




	public static class PersonBuilder implements Serializable{
		
		Person person = new Person();
    	
    	public PersonBuilder id(Long id) {
    		person.id=id;
    		return this;
    	}
        
        
        public PersonBuilder firstName(String firstName) {
        	person.firstName=firstName;
        	return this;
    	}
        
        
        public PersonBuilder lastName(String lastName) {
        	person.lastName=lastName;
        	return this;
    	}
        
        
        public PersonBuilder age(int age) {
        	person.age=age;
        	return this;
    	}
        
        
        public PersonBuilder orgId(Long orgId) {
        	person.orgId=orgId;
        	return this;
    	}
        
        public Person build() {
        	return person;
        	
        }
    }

}
