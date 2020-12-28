package com.mes.springsecurityapi.domain.security;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.CredentialsContainer;

import javax.persistence.Column;
import javax.persistence.Transient;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Created by mesar on 12/22/2020
 */
@Slf4j
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("users")
public class User implements CredentialsContainer{

    @Id
    private Integer id;

    private String username;

    private String password;

    private String lastLogin;

    private String verificationToken;

    private LocalDateTime verificationExpiresAt;

    @Builder.Default
    private Boolean isVerified = false;

    public User(User user){
        this(
                user.getId(),
                user.getUsername(),
                user.getPassword()
        );
    }

    @PersistenceConstructor
    public User(Integer id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.verificationToken = UUID.randomUUID().toString();
        this.verificationExpiresAt = LocalDateTime.now().plusMinutes(10);
    }

    @Builder.Default
    private Boolean accountNonExpired = true;

    @Builder.Default
    private Boolean accountNonLocked = true;

    @Builder.Default
    private Boolean credentialsNonExpired = true;

    @Builder.Default
    private Boolean enabled = true;

    @Builder.Default
    private Boolean useGoogle2f = false;

    private String google2faSecret;

    @With
    @Transient
    private Boolean google2faRequired = true;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdDate;

    @UpdateTimestamp
    private Timestamp lastModifiedDate;

    @Override
    public void eraseCredentials() {
        this.password = null;
    }
}