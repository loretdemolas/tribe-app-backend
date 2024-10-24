package com.savvato.tribeapp.entities;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Schema(description= "A user's role")
@Entity
public class UserRole {

	public static final UserRole ROLE_ADMIN = new UserRole(1L, "ROLE_admin");
	public static final UserRole ROLE_ACCOUNTHOLDER = new UserRole(2L, "ROLE_accountholder");
	public static final UserRole ROLE_PHRASEREVIEWER = new UserRole(3L, "ROLE_phrasereviewer");

	@Schema(example = "1")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	///
	@Schema(name="ROLES", defaultValue ="ROLE_accountholder", allowableValues = {"ROLE_admin, ROLE_accountholder, ROLE_phrasereviewer"})
	private String name;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	/////
	public UserRole(String name) {
		this.name = name;
	}
	
	private UserRole(Long id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public UserRole() {
		
	}
}
