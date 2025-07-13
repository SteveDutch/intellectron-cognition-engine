package com.stevedutch.intellectron.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import com.stevedutch.intellectron.record.ZettelDtoRecord;

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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

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
	private LocalDateTime added;
	
	@Column(name = "changed")
	private LocalDateTime changed;
	

	
	@ManyToMany(fetch = FetchType.LAZY, 
			cascade = {
	        CascadeType.PERSIST,
	        CascadeType.MERGE})
	@JoinTable(name = "zettel_references", joinColumns = @JoinColumn(name = "zettel_id"), 
	inverseJoinColumns = @JoinColumn(name = "reference_id"))
	private Set<Reference> references = new HashSet<>();
	
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "tagged", joinColumns = @JoinColumn(name = "zettel_id"), 
	inverseJoinColumns = @JoinColumn(name = "tag_id"))
	private List<Tag> tags = new ArrayList<>();

	@ManyToOne  (cascade = CascadeType.PERSIST) 
	@JoinColumn(name = "text_id")
	private Tekst tekst;

	public Zettel() {
	}

	public Zettel(String topic) {
		this.topic = topic;
	}

	public Zettel(Note note, Tekst tekst) {
		this.note = note;
		this.tekst = tekst;
	}

	public Zettel(ZettelDtoRecord zettelDto) {
		this.zettelId = zettelDto.zettel().getZettelId();
		this.topic = zettelDto.zettel().getTopic();
		this.note = zettelDto.zettel().getNote();
		this.added = zettelDto.zettel().getAdded();
		this.changed = zettelDto.zettel().getChanged();

		this.references = zettelDto.zettel().getReferences();
		this.tags = zettelDto.zettel().getTags();
		this.tekst = zettelDto.zettel().getTekst();
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


	public LocalDateTime getAdded() {
		return added;
	}


	public void setAdded(LocalDateTime added) {
		this.added = added;
	}

	public LocalDateTime getChanged() {
		return changed;
	}

	public void setChanged(LocalDateTime changed) {
		this.changed = changed;
	}



	public Set<Reference> getReferences() {
		return references;
	}

	public void setReferences(Set<Reference> references) {
		this.references = references;
	}
	
	public void addReference(Reference reference) {
        if (this.references == null) {
            this.references = new HashSet<>();
        }
        this.references.add(reference);
    }

    /**
     * Adds a typed reference to another Zettel with optional connection note.
     * 
     * @param target The Zettel to link to
     * @param type The type of connection
     * @param note Optional explanation of the connection
     * @throws IllegalArgumentException if target is null or equals this Zettel
     */
    public void addReference(Zettel target, ReferenceType type, String note) {
        Objects.requireNonNull(target, "Target Zettel cannot be null");
        
        if (this.equals(target)) {
            throw new IllegalArgumentException("Cannot create self-reference");
        }
        
        ReferenceType safeType = (type != null) ? type : ReferenceType.RELATES_TO;
        
        Reference ref = new Reference();
        ref.setSourceZettel(this);
        ref.setTargetZettel(target);
        ref.setType(safeType);
        ref.setConnectionNote(note);
        
        if (this.references == null) {
            this.references = new HashSet<>();
        }
        this.references.add(ref);
    }

    /**
     * Removes a reference to another Zettel.
     * 
     * @param reference The reference to remove
     */
    public void removeReference(Reference reference) {
        if (this.references != null) {
            this.references.remove(reference);
        }
    }

	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}
	
    public void addTag(Tag tag) {
        if (this.tags == null) {
            this.tags = new ArrayList<>();
        }
        this.tags.add(tag);
    }

    /**
     * Removes a tag from this Zettel.
     * 
     * @param tag The tag to remove
     */
    public void removeTag(Tag tag) {
        if (this.tags != null) {
            this.tags.remove(tag);
        }
    }


	public Tekst getTekst() {
		return tekst;
	}
	
	
	public void setTekst(Tekst tekst) {
		this.tekst = tekst;
	}

	/**
	 * Generates a human-friendly identifier dynamically at runtime.
	 * Combines topic and creation date for readability.
	 *
	 * @return a readable identifier for the Zettel
	 */
	@Transient
	public String getHumanFriendlyIdentifier() {
		String formattedDate = added != null ? added.toLocalDate().toString() : "undated";
		String safeTopic = topic != null ? topic : "untitled";
		return safeTopic + " (" + formattedDate + ")";
	}
	
    @Override
    public int hashCode() {
        return zettelId != null ? zettelId.hashCode() : 0;
    }

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

        if (!(obj instanceof Zettel))
            return false;

		Zettel other = (Zettel) obj;
        return zettelId != null &&
        		zettelId.equals(other.getZettelId());
	}
	
	@Override
	public String toString() {
		return "\n Zettel \n [zettelId =" + zettelId + ", topic=" + topic + " Note : "+ Optional.ofNullable(note).map(Note::getNoteText).orElse("No note available, it's NULL") + " added=" + added
				+ ", changed=" + changed
				+ ",  Anzahl der tags=" + Optional.ofNullable(tags).map(list -> list .size()) .orElse(0)
				+ ", \n tekst=" + tekst 
				+ " , \n  References: " + Optional.ofNullable(references).map(list -> list.toString()).orElse("keine Verweise") + "]\n \n ";
	}

}
