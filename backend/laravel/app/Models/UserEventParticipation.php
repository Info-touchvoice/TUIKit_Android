<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class UserEventParticipation extends Model
{
    use HasFactory;

    protected $table = 'user_event_participation';

    protected $fillable = [
        'user_id',
        'event_id',
        'participation_status',
        'current_progress',
        'target_progress',
        'recharge_amount',
        'spending_amount',
        'login_streak',
        'ranking_position',
        'tasks_completed',
        'joined_at',
        'completed_at',
    ];

    protected $casts = [
        'current_progress' => 'integer',
        'target_progress' => 'integer',
        'recharge_amount' => 'decimal:2',
        'spending_amount' => 'decimal:2',
        'login_streak' => 'integer',
        'ranking_position' => 'integer',
        'tasks_completed' => 'integer',
        'joined_at' => 'datetime',
        'completed_at' => 'datetime',
    ];

    /**
     * Status Types
     */
    const STATUS_PENDING = 'pending';
    const STATUS_ACTIVE = 'active';
    const STATUS_COMPLETED = 'completed';
    const STATUS_CANCELLED = 'cancelled';

    public static function getStatuses()
    {
        return [
            self::STATUS_PENDING,
            self::STATUS_ACTIVE,
            self::STATUS_COMPLETED,
            self::STATUS_CANCELLED,
        ];
    }

    /**
     * Relationships
     */
    public function event()
    {
        return $this->belongsTo(Event::class);
    }

    public function user()
    {
        return $this->belongsTo(User::class);
    }

    public function rewardsClaimed()
    {
        return $this->belongsToMany(
            UserRewardsClaimed::class,
            'user_id',
            'user_id'
        )->where('event_id', $this->event_id);
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

    public function scopeActive($query)
    {
        return $query->where('participation_status', self::STATUS_ACTIVE);
    }

    public function scopeCompleted($query)
    {
        return $query->where('participation_status', self::STATUS_COMPLETED);
    }

    public function scopePending($query)
    {
        return $query->where('participation_status', self::STATUS_PENDING);
    }

    public function scopeByStatus($query, $status)
    {
        return $query->where('participation_status', $status);
    }

    public function scopeOrderByProgress($query)
    {
        return $query->orderByRaw('(current_progress / target_progress) DESC');
    }

    public function scopeOrderByRanking($query)
    {
        return $query->whereNotNull('ranking_position')
            ->orderBy('ranking_position', 'ASC');
    }

    /**
     * Accessors
     */
    public function getProgressPercentageAttribute()
    {
        if ($this->target_progress == 0) {
            return 0;
        }
        return round(($this->current_progress / $this->target_progress) * 100, 2);
    }

    public function getRemainingProgressAttribute()
    {
        return max(0, $this->target_progress - $this->current_progress);
    }

    public function getIsCompletedAttribute()
    {
        return $this->participation_status === self::STATUS_COMPLETED;
    }

    /**
     * Methods
     */
    public function updateProgress($amount)
    {
        $this->current_progress += $amount;
        if ($this->current_progress >= $this->target_progress) {
            $this->complete();
        }
        $this->save();
        return $this;
    }

    public function updateRechargeAmount($amount)
    {
        $this->recharge_amount += $amount;
        $this->save();
        return $this;
    }

    public function updateSpendingAmount($amount)
    {
        $this->spending_amount += $amount;
        $this->save();
        return $this;
    }

    public function updateLoginStreak($days)
    {
        $this->login_streak = $days;
        $this->save();
        return $this;
    }

    public function updateRankingPosition($position)
    {
        $this->ranking_position = $position;
        $this->save();
        return $this;
    }

    public function incrementTasksCompleted()
    {
        $this->increment('tasks_completed');
        return $this;
    }

    public function activate()
    {
        $this->participation_status = self::STATUS_ACTIVE;
        $this->save();
        return $this;
    }

    public function complete()
    {
        $this->participation_status = self::STATUS_COMPLETED;
        $this->completed_at = now();
        $this->save();
        return $this;
    }

    public function cancel()
    {
        $this->participation_status = self::STATUS_CANCELLED;
        $this->save();
        return $this;
    }
}
