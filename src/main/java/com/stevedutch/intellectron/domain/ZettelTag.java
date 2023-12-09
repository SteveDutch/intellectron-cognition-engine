package com.stevedutch.intellectron.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity 
@Table(name = "tags")
public class ZettelTag {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "tag_id")
	private Long id;
	
	@Column(name = "tag", length =100)
	private String tagText;
	
	@ManyToMany(mappedBy = "zettelTags")
	private List<Zettel> zettel = new ArrayList<>();

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long tagId) {
		this.id = tagId;
	}

	/**
	 * @return the tagText
	 */
	public String getTagText() {
		return tagText;
	}

	/**
	 * @param tagText the tagText to set
	 */
	public void setTagText(String tagText) {
		this.tagText = tagText;
	}

	public List<Zettel> getZettel() {
		return zettel;
	}

	public void setZettel(List<Zettel> zettel) {
		this.zettel = zettel;
	}

	@Override
	public String toString() {
		return "ZettelTag [id=" + id + ", tagText=" + tagText + ", zettel=" + zettel + "]";
	}
	
	
	

}
