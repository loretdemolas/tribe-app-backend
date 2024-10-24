package com.savvato.tribeapp.entities;

import jakarta.persistence.*;
import java.util.Calendar;

@Entity
@IdClass(ReviewDecisionId.class)
public class ReviewDecision {
    @Id
    private Long reviewId;
    @Id
    private Long userId;
    private Long reasonId;
    private java.sql.Timestamp created;

    public ReviewDecision() {
        setCreated();
    }

    public ReviewDecision(Long reviewId, Long userId, Long reasonId) {
        this.reviewId = reviewId;
        this.userId = userId;
        this.reasonId = reasonId;
        setCreated();
    }

    public Long getReviewId() {
        return reviewId;
    }

    public void setReviewId(Long reviewId) {
        this.reviewId = reviewId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getReasonId() {
        return reasonId;
    }

    public void setReasonId(Long reasonId) {
        this.reasonId = reasonId;
    }
    public java.sql.Timestamp getCreated() {
        return created;
    }

    public void setCreated() {
        this.created = java.sql.Timestamp.from(Calendar.getInstance().toInstant());
    }
}
