package com.savvato.tribeapp.repositories;

import com.savvato.tribeapp.entities.PhraseSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhraseSequenceRepository extends JpaRepository<PhraseSequence, Long> {
    @Query(nativeQuery = true, value = "SELECT * FROM phrase_sequence WHERE user_id = ?1 ORDER BY position")
    List<PhraseSequence> findByUserIdOrderByPosition(Long userId);
}
