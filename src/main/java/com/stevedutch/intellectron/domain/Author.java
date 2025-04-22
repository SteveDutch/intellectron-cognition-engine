package com.stevedutch.intellectron.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import jakarta.persistence.Column; // NOTE mit Spring 3.0 kommt j́akarta statt javax
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import org.hibernate.annotations.NaturalId;

@Entity // NOTE Class name = Author, DB Table name = authors
@Table(name = "authors")
public class Author {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "author_id")
	private Long authorId;

	@NaturalId(mutable = true)
	@Column(name = "first_name", length = 105, nullable = false)
	private String authorFirstName;
	@NaturalId(mutable = true)
	@Column(name = "family_name", length = 105, nullable = false)
	private String authorFamilyName;

	@ManyToMany(mappedBy = "associatedAuthors")
	private List<Tekst> texts = new ArrayList<>();

	public Author(String authorFirstName, String authorFamilyName) {
		this.authorFirstName = authorFirstName;
		this.authorFamilyName = authorFamilyName;
	}

	public Author() {

	}

	// Getter & Setter
	public Long getAuthorId() {
		return authorId;
	}

	public void setAuthorId(Long authorId) {
		this.authorId = authorId;
	}

	public String getAuthorFirstName() {
		return authorFirstName;
	}

	public void setAuthorFirstName(String authorFirstName) {
		this.authorFirstName = authorFirstName;
	}

	public String getAuthorFamilyName() {
		return authorFamilyName;
	}

	public void setAuthorFamilyName(String authorFamilyName) {
		this.authorFamilyName = authorFamilyName;
	}

	public List<Tekst> getTexts() {
		return texts;
	}

	public void setTexts(List<Tekst> texts) {
		this.texts = texts;
	}

	@Override
	public String toString() {
		return "Author \n [authorId=" + authorId + ", \n authorFirstName=" + authorFirstName + ", authorFamilyName="
				+ authorFamilyName + ", \n Anzahl der Texte =" + Optional.of(texts.stream().count()) + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(authorFamilyName, authorFirstName, authorId, texts);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Author other = (Author) obj;
		return Objects.equals(authorFamilyName, other.authorFamilyName)
				&& Objects.equals(authorFirstName, other.authorFirstName) && Objects.equals(authorId, other.authorId)
				&& Objects.equals(texts, other.texts);
	}

}
