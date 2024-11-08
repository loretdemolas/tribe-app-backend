package com.savvato.tribeapp.services;

import com.savvato.tribeapp.controllers.dto.PhraseSequenceDataRequest;
import com.savvato.tribeapp.controllers.dto.PhraseSequenceRequest;
import com.savvato.tribeapp.dto.AttributeDTO;
import com.savvato.tribeapp.dto.PhraseDTO;
import com.savvato.tribeapp.entities.PhraseSequence;
import com.savvato.tribeapp.entities.ToBeReviewed;
import com.savvato.tribeapp.repositories.PhraseSequenceRepository;
import com.savvato.tribeapp.repositories.ReviewSubmittingUserRepository;
import com.savvato.tribeapp.repositories.ToBeReviewedRepository;
import com.savvato.tribeapp.repositories.UserPhraseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AttributesServiceImpl implements AttributesService {

    @Autowired
    PhraseService phraseService;

    @Autowired
    UserPhraseRepository userPhraseRepository;

    @Autowired
    private PhraseSequenceRepository phraseSequenceRepository;

    @Autowired
    private ToBeReviewedRepository toBeReviewedRepository;

    @Autowired
    ReviewSubmittingUserRepository reviewSubmittingUserRepository;

    @Override
    public Optional<Map<String, List<AttributeDTO>>> getAttributesByUserId(Long userId) {

        List<PhraseSequence> phraseSequences = phraseSequenceRepository.findByUserIdOrderByPosition(userId);

        Map<Long, Integer> phraseIdToSequenceMap = phraseSequences.stream()
                .collect(Collectors.toMap(
                        PhraseSequence::getPhraseId,
                        PhraseSequence::getPosition
                ));

        // Get all user phrases as phraseDTOs
        Optional<Map<PhraseDTO, Integer>> optUserPhraseDTOs = phraseService.getPhraseInformationByUserId(userId);

        Map<String, List<AttributeDTO>> resultMap = new HashMap<>();
        resultMap.put("attributes", Collections.emptyList());
        resultMap.put("pendingAttributes", Collections.emptyList());

        // If there are phrases, build DTO and add to attributes list
        if (optUserPhraseDTOs.isPresent()) {
            Map<PhraseDTO, Integer> phraseDTOUserCountMap = optUserPhraseDTOs.get();

            List<AttributeDTO> attributes = phraseDTOUserCountMap
                    .entrySet()
                    .stream()
                    .map(entry -> {
                        PhraseDTO phraseDTO = entry.getKey();
                        Integer userCount = entry.getValue();

                        // Populate the sequence in PhraseDTO
                        int sequence= phraseIdToSequenceMap.get(phraseDTO.id); // Find sequence
                        List<PhraseSequence> phrases = phraseSequenceRepository.findByUserIdOrderByPosition(userId);
                        return AttributeDTO.builder()
                                .phrase(phraseDTO)
                                .userCount(userCount)
                                .sequence(sequence) // No change to user count
                                .build();
                    })
                    .collect(Collectors.toList());
            attributes.sort(Comparator.comparingLong(a -> (a.phrase.id)));
            List<AttributeDTO> pendingAttributes = getPhrasesToBeReviewedByUserId(userId);

            resultMap.put("attributes", attributes);
            resultMap.put("pendingAttributes", pendingAttributes);

            return Optional.of(resultMap);
        }

        // If no phrases found, return an empty list
        return Optional.of(resultMap);
    }

    public List<AttributeDTO> getPhrasesToBeReviewedByUserId(Long userId) {
        List<Long> toBeReviewedIds = reviewSubmittingUserRepository.findToBeReviewedIdByUserId(userId);
        List<ToBeReviewed> toBeReviewedList = new ArrayList<>();
        for (Long id : toBeReviewedIds) {
            Optional<ToBeReviewed> optionalToBeReviewed = toBeReviewedRepository.findById(id);
            optionalToBeReviewed.ifPresent(toBeReviewedList::add);
        }

        return toBeReviewedList.stream()
                .map(toBeReviewed -> AttributeDTO.builder()
                        .phrase(PhraseDTO.builder()
                                .id(toBeReviewed.getId())
                                .verb(toBeReviewed.getVerb())
                                .adverb(toBeReviewed.getAdverb())
                                .preposition(toBeReviewed.getPreposition())
                                .noun(toBeReviewed.getNoun())
                                .build())
                        .userCount(0)
                        .sequence(0)
                        .build()
                )
                .collect(Collectors.toList());
    }

    //create a senario where false is returned
    @Transactional
    public boolean loadSequence(PhraseSequenceRequest sequence){
        for (PhraseSequenceDataRequest phrase : sequence.getPhrases()) {
            updatePhraseSequences(sequence.getUserId(), phrase);
        }

        return true;
    }

    @Transactional
    public void updatePhraseSequences(long userId, PhraseSequenceDataRequest phrase) {

        // Iterate over the list of phrases to update their positio
            phraseSequenceRepository.storeOrUpdatePhrases(
                    userId,
                    phrase.phraseId,
                    phrase.sequence
            );

    }
}
