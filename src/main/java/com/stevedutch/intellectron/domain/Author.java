package com.stevedutch.intellectron.domain;

import jakarta.persistence.Column; // mit Spring 3.0 kommt j́akarta statt javax
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity // Class name = Author, DB Table name = author
@Table(name = "author")
public class Author {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "author_id")
	private Long authorId;

    @Column(name = "first_name", length = 105)
	private String authorFirstName;
    @Column(name = "family_name", length = 105)
	private String authorFamilyName;
    
	/**
	 * @return the authorId
	 */
	public Long getAuthorId() {
		return authorId;
	}
	/**
	 * @param authorId the authorId to set
	 */
	public void setAuthorId(Long authorId) {
		this.authorId = authorId;
	}
	/**
	 * @return the authorFirstName
	 */
	public String getAuthorFirstName() {
		return authorFirstName;
	}
	/**
	 * @param authorFirstName the authorFirstName to set
	 */
	public void setAuthorFirstName(String authorFirstName) {
		this.authorFirstName = authorFirstName;
	}
	/**
	 * @return the authorFamilyName
	 */
	public String getAuthorFamilyName() {
		return authorFamilyName;
	}
	/**
	 * @param authorFamilyName the authorFamilyName to set
	 */
	public void setAuthorFamilyName(String authorFamilyName) {
		this.authorFamilyName = authorFamilyName;
	}
	@Override
	public String toString() {
		return "Author [authorId= " + authorId + ", authorFirstName= " + authorFirstName + ", authorFamilyName= "
				+ authorFamilyName + "]";
	}
	
	

}
