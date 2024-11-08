package com.savvato.tribeapp.unit.services;

import com.savvato.tribeapp.controllers.dto.PhraseSequenceDataRequest;
import com.savvato.tribeapp.controllers.dto.PhraseSequenceRequest;
import com.savvato.tribeapp.constants.PhraseTestConstants;
import com.savvato.tribeapp.constants.UserTestConstants;
import com.savvato.tribeapp.dto.AttributeDTO;
import com.savvato.tribeapp.dto.PhraseDTO;
import com.savvato.tribeapp.entities.PhraseSequence;
import com.savvato.tribeapp.entities.ToBeReviewed;
import com.savvato.tribeapp.repositories.PhraseSequenceRepository;
import com.savvato.tribeapp.repositories.ReviewSubmittingUserRepository;
import com.savvato.tribeapp.repositories.ToBeReviewedRepository;
import com.savvato.tribeapp.repositories.UserPhraseRepository;
import com.savvato.tribeapp.services.AttributesService;
import com.savvato.tribeapp.services.AttributesServiceImpl;
import com.savvato.tribeapp.services.PhraseService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class AttributesServiceImplTest implements UserTestConstants, PhraseTestConstants {

    @TestConfiguration
    static class AttributesServiceTestContextConfiguration {
        @Bean
        public AttributesService attributesService() {
            return new AttributesServiceImpl();
        }
    }

    @Autowired
    AttributesService attributesService;
    @MockBean
    PhraseService phraseService;
    @MockBean
    UserPhraseRepository userPhraseRepository;
    @MockBean
    PhraseSequenceRepository phraseSequenceRepository;
    @MockBean
    ToBeReviewedRepository toBeReviewedRepository;
    @MockBean
    ReviewSubmittingUserRepository reviewSubmittingUserRepository;


    @Test
    public void testGetAttributesByUserId_EmptyPhrases() {
        Long userId = 1L;

        when(phraseSequenceRepository.findByUserIdOrderByPosition(userId)).thenReturn(Collections.emptyList());

        when(phraseService.getPhraseInformationByUserId(userId)).thenReturn(Optional.empty());

        Optional<Map<String, List<AttributeDTO>>> result = attributesService.getAttributesByUserId(userId);

        assertTrue(result.isPresent());
        assertTrue(result.get().get("attributes").isEmpty());
        assertTrue(result.get().get("pendingAttributes").isEmpty());
    }

    @Test
    public void getAttributesByUserIdWhenAttributesExist() {
        Long userId = USER1_ID;

        List<PhraseSequence> phraseSequences = Arrays.asList(
                new PhraseSequence(userId, 1L, 1),
                new PhraseSequence(userId, 2L, 2)
        );
        when(phraseSequenceRepository.findByUserIdOrderByPosition(userId)).thenReturn(phraseSequences);

        Map<PhraseDTO, Integer> phraseInformation = Map.of(
                PhraseDTO.builder().id(1L).adverb("enthusiastically").verb("volunteers").preposition("at").noun("UNICEF").build(), 3,
                PhraseDTO.builder().id(2L).adverb("happily").verb("sings").preposition("at").noun("party").build(), 5
        );
        when(phraseService.getPhraseInformationByUserId(userId)).thenReturn(Optional.of(phraseInformation));

        List<AttributeDTO> pendingAttributes = List.of(
                AttributeDTO.builder()
                        .phrase(PhraseDTO.builder().id(3L).verb("review").adverb("carefully").noun("report").preposition("by").build())
                        .userCount(0)
                        .sequence(0)
                        .build()
        );
        when(reviewSubmittingUserRepository.findToBeReviewedIdByUserId(userId)).thenReturn(List.of(3L));
        when(toBeReviewedRepository.findById(3L)).thenReturn(Optional.of(
                new ToBeReviewed(3L, false ,"review", "carefully", "by", "report")
        ));

        Optional<Map<String, List<AttributeDTO>>> result = attributesService.getAttributesByUserId(userId);

        assertTrue(result.isPresent());
        Map<String, List<AttributeDTO>> resultMap = result.get();

        List<AttributeDTO> attributes = resultMap.get("attributes");
        assertEquals(2, attributes.size());

        // Validate the first attribute
        AttributeDTO attr1 = attributes.get(0);
        assertEquals(1L, attr1.phrase.id);
        assertEquals("enthusiastically", attr1.phrase.adverb);
        assertEquals("volunteers", attr1.phrase.verb);
        assertEquals("at", attr1.phrase.preposition);
        assertEquals("UNICEF", attr1.phrase.noun);
        assertEquals(3, attr1.userCount);
        assertEquals(1, attr1.sequence);

        // Validate the second attribute
        AttributeDTO attr2 = attributes.get(1);
        assertEquals(2L, attr2.phrase.id);
        assertEquals("happily", attr2.phrase.adverb);
        assertEquals("sings", attr2.phrase.verb);
        assertEquals("at", attr2.phrase.preposition);
        assertEquals("party", attr2.phrase.noun);
        assertEquals(5, attr2.userCount);
        assertEquals(2, attr2.sequence);

        // Verify pending attributes list
        List<AttributeDTO> pendingAttributesResult = resultMap.get("pendingAttributes");
        assertEquals(1, pendingAttributesResult.size());
        AttributeDTO pendingAttr = pendingAttributesResult.get(0);
        assertEquals(3L, pendingAttr.phrase.id);
        assertEquals("review", pendingAttr.phrase.adverb);
        assertEquals("carefully", pendingAttr.phrase.verb);
        assertEquals("report", pendingAttr.phrase.noun);
        assertEquals("by", pendingAttr.phrase.preposition);
        assertEquals(0, pendingAttr.userCount);
        assertEquals(0, pendingAttr.sequence);
    }

    @Test
    public void testGetAttributesByUserId_WithPhrases() {
        Long userId = 1L;

        List<PhraseSequence> phraseSequences = Arrays.asList(
                new PhraseSequence(userId, 1L, 1),
                new PhraseSequence(userId, 2L, 2)
        );


        Map<PhraseDTO, Integer> phraseInfo = new HashMap<>();
        PhraseDTO phrase1 = PhraseDTO.builder().id(1L).adverb("enthusiastically").verb("volunteers").preposition("at").noun("UNICEF").build();
        PhraseDTO phrase2 = PhraseDTO.builder().id(2L).adverb("happily").verb("sings").preposition("at").noun("party").build();

        phraseInfo.put(phrase1, 3);
        phraseInfo.put(phrase2, 5);

        when(phraseSequenceRepository.findByUserIdOrderByPosition(userId)).thenReturn(phraseSequences);
        when(phraseService.getPhraseInformationByUserId(userId)).thenReturn(Optional.of(phraseInfo));

        Optional<Map<String, List<AttributeDTO>>> result = attributesService.getAttributesByUserId(userId);

        assertTrue(result.isPresent());
        List<AttributeDTO> attributes = result.get().get("attributes");
        assertNotNull(attributes);

        List<AttributeDTO> pendingAttributes = result.get().get("pendingAttributes");
        assertNotNull(pendingAttributes);

        assertEquals(2, attributes.size());

        // Validate the first attribute
        AttributeDTO attr1 = attributes.get(0);
        assertEquals(1L, attr1.phrase.id);
        assertEquals("enthusiastically", attr1.phrase.adverb);
        assertEquals("volunteers", attr1.phrase.verb);
        assertEquals("at", attr1.phrase.preposition);
        assertEquals("UNICEF", attr1.phrase.noun);
        assertEquals(3, attr1.userCount);
        assertEquals(1, attr1.sequence);

        // Validate the second attribute
        AttributeDTO attr2 = attributes.get(1);
        assertEquals(2L, attr2.phrase.id);
        assertEquals("happily", attr2.phrase.adverb);
        assertEquals("sings", attr2.phrase.verb);
        assertEquals("at", attr2.phrase.preposition);
        assertEquals("party", attr2.phrase.noun);
        assertEquals(5, attr2.userCount);
        assertEquals(2, attr2.sequence);
    }

    @Test
    public void testGetPhrasesToBeReviewedByUserId() {
        Long userId = 1L;

        List<Long> toBeReviewedIds = Arrays.asList(100L, 200L);
        when(reviewSubmittingUserRepository.findToBeReviewedIdByUserId(userId)).thenReturn(toBeReviewedIds);

        ToBeReviewed toBeReviewed1 = new ToBeReviewed(100L, true, "run", "quickly", "towards", "goal");
        ToBeReviewed toBeReviewed2 = new ToBeReviewed(200L, false, "jump", "high", "over", "fence");

        when(toBeReviewedRepository.findById(100L)).thenReturn(Optional.of(toBeReviewed1));
        when(toBeReviewedRepository.findById(200L)).thenReturn(Optional.of(toBeReviewed2));

        List<AttributeDTO> result = attributesService.getPhrasesToBeReviewedByUserId(userId);

        // Assertions to verify the result
        assertEquals(2, result.size());

        // Verifying details of the first AttributeDTO
        AttributeDTO attribute1 = result.get(0);
        assertEquals(100L, attribute1.phrase.id);
        assertEquals("run", attribute1.phrase.adverb);
        assertEquals("quickly", attribute1.phrase.verb);
        assertEquals("towards", attribute1.phrase.preposition);
        assertEquals("goal", attribute1.phrase.noun);
        assertEquals(0, attribute1.userCount);
        assertEquals(0, attribute1.sequence);

        // Verifying details of the second AttributeDTO
        AttributeDTO attribute2 = result.get(1);
        assertEquals(200L, attribute2.phrase.id);
        assertEquals("jump", attribute2.phrase.adverb);
        assertEquals("high", attribute2.phrase.verb);
        assertEquals("over", attribute2.phrase.preposition);
        assertEquals("fence", attribute2.phrase.noun);
        assertEquals(0, attribute2.userCount);
        assertEquals(0, attribute2.sequence);
    }
  
    @Test
    public void testLoadSequence() {
        // Given
        Long userId = 1L;
        PhraseSequenceRequest sequence = new PhraseSequenceRequest(userId, Arrays.asList(
                new PhraseSequenceDataRequest(1L, 1),
                new PhraseSequenceDataRequest(2L, 2)
        ));

        // When
        boolean result = attributesService.loadSequence(sequence);

        // Then
        verify(phraseSequenceRepository, times(1)).storeOrUpdatePhrases(userId, 1L, 1);
        verify(phraseSequenceRepository, times(1)).storeOrUpdatePhrases(userId, 2L, 2);
        assertTrue(result);
    }

    @Test
    public void testUpdatePhraseSequences() {
        // Given
        Long userId = 1L;
        PhraseSequenceDataRequest phrase = new PhraseSequenceDataRequest(1L, 1);

        // When
        attributesService.updatePhraseSequences(userId, phrase);

        // Then
        verify(phraseSequenceRepository, times(1)).storeOrUpdatePhrases(userId, 1L, 1);
    }

    @Test
    public void testLoadSequence_WithEmptyPhrases() {
        // Given
        Long userId = 1L;
        PhraseSequenceRequest sequence = new PhraseSequenceRequest(userId, Collections.emptyList());

        // When
        boolean result = attributesService.loadSequence(sequence);

        // Then
        verify(phraseSequenceRepository, never()).storeOrUpdatePhrases(anyLong(), anyLong(), anyInt());
        assertTrue(result);
    }

    @Test
    public void testLoadSequence_WithNullPhrases() {
        // Given
        Long userId = 1L;
        PhraseSequenceRequest sequence = new PhraseSequenceRequest(userId, null);

        // When and Then
        assertThrows(NullPointerException.class, () -> attributesService.loadSequence(sequence));
    }

}
