<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Support\Facades\Hash;
use Tymon\JWTAuth\Contracts\JWTSubject;
use Illuminate\Foundation\Auth\User as Authenticatable;

class AdminUser extends Authenticatable implements JWTSubject
{
    use HasFactory;

    protected $table = 'admin_users';

    protected $fillable = [
        'username',
        'email',
        'password_hash',
        'role',
        'is_active',
        'last_login',
    ];

    protected $hidden = [
        'password_hash',
    ];

    protected $casts = [
        'is_active' => 'boolean',
        'last_login' => 'datetime',
    ];

    /**
     * User Roles
     */
    const ROLE_SUPER_ADMIN = 'super_admin';
    const ROLE_ADMIN = 'admin';
    const ROLE_MODERATOR = 'moderator';

    public static function getRoles()
    {
        return [
            self::ROLE_SUPER_ADMIN,
            self::ROLE_ADMIN,
            self::ROLE_MODERATOR,
        ];
    }

    /**
     * JWT Implementation
     */
    public function getJWTIdentifier()
    {
        return $this->getKey();
    }

    public function getJWTCustomClaims()
    {
        return [
            'role' => $this->role,
            'username' => $this->username,
        ];
    }

    /**
     * Relationships
     */
    public function eventsCreated()
    {
        return $this->hasMany(Event::class, 'created_by');
    }

    /**
     * Scopes
     */
    public function scopeActive($query)
    {
        return $query->where('is_active', true);
    }

    public function scopeByRole($query, $role)
    {
        return $query->where('role', $role);
    }

    /**
     * Methods
     */
    public function setPassword($password)
    {
        $this->password_hash = Hash::make($password);
        return $this;
    }

    public function updateLastLogin()
    {
        $this->last_login = now();
        $this->save();
        return $this;
    }

    public function activate()
    {
        $this->is_active = true;
        $this->save();
        return $this;
    }

    public function deactivate()
    {
        $this->is_active = false;
        $this->save();
        return $this;
    }

    public function isSuperAdmin()
    {
        return $this->role === self::ROLE_SUPER_ADMIN;
    }

    public function isAdmin()
    {
        return $this->role === self::ROLE_ADMIN || $this->isSuperAdmin();
    }

    public function isModerator()
    {
        return $this->role === self::ROLE_MODERATOR || $this->isAdmin();
    }

    public function canCreateEvent()
    {
        return $this->isAdmin();
    }

    public function canEditEvent($event)
    {
        if ($this->isSuperAdmin()) {
            return true;
        }
        return $event->created_by === $this->id;
    }

    public function canDeleteEvent($event)
    {
        if ($this->isSuperAdmin()) {
            return true;
        }
        return $event->created_by === $this->id;
    }

    public function canManageUsers()
    {
        return $this->isSuperAdmin();
    }

    /**
     * Override authenticate method for password verification
     */
    public function getAuthPassword()
    {
        return $this->password_hash;
    }
}
