package com.savvato.tribeapp.services;

import com.savvato.tribeapp.controllers.dto.PhraseSequenceDataRequest;
import com.savvato.tribeapp.controllers.dto.PhraseSequenceRequest;
import com.savvato.tribeapp.dto.AttributeDTO;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AttributesService {

    Optional<Map<String, List<AttributeDTO>>> getAttributesByUserId(Long id);

    void updatePhraseSequences(long userId, PhraseSequenceDataRequest phrase);

    boolean loadSequence(PhraseSequenceRequest phrases);

    List<AttributeDTO> getPhrasesToBeReviewedByUserId(Long userId);
}

