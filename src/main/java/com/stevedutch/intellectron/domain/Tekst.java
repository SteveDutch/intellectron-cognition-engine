package com.stevedutch.intellectron.domain;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity // NOTE Class name = Tekst, DB Table name = texts
@Table(name = "texts")
public class Tekst {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "text_id")
	private Long textId;

	@Column(name = "tekst", length = 16777216)
	private String content;
	
	@Column(name = "tekstdato")
	private LocalDate textDate;	
	
	@Column(name = "source", length =700)
	private String source;

	/**
	 * @return Long return the textId
	 */
	public Long getTextId() {
		return textId;
	}

	/**
	 * @param textId the textId to set
	 */
	public void setTextId(Long contentId) {
		this.textId = contentId;
	}

	/**
	 * @return String return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @return the textDate
	 */
	public LocalDate getTextDate() {
		return textDate;
	}

	/**
	 * @param textDate the textDate to set
	 */
	public void setTextDate(LocalDate textDate) {
		this.textDate = textDate;
	}

	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}

	@Override
	public String toString() {
		return "Tekst [textId=" + textId + ", content=" + content + ", textDate=" + textDate + ", source=" + source
				+ "]";
	}

}