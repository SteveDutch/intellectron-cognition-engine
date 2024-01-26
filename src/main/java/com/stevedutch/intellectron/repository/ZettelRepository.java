package com.stevedutch.intellectron.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.stevedutch.intellectron.domain.Zettel;

@Repository
public interface ZettelRepository extends JpaRepository<Zettel, Long>{
	
	public Optional<Zettel> findById(Long Id);

	@Query("select zettel from Zettel zettel"
			+ " left join fetch zettel.note")
	public List<Zettel> findAllZettelWithTopic();
	
	@Query("select zettel from Zettel zettel group by zettel.zettelId order by zettel.added desc limit 10")
	public List<Zettel> findLast10Zettel();
	
}
