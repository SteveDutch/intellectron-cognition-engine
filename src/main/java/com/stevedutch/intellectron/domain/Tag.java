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
public class Tag {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "tag_id")
	private Long tagId;
	
	@Column(name = "tag", length =45)
	private String tagText;
	
	@ManyToMany(mappedBy = "tags")
	private List<Zettel> zettel = new ArrayList<>();

	/**
	 * @return the tagId
	 */
	public Long getTagId() {
		return tagId;
	}

	/**
	 * @param tagId the tagId to set
	 */
	public void setTagId(Long tagId) {
		this.tagId = tagId;
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
		return "Tag [tagId=" + tagId + ", tagText=" + tagText + ", zettel=" + zettel + "]";
	}
	
	
	

}
