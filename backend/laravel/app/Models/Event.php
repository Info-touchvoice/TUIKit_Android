<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\SoftDeletes;

class Event extends Model
{
    use HasFactory, SoftDeletes;

    protected $fillable = [
        'event_type',
        'name',
        'description',
        'banner_image_url',
        'icon_url',
        'event_rules',
        'start_date',
        'end_date',
        'is_active',
        'is_published',
        'created_by',
    ];

    protected $casts = [
        'start_date' => 'datetime',
        'end_date' => 'datetime',
        'is_active' => 'boolean',
        'is_published' => 'boolean',
    ];

    /**
     * Event types
     */
    const TYPE_SEASONAL = 'seasonal';
    const TYPE_VIP = 'vip';
    const TYPE_RECHARGE = 'recharge';
    const TYPE_CONSUMPTION = 'consumption';
    const TYPE_RANKING = 'ranking';
    const TYPE_FAMILY = 'family';
    const TYPE_ELECTION = 'election';
    const TYPE_LOGIN = 'login';
    const TYPE_COOPERATIVE = 'cooperative';
    const TYPE_CUSTOM = 'custom';

    public static function getEventTypes()
    {
        return [
            self::TYPE_SEASONAL,
            self::TYPE_VIP,
            self::TYPE_RECHARGE,
            self::TYPE_CONSUMPTION,
            self::TYPE_RANKING,
            self::TYPE_FAMILY,
            self::TYPE_ELECTION,
            self::TYPE_LOGIN,
            self::TYPE_COOPERATIVE,
            self::TYPE_CUSTOM,
        ];
    }

    /**
     * Relationships
     */
    public function rewards()
    {
        return $this->hasMany(EventReward::class);
    }

    public function participations()
    {
        return $this->hasMany(UserEventParticipation::class);
    }

    public function notifications()
    {
        return $this->hasMany(EventNotification::class);
    }

    public function statistics()
    {
        return $this->hasOne(EventStatistic::class);
    }

    public function creator()
    {
        return $this->belongsTo(AdminUser::class, 'created_by');
    }

    public function userRewardsClaimed()
    {
        return $this->hasMany(UserRewardsClaimed::class);
    }

    /**
     * Scopes
     */
    public function scopeActive($query)
    {
        return $query->where('is_active', true);
    }

    public function scopePublished($query)
    {
        return $query->where('is_published', true);
    }

    public function scopeUpcoming($query)
    {
        return $query->where('start_date', '>', now());
    }

    public function scopeOngoing($query)
    {
        return $query->where('start_date', '<=', now())
            ->where('end_date', '>=', now());
    }

    public function scopeCompleted($query)
    {
        return $query->where('end_date', '<', now());
    }

    public function scopeByType($query, $type)
    {
        return $query->where('event_type', $type);
    }

    /**
     * Accessors
     */
    public function getIsUpcomingAttribute()
    {
        return $this->start_date > now();
    }

    public function getIsOngoingAttribute()
    {
        return $this->start_date <= now() && $this->end_date >= now();
    }

    public function getIsCompletedAttribute()
    {
        return $this->end_date < now();
    }

    public function getDaysRemainingAttribute()
    {
        if ($this->is_completed) {
            return 0;
        }
        return $this->end_date->diffInDays(now());
    }

    public function getDaysUntilStartAttribute()
    {
        if ($this->is_upcoming) {
            return $this->start_date->diffInDays(now());
        }
        return 0;
    }

    public function getParticipantsCountAttribute()
    {
        return $this->participations()->count();
    }

    public function getActiveParticipantsCountAttribute()
    {
        return $this->participations()
            ->where('participation_status', 'active')
            ->count();
    }

    /**
     * Methods
     */
    public function getStatus()
    {
        if ($this->is_upcoming) {
            return 'upcoming';
        } elseif ($this->is_ongoing) {
            return 'ongoing';
        } else {
            return 'completed';
        }
    }

    public function activate()
    {
        $this->update(['is_active' => true]);
        return $this;
    }

    public function deactivate()
    {
        $this->update(['is_active' => false]);
        return $this;
    }

    public function publish()
    {
        $this->update(['is_published' => true]);
        return $this;
    }

    public function unpublish()
    {
        $this->update(['is_published' => false]);
        return $this;
    }
}
