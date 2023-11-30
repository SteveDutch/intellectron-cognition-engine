package com.stevedutch.intellectron.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
	@Column(name = "zettel_id", nullable = false)
	private Long zettelId;
	
	@Column(name = "topic",length =255 )
	private String topic;
	
	@OneToOne(mappedBy = "zettel")
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
	
	@ManyToMany
	@JoinTable(name = "zettel_authors", joinColumns = @JoinColumn(name = "zettel_id"), 
	inverseJoinColumns = @JoinColumn(name = "author_id"))
	private List<Author> authors = new ArrayList<>();

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

	@Override
	public String toString() {
		return "Zettel [zettelId=" + zettelId + ", added=" + added + "]";
	}
	

}
