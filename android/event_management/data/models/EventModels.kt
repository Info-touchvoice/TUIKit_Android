package com.touchvoice.eventmanagement.data.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

// Event Data Models
data class Event(
    val id: Long,
    @SerializedName("event_type")
    val eventType: String,
    val name: String,
    val description: String?,
    @SerializedName("banner_image_url")
    val bannerImageUrl: String?,
    @SerializedName("icon_url")
    val iconUrl: String?,
    @SerializedName("event_rules")
    val eventRules: String?,
    @SerializedName("start_date")
    val startDate: String,
    @SerializedName("end_date")
    val endDate: String,
    @SerializedName("is_active")
    val isActive: Boolean,
    @SerializedName("is_published")
    val isPublished: Boolean,
    val status: String?,
    @SerializedName("days_remaining")
    val daysRemaining: Int?,
    val rewards: List<EventReward>?,
    val statistics: EventStatistic?
) : Serializable

data class EventReward(
    val id: Long,
    @SerializedName("event_id")
    val eventId: Long,
    @SerializedName("reward_tier")
    val rewardTier: Int,
    @SerializedName("reward_type")
    val rewardType: String,
    @SerializedName("reward_amount")
    val rewardAmount: Int,
    @SerializedName("reward_name")
    val rewardName: String?,
    @SerializedName("reward_description")
    val rewardDescription: String?,
    @SerializedName("trigger_type")
    val triggerType: String,
    @SerializedName("trigger_condition")
    val triggerCondition: Int,
    @SerializedName("max_claims")
    val maxClaims: Int?,
    @SerializedName("claims_count")
    val claimsCount: Int
) : Serializable

data class EventStatistic(
    val id: Long,
    @SerializedName("event_id")
    val eventId: Long,
    @SerializedName("total_participants")
    val totalParticipants: Int,
    @SerializedName("total_active_participants")
    val totalActiveParticipants: Int,
    @SerializedName("total_completed_participants")
    val totalCompletedParticipants: Int,
    @SerializedName("total_recharge_amount")
    val totalRechargeAmount: Double,
    @SerializedName("total_spending_amount")
    val totalSpendingAmount: Double,
    @SerializedName("total_rewards_distributed")
    val totalRewardsDistributed: Double,
    @SerializedName("total_coins_distributed")
    val totalCoinsDistributed: Long,
    @SerializedName("total_diamonds_distributed")
    val totalDiamondsDistributed: Long
) : Serializable

data class UserEventParticipation(
    val id: Long,
    @SerializedName("user_id")
    val userId: Long,
    @SerializedName("event_id")
    val eventId: Long,
    @SerializedName("participation_status")
    val participationStatus: String,
    @SerializedName("current_progress")
    val currentProgress: Int,
    @SerializedName("target_progress")
    val targetProgress: Int,
    @SerializedName("recharge_amount")
    val rechargeAmount: Double,
    @SerializedName("spending_amount")
    val spendingAmount: Double,
    @SerializedName("login_streak")
    val loginStreak: Int,
    @SerializedName("ranking_position")
    val rankingPosition: Int?,
    @SerializedName("tasks_completed")
    val tasksCompleted: Int,
    @SerializedName("joined_at")
    val joinedAt: String,
    @SerializedName("completed_at")
    val completedAt: String?
) : Serializable

data class UserRewardsClaimed(
    val id: Long,
    @SerializedName("user_id")
    val userId: Long,
    @SerializedName("event_id")
    val eventId: Long,
    @SerializedName("event_reward_id")
    val eventRewardId: Long,
    @SerializedName("reward_type")
    val rewardType: String,
    @SerializedName("reward_amount")
    val rewardAmount: Int,
    @SerializedName("reward_data")
    val rewardData: Map<String, Any>?,
    @SerializedName("claimed_at")
    val claimedAt: String,
    @SerializedName("processed_at")
    val processedAt: String?,
    val status: String
) : Serializable

data class EventNotification(
    val id: Long,
    @SerializedName("event_id")
    val eventId: Long,
    @SerializedName("user_id")
    val userId: Long,
    @SerializedName("notification_type")
    val notificationType: String,
    val title: String,
    val message: String,
    @SerializedName("is_read")
    val isRead: Boolean,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("read_at")
    val readAt: String?
) : Serializable

// Response Wrappers
data class ApiResponse<T>(
    val success: Boolean,
    val message: String?,
    val data: T?
)

data class PaginatedResponse<T>(
    val success: Boolean,
    val data: List<T>,
    val pagination: PaginationInfo
)

data class PaginationInfo(
    @SerializedName("current_page")
    val currentPage: Int,
    @SerializedName("per_page")
    val perPage: Int,
    val total: Int,
    @SerializedName("total_pages")
    val totalPages: Int
)

data class AuthResponse(
    val success: Boolean,
    val message: String?,
    val data: AuthData?
)

data class AuthData(
    val token: String,
    val user: User
)

data class User(
    val id: Long,
    val username: String,
    val email: String,
    val role: String
)
