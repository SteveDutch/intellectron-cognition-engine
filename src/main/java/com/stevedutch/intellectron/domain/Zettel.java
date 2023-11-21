package com.stevedutch.intellectron.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity // Class name = Zettel, DB Table name = zettel
@Table(name = "zettel")
public class Zettel {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "zettel_id", nullable = false)
	private Long zettelId;
	
	// TODO tekst_id
	@Column(name = "title", length = 255)	
	private String textTitle;
	@Column(name = "source", length = 255)	
	private String source;
	// TODO added
	// TODO article_date
	@Column(name = "text_type")
	private int textType;
	// TODO signature, tag
	
	// Getter & Setter
	public Long getZettelId() {
		return zettelId;
	}
	
	public void setZettelId(Long zettelId) {
		this.zettelId = zettelId;
	}
	
	public String getTextTitle() {
		return textTitle;
	}
	
	public void setTextTitle(String textTitle) {
		this.textTitle = textTitle;
	}
	
	public String getSource() {
		return source;
	}
	
	public void setSource(String source) {
		this.source = source;
	}
	
	public int getTextType() {
		return textType;
	}
	
	public void setTextType(int textType) {
		this.textType = textType;
	}

	@Override
	public String toString() {
		return "Zettel [zettelId= " + zettelId + ", textTitle= " + textTitle + ", source=" + source + ", textType="
				+ textType + "]";
	}
	

}
