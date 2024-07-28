package com.stevedutch.intellectron.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.stevedutch.intellectron.domain.Tag;
import com.stevedutch.intellectron.domain.Zettel;

@Repository
public interface ZettelRepository extends JpaRepository<Zettel, Long> {

	public Optional<Zettel> findById(Long Id);

	@Query("select zettel from Zettel zettel" + " left join fetch zettel.note")
	public List<Zettel> findAllZettelWithTopic();

//	@Query("select zettel from Zettel zettel group by zettel.zettelId order by zettel.added desc limit 10")
	@Query("SELECT zettel FROM Zettel zettel ORDER BY added DESC LIMIT 10")
	public List<Zettel> findLast10Zettel();

	@Query("SELECT zettel FROM Zettel zettel WHERE zettel.id = (SELECT FLOOR(MAX(zettel.id) * RAND()) FROM Zettel zettel) ORDER BY zettel.id LIMIT 1")
	public Zettel findOneRandomZettel();

	public List<Zettel> findZettelByTags(Tag searchTag);

	@Query("SELECT zettel FROM Zettel zettel WHERE zettel.topic LIKE %:searchTerm%")
	public List<Zettel> findZettelByTopicFragment(@Param("searchTerm") String topicFragment);

	@Query("SELECT zettel FROM Zettel zettel JOIN Note note ON zettel.id = note.zettel.id WHERE note.noteText LIKE %:searchTerm%")
	public List<Zettel> findZettelByNoteFragment(@Param("searchTerm") String noteFragment);

	@Query("SELECT zettel FROM Zettel zettel JOIN zettel.tekst text WHERE text.text LIKE  %:searchTerm%")
	public List<Zettel> findZettelByTextFragment(@Param("searchTerm") String textFragment);

	@Query("SELECT zettel FROM Zettel zettel JOIN Note note ON zettel.id = note.zettel.id WHERE note.noteText = :noteText")
	public Zettel findOneZettelByNote(String noteText);

}
