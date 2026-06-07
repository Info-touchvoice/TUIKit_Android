<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class EventStatistic extends Model
{
    use HasFactory;

    protected $fillable = [
        'event_id',
        'total_participants',
        'total_active_participants',
        'total_completed_participants',
        'total_recharge_amount',
        'total_spending_amount',
        'total_rewards_distributed',
        'total_coins_distributed',
        'total_diamonds_distributed',
        'total_vip_days_distributed',
        'total_frames_distributed',
        'total_badges_distributed',
        'total_gifts_distributed',
    ];

    protected $casts = [
        'total_participants' => 'integer',
        'total_active_participants' => 'integer',
        'total_completed_participants' => 'integer',
        'total_recharge_amount' => 'decimal:2',
        'total_spending_amount' => 'decimal:2',
        'total_rewards_distributed' => 'decimal:2',
        'total_coins_distributed' => 'integer',
        'total_diamonds_distributed' => 'integer',
        'total_vip_days_distributed' => 'integer',
        'total_frames_distributed' => 'integer',
        'total_badges_distributed' => 'integer',
        'total_gifts_distributed' => 'integer',
    ];

    public $timestamps = false;

    /**
     * Relationships
     */
    public function event()
    {
        return $this->belongsTo(Event::class);
    }

    /**
     * Methods
     */
    public function refresh()
    {
        $event = $this->event;

        $participations = UserEventParticipation::where('event_id', $event->id)->get();
        $rewards = UserRewardsClaimed::where('event_id', $event->id)->get();

        $this->total_participants = $participations->count();
        $this->total_active_participants = $participations
            ->where('participation_status', 'active')
            ->count();
        $this->total_completed_participants = $participations
            ->where('participation_status', 'completed')
            ->count();

        $this->total_recharge_amount = $participations->sum('recharge_amount');
        $this->total_spending_amount = $participations->sum('spending_amount');

        // Calculate reward distribution
        $this->total_coins_distributed = $rewards
            ->where('reward_type', 'coins')
            ->sum('reward_amount');
        $this->total_diamonds_distributed = $rewards
            ->where('reward_type', 'diamonds')
            ->sum('reward_amount');
        $this->total_vip_days_distributed = $rewards
            ->where('reward_type', 'vip_days')
            ->sum('reward_amount');
        $this->total_frames_distributed = $rewards
            ->where('reward_type', 'frames')
            ->sum('reward_amount');
        $this->total_badges_distributed = $rewards
            ->where('reward_type', 'badges')
            ->sum('reward_amount');
        $this->total_gifts_distributed = $rewards
            ->where('reward_type', 'gifts')
            ->sum('reward_amount');

        $this->total_rewards_distributed = $rewards->sum('reward_amount');

        $this->last_updated = now();
        $this->save();

        return $this;
    }

    public function getCompletionRate()
    {
        if ($this->total_participants == 0) {
            return 0;
        }
        return round(($this->total_completed_participants / $this->total_participants) * 100, 2);
    }

    public function getAverageRechargePerUser()
    {
        if ($this->total_participants == 0) {
            return 0;
        }
        return round($this->total_recharge_amount / $this->total_participants, 2);
    }

    public function getAverageSpendingPerUser()
    {
        if ($this->total_participants == 0) {
            return 0;
        }
        return round($this->total_spending_amount / $this->total_participants, 2);
    }

    /**
     * Accessors
     */
    public function getCompletionRatePercentageAttribute()
    {
        return $this->getCompletionRate() . '%';
    }
}
