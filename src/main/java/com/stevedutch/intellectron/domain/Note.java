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
	
	@Id
	private Long zetteld;
	
	@OneToOne
	@MapsId
	@JoinColumn(name = "zettel_id")
	private Zettel zettel;
	
	@Column(length = 65000)
	private String note;

	/**
	 * @return the zetteld
	 */
	public Long getZetteld() {
		return zetteld;
	}

	/**
	 * @param zetteld the zetteld to set
	 */
	public void setZetteld(Long zetteld) {
		this.zetteld = zetteld;
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
	 * @return the note
	 */
	public String getNote() {
		return note;
	}

	/**
	 * @param note the note to set
	 */
	public void setNote(String note) {
		this.note = note;
	}
	
	

}
