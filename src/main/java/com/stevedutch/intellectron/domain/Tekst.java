package com.stevedutch.intellectron.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity // NOTE Class name = Tekst, DB Table name = texts
@Table(name = "texts")
public class Tekst {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "text_id")
	private Long textId;

	@Column(name = "tekst", length = 16777216)
	private String textContent;
	
	@Column(name = "tekstdato")
	private LocalDate textDate;	
	
	@Column(name = "source", length =700)
	private String source;

	@OneToMany(mappedBy = "tekst")
	private List<Zettel> zettels;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "texts_authors", joinColumns = @JoinColumn(name = "text_id"), 
	inverseJoinColumns = @JoinColumn(name = "author_id"))
	private List<Author> authors = new ArrayList<>();

	// Getter & Setter
	
	public Long getTextId() {
		return textId;
	}

	public void setTextId(Long textId) {
		this.textId = textId;
	}

	public String getTextContent() {
		return textContent;
	}

	public void setTextContent(String content) {
		this.textContent = content;
	}

	public LocalDate getTextDate() {
		return textDate;
	}

	public void setTextDate(LocalDate textDate) {
		this.textDate = textDate;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public List<Zettel> getZettels() {
		return zettels;
	}

	public void setZettels(List<Zettel> zettels) {
		this.zettels = zettels;
	}

	public List<Author> getAuthors() {
		return authors;
	}

	public void setAuthors(List<Author> authors) {
		this.authors = authors;
	}

	@Override
	public String toString() {
		return "Tekst [textId=" + textId + ", textContent=" + textContent + ", textDate=" + textDate + ", source="
				+ source + ", zettels=" + zettels + ", authors=" + authors + "]";
	}

}