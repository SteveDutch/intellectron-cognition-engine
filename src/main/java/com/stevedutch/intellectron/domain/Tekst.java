package com.stevedutch.intellectron.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
	private String text;
	
	@Column(name = "tekstdato")
	private LocalDate textDate;	
	
	@Column(name = "source", length = 700)
	private String source;

	@OneToMany(mappedBy = "tekst", cascade = CascadeType.ALL)
	private List<Zettel> zettels;
	
	@ManyToMany(fetch = FetchType.LAZY, 
			cascade = {
	        CascadeType.PERSIST,
	        CascadeType.MERGE})
	@JoinTable(name = "texts_authors", joinColumns = @JoinColumn(name = "text_id"), 
	inverseJoinColumns = @JoinColumn(name = "author_id"))
	private List<Author> associatedAuthors = new ArrayList<>();

	// Getter & Setter
	
	public Tekst(String text) {
		this.text = text;
	}

	public Tekst() {
		// TODO Auto-generated constructor stub
	}

	public Long getTextId() {
		return textId;
	}

	public void setTextId(Long textId) {
		this.textId = textId;
	}

	public String getText() {
		return text;
	}

	public void setText(String content) {
		this.text = content;
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
		// um auch einen Zettel hinzufügen zu können, wenn nichts vorhanden, bzw. == NULL
		if (zettels == null) {
            zettels = new ArrayList<>();
        }
		return zettels;
	}

	public void setZettels(List<Zettel> zettels) {
		this.zettels = zettels;
	}

	public List<Author> getAssociatedAuthors() {
		return associatedAuthors;
	}

	public void setAssociatedAuthors(List<Author> authors) {
		this.associatedAuthors = authors;
	}

	@Override
	public String toString() {
		return "Tekst [textId=" + textId + ", text=" + text + ", textDate=" + textDate + ", source=" + source
				 + ", associatedAuthors=" + associatedAuthors + "ZETTELS, wieviele: " + Optional.ofNullable(zettels).map(list -> list
						.size())
						.orElse(0) + "]";
	}

}