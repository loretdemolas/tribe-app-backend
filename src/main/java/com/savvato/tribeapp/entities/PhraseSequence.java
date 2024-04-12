package com.savvato.tribeapp.entities;

import javax.persistence.*;

@Entity
@Table(name = "phrase_sequence")
public class PhraseSequence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "phrase_id")
    private Long phraseId;

    @Column(name = "position")
    private Integer position;

    // Constructors, getters, and setters

    public PhraseSequence() {
    }

    public PhraseSequence(Long userId, Long phraseId, Integer position) {
        this.userId = userId;
        this.phraseId = phraseId;
        this.position = position;
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPhraseId() {
        return phraseId;
    }

    public void setPhraseId(Long phraseId) {
        this.phraseId = phraseId;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }
}
