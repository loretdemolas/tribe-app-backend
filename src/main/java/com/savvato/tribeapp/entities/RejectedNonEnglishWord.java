package com.savvato.tribeapp.entities;

import jakarta.persistence.*;

@Entity
@Table(name="rejected_non_english_word")
public class RejectedNonEnglishWord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private String word;

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public RejectedNonEnglishWord(String word) {
        this.setWord(word);
    }

    public RejectedNonEnglishWord(long id, String word) {
        this.setId(id);
        this.setWord(word);
    }

    public RejectedNonEnglishWord() {

    }
}
