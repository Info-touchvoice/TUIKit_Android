<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class EventReward extends Model
{
    use HasFactory;

    protected $fillable = [
        'event_id',
        'reward_tier',
        'reward_type',
        'reward_amount',
        'reward_name',
        'reward_description',
        'trigger_type',
        'trigger_condition',
        'max_claims',
        'claims_count',
    ];

    protected $casts = [
        'reward_amount' => 'integer',
        'reward_tier' => 'integer',
        'trigger_condition' => 'integer',
        'max_claims' => 'integer',
        'claims_count' => 'integer',
    ];

    /**
     * Reward Types
     */
    const TYPE_COINS = 'coins';
    const TYPE_DIAMONDS = 'diamonds';
    const TYPE_VIP_DAYS = 'vip_days';
    const TYPE_FRAMES = 'frames';
    const TYPE_BADGES = 'badges';
    const TYPE_GIFTS = 'gifts';
    const TYPE_CUSTOM = 'custom';

    public static function getRewardTypes()
    {
        return [
            self::TYPE_COINS,
            self::TYPE_DIAMONDS,
            self::TYPE_VIP_DAYS,
            self::TYPE_FRAMES,
            self::TYPE_BADGES,
            self::TYPE_GIFTS,
            self::TYPE_CUSTOM,
        ];
    }

    /**
     * Trigger Types
     */
    const TRIGGER_RECHARGE_AMOUNT = 'recharge_amount';
    const TRIGGER_SPENDING_AMOUNT = 'spending_amount';
    const TRIGGER_LOGIN_STREAK = 'login_streak';
    const TRIGGER_RANKING_POSITION = 'ranking_position';
    const TRIGGER_TASK_COMPLETION = 'task_completion';

    public static function getTriggerTypes()
    {
        return [
            self::TRIGGER_RECHARGE_AMOUNT,
            self::TRIGGER_SPENDING_AMOUNT,
            self::TRIGGER_LOGIN_STREAK,
            self::TRIGGER_RANKING_POSITION,
            self::TRIGGER_TASK_COMPLETION,
        ];
    }

    /**
     * Relationships
     */
    public function event()
    {
        return $this->belongsTo(Event::class);
    }

    public function userRewardsClaimed()
    {
        return $this->hasMany(UserRewardsClaimed::class);
    }

    /**
     * Scopes
     */
    public function scopeByEvent($query, $eventId)
    {
        return $query->where('event_id', $eventId);
    }

    public function scopeByType($query, $type)
    {
        return $query->where('reward_type', $type);
    }

    public function scopeAvailable($query)
    {
        return $query->where(function ($q) {
            $q->whereNull('max_claims')
                ->orWhereRaw('claims_count < max_claims');
        });
    }

    /**
     * Methods
     */
    public function canClaim()
    {
        if (is_null($this->max_claims)) {
            return true;
        }
        return $this->claims_count < $this->max_claims;
    }

    public function incrementClaimsCount()
    {
        $this->increment('claims_count');
        return $this;
    }

    public function getRemainingClaimsCount()
    {
        if (is_null($this->max_claims)) {
            return null; // Unlimited
        }
        return max(0, $this->max_claims - $this->claims_count);
    }

    /**
     * Accessors
     */
    public function getIsAvailableAttribute()
    {
        return $this->canClaim();
    }

    public function getTriggerLabel()
    {
        $labels = [
            self::TRIGGER_RECHARGE_AMOUNT => 'Recharge Amount (Cents)',
            self::TRIGGER_SPENDING_AMOUNT => 'Spending Amount (Cents)',
            self::TRIGGER_LOGIN_STREAK => 'Login Streak (Days)',
            self::TRIGGER_RANKING_POSITION => 'Ranking Position',
            self::TRIGGER_TASK_COMPLETION => 'Task Completion (Count)',
        ];

        return $labels[$this->trigger_type] ?? $this->trigger_type;
    }
}
