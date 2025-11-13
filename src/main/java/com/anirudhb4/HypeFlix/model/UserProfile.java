package com.anirudhb4.HypeFlix.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    private UUID id;

    @Column(unique = true)
    private String username;

    private String region;

    @OneToMany(mappedBy = "user")
    private Set<HypePoint> hypePoints;

    @OneToMany(mappedBy = "author")
    private Set<DiscussionPost> posts;
}
