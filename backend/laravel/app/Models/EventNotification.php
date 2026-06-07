<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class EventNotification extends Model
{
    use HasFactory;

    protected $fillable = [
        'event_id',
        'user_id',
        'notification_type',
        'title',
        'message',
        'is_read',
        'created_at',
        'read_at',
    ];

    protected $casts = [
        'is_read' => 'boolean',
        'created_at' => 'datetime',
        'read_at' => 'datetime',
    ];

    /**
     * Notification Types
     */
    const TYPE_EVENT_STARTED = 'event_started';
    const TYPE_EVENT_ENDED = 'event_ended';
    const TYPE_REWARD_EARNED = 'reward_earned';
    const TYPE_MILESTONE_REACHED = 'milestone_reached';

    public static function getNotificationTypes()
    {
        return [
            self::TYPE_EVENT_STARTED,
            self::TYPE_EVENT_ENDED,
            self::TYPE_REWARD_EARNED,
            self::TYPE_MILESTONE_REACHED,
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

    public function scopeUnread($query)
    {
        return $query->where('is_read', false);
    }

    public function scopeRead($query)
    {
        return $query->where('is_read', true);
    }

    public function scopeByType($query, $type)
    {
        return $query->where('notification_type', $type);
    }

    public function scopeRecent($query)
    {
        return $query->orderBy('created_at', 'DESC');
    }

    /**
     * Methods
     */
    public function markAsRead()
    {
        if (!$this->is_read) {
            $this->is_read = true;
            $this->read_at = now();
            $this->save();
        }
        return $this;
    }

    public function markAsUnread()
    {
        if ($this->is_read) {
            $this->is_read = false;
            $this->read_at = null;
            $this->save();
        }
        return $this;
    }

    /**
     * Accessors
     */
    public function getTypeLabel()
    {
        $labels = [
            self::TYPE_EVENT_STARTED => 'Event Started',
            self::TYPE_EVENT_ENDED => 'Event Ended',
            self::TYPE_REWARD_EARNED => 'Reward Earned',
            self::TYPE_MILESTONE_REACHED => 'Milestone Reached',
        ];

        return $labels[$this->notification_type] ?? $this->notification_type;
    }
}
