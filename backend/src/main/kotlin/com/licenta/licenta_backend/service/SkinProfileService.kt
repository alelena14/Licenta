package com.licenta.licenta_backend.service

import com.licenta.licenta_backend.dto.ProfileStatsResponse
import com.licenta.licenta_backend.dto.SkinProfileResponse
import com.licenta.licenta_backend.dto.UpdateSkinProfileRequest
import com.licenta.licenta_backend.model.UserConcern
import com.licenta.licenta_backend.model.UserSkinProfile
import com.licenta.licenta_backend.repository.*
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class SkinProfileService(
    private val userRepository: UserRepository,
    private val profileRepository: UserSkinProfileRepository,
    private val userConcernRepository: UserConcernRepository,
    private val concernRepository: ConcernRepository,
    private val savedChatRepository: SavedChatRepository,
    private val favoriteRepository: FavoriteRepository
) {

    fun getProfile(firebaseUid: String): SkinProfileResponse {

        val user = userRepository.findByFirebaseUid(firebaseUid)
            ?: throw RuntimeException("User not found")

        val profile = profileRepository.findByUserId(user.id)
            ?: return SkinProfileResponse(
                skinType = null,
                concerns = emptyList()
            )

        val concerns = userConcernRepository
            .findAllByProfileId(profile.id)
            .map { it.concernCode }

        return SkinProfileResponse(
            skinType = profile.skinType,
            concerns = concerns
        )
    }

    @Transactional
    fun updateProfile(
        firebaseUid: String,
        request: UpdateSkinProfileRequest
    ): SkinProfileResponse {

        val user = userRepository.findByFirebaseUid(firebaseUid)
            ?: throw RuntimeException("User not found")

        val profile = profileRepository.findByUserId(user.id)
            ?: profileRepository.save(
                UserSkinProfile(
                    user = user
                )
            )

        profile.skinType = request.skinType

        val savedProfile = profileRepository.save(profile)

        userConcernRepository.deleteAllByProfileId(savedProfile.id)

        val concerns = request.concerns.map {
            UserConcern(
                profile = savedProfile,
                concernCode = it
            )
        }

        userConcernRepository.saveAll(concerns)

        return SkinProfileResponse(
            skinType = savedProfile.skinType,
            concerns = request.concerns
        )
    }

    fun getStats(firebaseUid: String): ProfileStatsResponse {
        val user = userRepository.findByFirebaseUid(firebaseUid)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")

        val profile = profileRepository.findByUserId(user.id)
        val skinConcerns = if (profile != null)
            userConcernRepository.findAllByProfileId(profile.id).size
        else 0

        return ProfileStatsResponse(
            conversations = savedChatRepository.countByUserId(user.id),
            savedProducts = favoriteRepository.countByUid(user.id),
            skinConcerns = skinConcerns
        )
    }

    fun saveConcernsFromAnalysis(firebaseUid: String, detectedConcerns: List<String>) {
        val user = userRepository.findByFirebaseUid(firebaseUid)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")

        val profile = profileRepository.findByUserId(user.id)
            ?: UserSkinProfile(user = user).also { profileRepository.save(it) }

        val existing = userConcernRepository.findAllByProfileId(profile.id)
            .map { it.concernCode }.toSet()

        val allConcerns = concernRepository.findAll()
        val nameToCode = allConcerns.associateBy(
            { it.displayName.lowercase().trim() },
            { it.code }
        )

        val newConcerns = detectedConcerns
            .mapNotNull { detected ->
                nameToCode[detected.lowercase().trim()] ?: detected
            }
            .filter { it !in existing }

        newConcerns.forEach { code ->
            userConcernRepository.save(UserConcern(profile = profile, concernCode = code))
        }
    }
}