package com.twochickendevs.foodstoreservice.security;

import com.twochickendevs.foodstoreservice.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "revoked_tokens")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RevokedToken extends BaseEntity {

    @Id
    @Column(nullable = false, columnDefinition = "TEXT")
    private String token;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;
}
