package com.stevedutch.intellectron.domain;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;

@Entity // Class name = Reference, DB Table name = references
@Table(name = "pointer")
public class Reference {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	@Column(name = "reference_id")
	private Long id;
	
	@ManyToOne
	private Zettel sourceZettel;
	
	@ManyToOne
	private Zettel targetZettel;
	
	@Enumerated(EnumType.STRING)
	private ReferenceType type;
	
	private String connectionNote; // Brief explanation of why these notes are connected
	
	public String getConnectionNote() {
		return connectionNote;
	}

	public void setConnectionNote(String connectionNote) {
		this.connectionNote = connectionNote;
	}

	public ReferenceType getType() {
		return type;
	}

	public void setType(ReferenceType type) {
		this.type = type;
	}

	@ManyToMany(mappedBy = "references")
	private Set<Zettel> zettels = new HashSet<>();
	
	public Reference() {
		
	}
	
	public Reference(String sourceSignature, String targetSignature) {
		this.sourceZettel = new Zettel();
		this.sourceZettel.setZettelId(Long.parseLong(sourceSignature));
		this.targetZettel = new Zettel();
		this.targetZettel.setZettelId(Long.parseLong(targetSignature));
	}

	// Getter & Setter
	
	public Long getReferenceId() {
		return id;
	}

	public void setReferenceId(Long referenceId) {
		this.id = referenceId;
	}

	public Long getOriginZettel() {
		return sourceZettel != null ? sourceZettel.getZettelId() : null;
	}

	public void setOriginZettel(Long originZettel) {
		this.sourceZettel = new Zettel();
		this.sourceZettel.setZettelId(originZettel);
	}

	public Zettel getSourceZettel() {
		return sourceZettel;
	}

	public void setSourceZettel(Zettel sourceZettel) {
		this.sourceZettel = sourceZettel;
	}

	public Zettel getTargetZettel() {
		return targetZettel;
	}

	public void setTargetZettel(Zettel targetZettel) {
		this.targetZettel = targetZettel;
	}

	// Legacy methods for backward compatibility
	public Long getTargetZettelId() {
		return targetZettel != null ? targetZettel.getZettelId() : null;
	}

	public void setTargetZettelId(Long targetZettelId) {
		this.targetZettel = new Zettel();
		this.targetZettel.setZettelId(targetZettelId);
	}

	public Set<Zettel> getZettels() {
		return zettels;
	}

	public void setZettels(Set<Zettel> zettels) {
		this.zettels = zettels;
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		

        if (!(obj instanceof Reference))
            return false;

		Reference other = (Reference) obj;
        return id != null &&
        		id.equals(other.getReferenceId());
	}

	@Override
	public String toString() {
		return "Reference [referenceId=" + id + ", originZettel=" + getOriginZettel() + ", targetZettel="
				+ getTargetZettel() + " ,  Anzahl der Zettel =\n " + Optional.of(zettels.stream().count()) + " \n"
						+  "]";
	}

	public Long getReferenceId(Reference reference) {
		this.id = reference.getReferenceId();
		return id;
	}
	
	

}

