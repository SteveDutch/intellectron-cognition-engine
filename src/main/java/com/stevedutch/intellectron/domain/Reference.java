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

@Entity // Class name = Reference, DB Table name = references
@Table(name = "pointer")
public class Reference {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	@Column(name = "reference_id")
	private Long referenceId;
	
	@Column(name = "origin_zettel")
	private Long originZettel;	
	
	@Column(name = "target_zettel")
	private Long targetZettel;
	
	@ManyToMany(mappedBy = "references")
	private Set<Zettel> zettels = new HashSet<>();
	
	public Reference() {
		
	}
	
	public Reference(String signature) {
		this.originZettel = Long.parseLong(signature);
	}

	// Getter & Setter
	
	public Long getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(Long referenceId) {
		this.referenceId = referenceId;
	}

	public Long getOriginZettel() {
		return originZettel;
	}

	public void setOriginZettel(Long originZettel) {
		this.originZettel = originZettel;
	}

	public Long getTargetZettel() {
		return targetZettel;
	}

	public void setTargetZettel(Long targetZettel) {
		this.targetZettel = targetZettel;
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
        return referenceId != null &&
        		referenceId.equals(other.getReferenceId());
	}

	@Override
	public String toString() {
		return "Reference [referenceId=" + referenceId + ", originZettel=" + originZettel + ", targetZettel="
				+ targetZettel + " ,  Anzahl der Zettel =\n " + Optional.of(zettels.stream().count()) + " \n"
						+  "]";
	}
	
	

}
