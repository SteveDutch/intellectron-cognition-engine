package com.stevedutch.intellectron.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity // Class name = Zettel, DB Table name = zettel
@Table(name = "zettel")
public class Zettel {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "zettel_id"/* , nullable = false */)
	private Long zettelId;
	
	@Column(name = "topic",length =255 )
	private String topic;
	
	@OneToOne(mappedBy = "zettel", cascade = CascadeType.ALL)
	private Note note;
	
	// TODO add title (where in ERD & here) meinte ich damit topic?
//	@Column(name = "title", length = 255)	
//	private String textTitle;
	

	@Column(name = "added")
	private LocalDate added;
	@Column(name = "changed")
	private LocalDate changed;
	
	@Column(name = "signature")
	private Integer signature;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "zettel_authors", joinColumns = @JoinColumn(name = "zettel_id"), 
	inverseJoinColumns = @JoinColumn(name = "author_id"))
	private List<Author> authors = new ArrayList<>();
	
	@ManyToMany
	@JoinTable(name = "tagged", joinColumns = @JoinColumn(name = "zettel_id"), 
	inverseJoinColumns = @JoinColumn(name = "tag_id"))
	private List<ZettelTag> zettelTags = new ArrayList<>();

	@ManyToMany
	@JoinTable(name = "zettel_texts", joinColumns = @JoinColumn(name = "zettel_id"), 
	inverseJoinColumns = @JoinColumn(name = "tekst_id"))
	private List<Tekst> teksts = new ArrayList<>();

	//constructor junit test
	public Zettel(Long zettelId, String topic, Note note, LocalDate added, LocalDate changed, Integer signature,
			List<Author> authors, List<ZettelTag> zettelTags, List<Tekst> teksts) {
		super();
		this.zettelId = zettelId;
		this.topic = topic;
		this.note = note;
		this.added = added;
		this.changed = changed;
		this.signature = signature;
		this.authors = authors;
		this.zettelTags = zettelTags;
		this.teksts = teksts;
	}

	
	public Zettel() {
		// TODO Auto-generated constructor stub
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

	/**
	 * @return the added
	 */
	public LocalDate getAdded() {
		return added;
	}

	/**
	 * @param added the added to set
	 */
	public void setAdded(LocalDate added) {
		this.added = added;
	}

	/**
	 * @return the changed
	 */
	public LocalDate getChanged() {
		return changed;
	}

	/**
	 * @param changed the changed to set
	 */
	public void setChanged(LocalDate changed) {
		this.changed = changed;
	}

	/**
	 * @return the signature
	 */
	public Integer getSignature() {
		return signature;
	}

	/**
	 * @param signature the signature to set
	 */
	public void setSignature(Integer signature) {
		this.signature = signature;
	}

	/**
	 * @return the authors
	 */
	public List<Author> getAuthors() {
		return authors;
	}

	/**
	 * @param authors the authors to set
	 */
	public void setAuthors(List<Author> authors) {
		this.authors = authors;
	}

	public List<ZettelTag> getTags() {
		return zettelTags;
	}

	public void setTags(List<ZettelTag> zettelTags) {
		this.zettelTags = zettelTags;
	}

	public List<Tekst> getTeksts() {
		return teksts;
	}

	public void setTeksts(List<Tekst> teksts) {
		this.teksts = teksts;
	}

	@Override
	public String toString() {
		return "Zettel [zettelId=" + zettelId + ", topic=" + topic + ", note=" + note + ", added=" + added
				+ ", changed=" + changed + ", signature=" + signature + ", authors=" + authors + ", zettelTags="
				+ zettelTags + ", teksts=" + teksts + "]";
	}
	

}
