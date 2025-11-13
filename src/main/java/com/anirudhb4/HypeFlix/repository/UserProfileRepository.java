package com.anirudhb4.HypeFlix.repository;

import com.anirudhb4.HypeFlix.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {
    UserProfile findByUsername(String username);
}
