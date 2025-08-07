package com.stevedutch.intellectron.domain;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity // Class name = Reference, DB Table name = pointer
@Table(name = "pointer")
public class Reference {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	@Column(name = "reference_id")
	private Long id;
	
	@Column(name = "source_zettel_id", nullable = false)
	private Long sourceZettelId;
	
	@Column(name = "target_zettel_id", nullable = false)
	private Long targetZettelId;
	
	@Enumerated(EnumType.STRING)
	private ReferenceType type;
	
	private String connectionNote; // Brief explanation of why these notes are connected
	
	// Constructors
	public Reference() {
	}
	
	/**
	 * Creates a reference between two existing Zettel entities.
	 * Use this constructor when you have fully loaded Zettel objects.
	 */
	public Reference(Zettel sourceZettel, Zettel targetZettel, ReferenceType type) {
		this.sourceZettelId = sourceZettel.getZettelId();
		this.targetZettelId = targetZettel.getZettelId();
		this.type = type;
	}
	
	public Reference(Long sourceZettelId, Long targetZettelId, ReferenceType type, String connectionNote) {
		super();
		this.sourceZettelId = sourceZettelId;
		this.targetZettelId = targetZettelId;
		this.type = type;
		this.connectionNote = connectionNote;
	}

	// Getters and Setters
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public void setSourceZettelId(Long sourceZettelId) {
		this.sourceZettelId = sourceZettelId;
	}

	public Long getSourceZettelId() {
		return sourceZettelId;
	}
	
	public void setSourceZettel(Zettel sourceZettel) {
		this.sourceZettelId = sourceZettel.getZettelId();
	}
	/**
	 * Gets the target Zettel ID.
	 * @return target Zettel ID
	 */
	public Long getTargetZettelId() {
		return targetZettelId;
	}
	
	public void setTargetZettelId(Long targetZettelId) {
		this.targetZettelId = targetZettelId;
	}
	

	public void setTargetZettel(Zettel targetZettel) {
		this.targetZettelId = targetZettel.getZettelId();
	}

	public ReferenceType getType() {
		return type;
	}

	public void setType(ReferenceType type) {
		this.type = type;
	}

	public String getConnectionNote() {
		return connectionNote;
	}

	public void setConnectionNote(String connectionNote) {
		this.connectionNote = connectionNote;
	}

	// Convenience methods for backward compatibility
	
	/**
	 * @deprecated Use getId() instead for consistency
	 */
	@Deprecated
	public Long getReferenceId() {
		return id;
	}

	/**
	 * @deprecated Use setId() instead for consistency
	 */
	@Deprecated
	public void setReferenceId(Long referenceId) {
		this.id = referenceId;
	}


	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (!(obj instanceof Reference))
			return false;

		Reference other = (Reference) obj;
		return id != null && id.equals(other.getId());
	}

	@Override
	public String toString() {
		return String.format("Reference [id=%d, source=%s, target=%s, type=%s, note='%s']", 
			id, 
			getSourceZettelId(), 
			getTargetZettelId(), 
			type,
			connectionNote);
	}
}

