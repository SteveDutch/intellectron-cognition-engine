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
	private Long id;
	
	@Column(name = "tag", length =100)
	private String tagText;
	
	@ManyToMany(mappedBy = "tags")
	private List<Zettel> zettels = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long tagId) {
		this.id = tagId;
	}

	public String getTagText() {
		return tagText;
	}

	public void setTagText(String tagText) {
		this.tagText = tagText;
	}

	public List<Zettel> getZettels() {
		return zettels;
	}

	public void setZettels(List<Zettel> zettel) {
		this.zettels = zettel;
	}

	@Override
	public String toString() {
		return "Tag [id=" + id + ", tagText=" + tagText + ", zettels=" + zettels + "]";
	}
	
	
	

}
