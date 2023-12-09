package com.stevedutch.intellectron.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "notes")
public class Note {

//	@Id
//	private Long zettelId;

	@Id
	private Long zettelId;

	@OneToOne
	@MapsId
	@JoinColumn(name = "zettel_id")
	private Zettel zettel;

	@Column(length = 65000)
	private String content;
	
	// für junit
	public Note(String content) {

		// TODO Auto-generated constructor stub
		this.content = content;
		this.zettel = null;
		this.zettelId = null;
	}

	public Note() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * content
	 * 
	 * @return the zettelId
	 */
	public Long getZettelId() {
		return zettelId;
	}

	/**
	 * @param zettelId the zettelId to set
	 */
	public void setZettelId(Long zettelId) {
		this.zettelId = zettelId;
	}

	/**
	 * @return the zettel
	 */
	public Zettel getZettel() {
		return zettel;
	}

	/**
	 * @param zettel the zettel to set
	 */
	public void setZettel(Zettel zettel) {
		this.zettel = zettel;
	}

	/**
	 * @return the content
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

	@Override
	public String toString() {
		return "Note [zettelId=" + zettelId + ", zettel=" + zettel + ", content=" + content + "]";
	}

}
