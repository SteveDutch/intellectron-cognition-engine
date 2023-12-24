package com.stevedutch.intellectron.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity // Class name = Zettel, DB Table name = zettel
@Table(name = "zettel")
public class Zettel {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "zettel_id"/* , nullable = false */)
	private Long zettelId;
	
	@Column(name = "topic",length = 255 )
	private String topic;
	
	@OneToOne(mappedBy = "zettel", cascade = CascadeType.ALL)
	private Note note;
	
	@Column(name = "added")
	private LocalDate added;
	@Column(name = "changed")
	private LocalDate changed;
	
	@Column(name = "signature")
	private Integer signature;
	
	@ManyToMany
	@JoinTable(name = "tagged", joinColumns = @JoinColumn(name = "zettel_id"), 
	inverseJoinColumns = @JoinColumn(name = "tag_id"))
	private List<Tag> tags = new ArrayList<>();

	@ManyToOne
	@JoinColumn(name = "tekst_id")
	private Tekst tekst;

	//constructor junit test
//	public Zettel(Long zettelId, String topic, Note note, LocalDate added, LocalDate changed, Integer signature,
//			List<Author> authors, List<Tag> tags, List<Tekst> teksts) {
//		super();
//		this.zettelId = zettelId;
//		this.topic = topic;
//		this.note = note;
//		this.added = added;
//		this.changed = changed;
//		this.signature = signature;
//		this.authors = authors;
//		this.zettelTags = tags;
//		this.teksts = teksts;
//	}

	


	public Zettel() {
		// TODO Auto-generated constructor stub
	}

	public Zettel(Note note, Tekst tekst) {
		super();
		this.note = note;
		this.tekst = tekst;
	}

	// Getter & Setter
	public Long getZettelId() {
		return zettelId;
	}
	
	public void setZettelId(Long zettelId) {
		this.zettelId = zettelId;
	}
	
	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public Note getNote() {
		return note;
	}

	public void setNote(Note note) {
		this.note = note;
	}


	public LocalDate getAdded() {
		return added;
	}


	public void setAdded(LocalDate added) {
		this.added = added;
	}

	public LocalDate getChanged() {
		return changed;
	}

	public void setChanged(LocalDate changed) {
		this.changed = changed;
	}

	public Integer getSignature() {
		return signature;
	}

	public void setSignature(Integer signature) {
		this.signature = signature;
	}

	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}
	
    public void addTag(Tag tag) {
    	this.tags = new ArrayList<>(Arrays.asList(tag));
    }


	public Tekst getTekst() {
		return tekst;
	}
	
	
	public void setTekst(Tekst tekst) {
		this.tekst = tekst;
	}
	
	@Override
	public String toString() {
		return "Zettel [zettelId=" + zettelId + ", topic=" + topic + ", note=" + note + ", added=" + added
				+ ", changed=" + changed + ", signature=" + signature + ", tags=" + tags + ", tekst="
				+ tekst + "]";
	}
	

}
