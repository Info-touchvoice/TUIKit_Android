<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class UserRewardsClaimed extends Model
{
    use HasFactory;

    protected $table = 'user_rewards_claimed';

    protected $fillable = [
        'user_id',
        'event_id',
        'event_reward_id',
        'reward_type',
        'reward_amount',
        'reward_data',
        'claimed_at',
        'processed_at',
        'status',
    ];

    protected $casts = [
        'reward_amount' => 'integer',
        'reward_data' => 'json',
        'claimed_at' => 'datetime',
        'processed_at' => 'datetime',
    ];

    /**
     * Status Types
     */
    const STATUS_PENDING = 'pending';
    const STATUS_PROCESSED = 'processed';
    const STATUS_FAILED = 'failed';

    public static function getStatuses()
    {
        return [
            self::STATUS_PENDING,
            self::STATUS_PROCESSED,
            self::STATUS_FAILED,
        ];
    }

    /**
     * Relationships
     */
    public function user()
    {
        return $this->belongsTo(User::class);
    }

    public function event()
    {
        return $this->belongsTo(Event::class);
    }

    public function eventReward()
    {
        return $this->belongsTo(EventReward::class);
    }

    /**
     * Scopes
     */
    public function scopeByUser($query, $userId)
    {
        return $query->where('user_id', $userId);
    }

    public function scopeByEvent($query, $eventId)
    {
        return $query->where('event_id', $eventId);
    }

    public function scopeByType($query, $type)
    {
        return $query->where('reward_type', $type);
    }

    public function scopePending($query)
    {
        return $query->where('status', self::STATUS_PENDING);
    }

    public function scopeProcessed($query)
    {
        return $query->where('status', self::STATUS_PROCESSED);
    }

    public function scopeFailed($query)
    {
        return $query->where('status', self::STATUS_FAILED);
    }

    public function scopeByStatus($query, $status)
    {
        return $query->where('status', $status);
    }

    /**
     * Methods
     */
    public function markAsProcessed()
    {
        $this->status = self::STATUS_PROCESSED;
        $this->processed_at = now();
        $this->save();
        return $this;
    }

    public function markAsFailed()
    {
        $this->status = self::STATUS_FAILED;
        $this->save();
        return $this;
    }

    public function isPending()
    {
        return $this->status === self::STATUS_PENDING;
    }

    public function isProcessed()
    {
        return $this->status === self::STATUS_PROCESSED;
    }

    public function isFailed()
    {
        return $this->status === self::STATUS_FAILED;
    }
}
