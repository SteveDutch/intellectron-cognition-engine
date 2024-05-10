package com.stevedutch.intellectron.domain;

import java.util.Objects;

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
	private Long zettelId;

	@OneToOne
	@MapsId
	@JoinColumn(name = "zettel_id")
	private Zettel zettel;

	@Column(length = 65000)
	private String noteText;
	


 
	
	public Note() {
		// TODO Auto-generated constructor stub
	}

	public Long getZettelId() {
		return zettelId;
	}

	public void setZettelId(Long zettelId) {
		this.zettelId = zettelId;
	}

	public Zettel getZettel() {
		return zettel;
	}

	public void setZettel(Zettel zettel) {
		this.zettel = zettel;
	}

	public String getNoteText() {
		return noteText;
	}

	public void setNoteText(String content) {
		this.noteText = content;
	}

	@Override
	public String toString() {
		return "Note [zettelId=" + zettelId + ", zettel=" + zettel + ", noteText=" + noteText + "]";
	}



	@Override
	public int hashCode() {
		return Objects.hash(noteText, zettel, zettelId);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Note other = (Note) obj;
		return Objects.equals(noteText, other.noteText) && Objects.equals(zettel, other.zettel)
				&& Objects.equals(zettelId, other.zettelId);
	}

}
