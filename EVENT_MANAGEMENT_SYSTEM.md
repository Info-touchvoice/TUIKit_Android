# Event Management System - Design Documentation

## System Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                    Admin Web Panel                          │
│              (PHP/Laravel + MySQL)                          │
└────────────────────┬────────────────────────────────────────┘
                     │
        ┌────────────┴────────────┐
        │                         │
        ▼                         ▼
   REST APIs              WebSocket Events
        │                         │
        └────────────┬────────────┘
                     │
     ┌───────────────┼───────────────┐
     ▼               ▼               ▼
Android App      iOS App        Web Frontend
(Kotlin)      (Swift/ObjC)    (React/Vue)
```

---

## Database Schema Design

### 1. Events Table
```sql
CREATE TABLE events (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    event_type ENUM('seasonal', 'vip', 'recharge', 'consumption', 'ranking', 'family', 'election', 'login', 'cooperative', 'custom') NOT NULL,
    name VARCHAR(255) NOT NULL,
    description LONGTEXT,
    banner_image_url VARCHAR(500),
    icon_url VARCHAR(500),
    event_rules LONGTEXT,
    
    start_date DATETIME NOT NULL,
    end_date DATETIME NOT NULL,
    
    is_active BOOLEAN DEFAULT true,
    is_published BOOLEAN DEFAULT false,
    
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by BIGINT,
    
    INDEX idx_event_type (event_type),
    INDEX idx_is_active (is_active),
    INDEX idx_start_date (start_date),
    INDEX idx_end_date (end_date),
    FOREIGN KEY (created_by) REFERENCES admin_users(id)
);
```

### 2. Event Rewards Table
```sql
CREATE TABLE event_rewards (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    event_id BIGINT NOT NULL,
    reward_tier INT NOT NULL DEFAULT 1,
    reward_type ENUM('coins', 'diamonds', 'vip_days', 'frames', 'badges', 'gifts', 'custom') NOT NULL,
    reward_amount INT NOT NULL,
    reward_name VARCHAR(255),
    reward_description TEXT,
    
    -- Trigger conditions
    trigger_type ENUM('recharge_amount', 'spending_amount', 'login_streak', 'ranking_position', 'task_completion') NOT NULL,
    trigger_condition INT NOT NULL,  -- Amount or position or days
    
    max_claims INT DEFAULT NULL,  -- NULL = unlimited
    claims_count INT DEFAULT 0,
    
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_event_id (event_id),
    INDEX idx_reward_type (reward_type),
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE
);
```

### 3. User Event Participation Table
```sql
CREATE TABLE user_event_participation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    
    participation_status ENUM('pending', 'active', 'completed', 'cancelled') DEFAULT 'active',
    
    -- Progress tracking
    current_progress INT DEFAULT 0,
    target_progress INT NOT NULL,
    
    -- Participation data
    recharge_amount DECIMAL(18, 2) DEFAULT 0.00,
    spending_amount DECIMAL(18, 2) DEFAULT 0.00,
    login_streak INT DEFAULT 0,
    ranking_position INT DEFAULT NULL,
    tasks_completed INT DEFAULT 0,
    
    joined_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    completed_at DATETIME DEFAULT NULL,
    
    INDEX idx_user_id (user_id),
    INDEX idx_event_id (event_id),
    INDEX idx_participation_status (participation_status),
    UNIQUE KEY unique_user_event (user_id, event_id)
);
```

### 4. User Rewards Claimed Table
```sql
CREATE TABLE user_rewards_claimed (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    event_reward_id BIGINT NOT NULL,
    
    reward_type VARCHAR(50) NOT NULL,
    reward_amount INT NOT NULL,
    reward_data JSON,  -- Store custom reward details
    
    claimed_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    processed_at DATETIME DEFAULT NULL,
    status ENUM('pending', 'processed', 'failed') DEFAULT 'pending',
    
    INDEX idx_user_id (user_id),
    INDEX idx_event_id (event_id),
    INDEX idx_status (status),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    FOREIGN KEY (event_reward_id) REFERENCES event_rewards(id) ON DELETE CASCADE
);
```

### 5. Event Notifications Table
```sql
CREATE TABLE event_notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    event_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    
    notification_type ENUM('event_started', 'event_ended', 'reward_earned', 'milestone_reached') NOT NULL,
    title VARCHAR(255) NOT NULL,
    message LONGTEXT NOT NULL,
    
    is_read BOOLEAN DEFAULT false,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    read_at DATETIME DEFAULT NULL,
    
    INDEX idx_user_id (user_id),
    INDEX idx_event_id (event_id),
    INDEX idx_is_read (is_read),
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

### 6. Event Statistics Table
```sql
CREATE TABLE event_statistics (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    event_id BIGINT NOT NULL,
    
    total_participants INT DEFAULT 0,
    total_active_participants INT DEFAULT 0,
    total_completed_participants INT DEFAULT 0,
    
    total_recharge_amount DECIMAL(18, 2) DEFAULT 0.00,
    total_spending_amount DECIMAL(18, 2) DEFAULT 0.00,
    total_rewards_distributed DECIMAL(18, 2) DEFAULT 0.00,
    
    total_coins_distributed BIGINT DEFAULT 0,
    total_diamonds_distributed BIGINT DEFAULT 0,
    total_vip_days_distributed INT DEFAULT 0,
    total_frames_distributed INT DEFAULT 0,
    total_badges_distributed INT DEFAULT 0,
    total_gifts_distributed INT DEFAULT 0,
    
    last_updated DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    UNIQUE KEY unique_event_stats (event_id),
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE
);
```

### 7. Admin Users Table
```sql
CREATE TABLE admin_users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    
    role ENUM('super_admin', 'admin', 'moderator') DEFAULT 'admin',
    
    is_active BOOLEAN DEFAULT true,
    last_login DATETIME DEFAULT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_email (email),
    INDEX idx_role (role)
);
```

---

## REST API Specifications

### Authentication Endpoints

#### 1. Admin Login
```
POST /api/v1/auth/login
Content-Type: application/json

Request:
{
    "email": "admin@example.com",
    "password": "password123"
}

Response (200):
{
    "success": true,
    "data": {
        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
        "user": {
            "id": 1,
            "username": "admin",
            "email": "admin@example.com",
            "role": "super_admin"
        }
    }
}

Response (401):
{
    "success": false,
    "message": "Invalid email or password"
}
```

---

### Event Management Endpoints

#### 2. Create Event
```
POST /api/v1/events
Authorization: Bearer {token}
Content-Type: application/json

Request:
{
    "event_type": "seasonal",
    "name": "Summer Festival 2024",
    "description": "Celebrate summer with amazing rewards",
    "banner_image_url": "https://cdn.example.com/banner.jpg",
    "icon_url": "https://cdn.example.com/icon.png",
    "event_rules": "Users must recharge minimum $10 to participate",
    "start_date": "2024-06-01T00:00:00Z",
    "end_date": "2024-08-31T23:59:59Z",
    "is_active": true
}

Response (201):
{
    "success": true,
    "message": "Event created successfully",
    "data": {
        "id": 1,
        "event_type": "seasonal",
        "name": "Summer Festival 2024",
        "created_at": "2024-05-20T10:30:00Z"
    }
}
```

#### 3. Get All Events
```
GET /api/v1/events?page=1&per_page=20&type=seasonal&is_active=true
Authorization: Bearer {token}

Response (200):
{
    "success": true,
    "data": [
        {
            "id": 1,
            "event_type": "seasonal",
            "name": "Summer Festival 2024",
            "description": "Celebrate summer with amazing rewards",
            "banner_image_url": "https://cdn.example.com/banner.jpg",
            "icon_url": "https://cdn.example.com/icon.png",
            "start_date": "2024-06-01T00:00:00Z",
            "end_date": "2024-08-31T23:59:59Z",
            "is_active": true,
            "is_published": true,
            "participants_count": 5234,
            "created_at": "2024-05-20T10:30:00Z"
        }
    ],
    "pagination": {
        "current_page": 1,
        "per_page": 20,
        "total": 45,
        "total_pages": 3
    }
}
```

#### 4. Get Event Details
```
GET /api/v1/events/{event_id}
Authorization: Bearer {token}

Response (200):
{
    "success": true,
    "data": {
        "id": 1,
        "event_type": "seasonal",
        "name": "Summer Festival 2024",
        "description": "Celebrate summer with amazing rewards",
        "banner_image_url": "https://cdn.example.com/banner.jpg",
        "icon_url": "https://cdn.example.com/icon.png",
        "event_rules": "Users must recharge minimum $10 to participate",
        "start_date": "2024-06-01T00:00:00Z",
        "end_date": "2024-08-31T23:59:59Z",
        "is_active": true,
        "is_published": true,
        "rewards": [
            {
                "id": 1,
                "reward_tier": 1,
                "reward_type": "diamonds",
                "reward_amount": 100,
                "reward_name": "Diamond Starter Pack",
                "trigger_type": "recharge_amount",
                "trigger_condition": 1000,
                "max_claims": null
            }
        ],
        "statistics": {
            "total_participants": 5234,
            "total_active_participants": 3120,
            "total_completed_participants": 2114,
            "total_recharge_amount": 156420.50,
            "total_spending_amount": 234567.80,
            "total_rewards_distributed": 345678.90
        }
    }
}
```

#### 5. Update Event
```
PUT /api/v1/events/{event_id}
Authorization: Bearer {token}
Content-Type: application/json

Request:
{
    "name": "Summer Festival 2024 - Extended",
    "description": "Celebrate summer with amazing rewards - Extended!",
    "end_date": "2024-09-30T23:59:59Z",
    "is_active": true
}

Response (200):
{
    "success": true,
    "message": "Event updated successfully",
    "data": {
        "id": 1,
        "name": "Summer Festival 2024 - Extended",
        "updated_at": "2024-05-21T14:20:00Z"
    }
}
```

#### 6. Toggle Event Status
```
PATCH /api/v1/events/{event_id}/status
Authorization: Bearer {token}
Content-Type: application/json

Request:
{
    "is_active": false
}

Response (200):
{
    "success": true,
    "message": "Event status updated",
    "data": {
        "id": 1,
        "is_active": false,
        "updated_at": "2024-05-21T14:20:00Z"
    }
}
```

#### 7. Delete Event
```
DELETE /api/v1/events/{event_id}
Authorization: Bearer {token}

Response (200):
{
    "success": true,
    "message": "Event deleted successfully"
}
```

---

### Reward Management Endpoints

#### 8. Create Event Reward
```
POST /api/v1/events/{event_id}/rewards
Authorization: Bearer {token}
Content-Type: application/json

Request:
{
    "reward_tier": 1,
    "reward_type": "diamonds",
    "reward_amount": 100,
    "reward_name": "Diamond Starter Pack",
    "reward_description": "Get 100 diamonds when you recharge $10",
    "trigger_type": "recharge_amount",
    "trigger_condition": 1000,
    "max_claims": null
}

Response (201):
{
    "success": true,
    "message": "Reward created successfully",
    "data": {
        "id": 1,
        "event_id": 1,
        "reward_tier": 1,
        "reward_type": "diamonds",
        "created_at": "2024-05-20T10:30:00Z"
    }
}
```

#### 9. Update Event Reward
```
PUT /api/v1/events/{event_id}/rewards/{reward_id}
Authorization: Bearer {token}
Content-Type: application/json

Request:
{
    "reward_amount": 150,
    "max_claims": 500
}

Response (200):
{
    "success": true,
    "message": "Reward updated successfully",
    "data": {
        "id": 1,
        "reward_amount": 150,
        "updated_at": "2024-05-21T14:20:00Z"
    }
}
```

#### 10. Delete Event Reward
```
DELETE /api/v1/events/{event_id}/rewards/{reward_id}
Authorization: Bearer {token}

Response (200):
{
    "success": true,
    "message": "Reward deleted successfully"
}
```

#### 11. Get Event Rewards
```
GET /api/v1/events/{event_id}/rewards
Authorization: Bearer {token}

Response (200):
{
    "success": true,
    "data": [
        {
            "id": 1,
            "reward_tier": 1,
            "reward_type": "diamonds",
            "reward_amount": 100,
            "reward_name": "Diamond Starter Pack",
            "trigger_type": "recharge_amount",
            "trigger_condition": 1000,
            "max_claims": null,
            "claims_count": 234
        }
    ]
}
```

---

### Participation & Analytics Endpoints

#### 12. Get Event Participation Stats
```
GET /api/v1/events/{event_id}/participants
Authorization: Bearer {token}
Content-Type: application/json

Request Parameters:
?page=1&per_page=20&status=active&sort_by=ranking_position

Response (200):
{
    "success": true,
    "data": [
        {
            "user_id": 12345,
            "username": "player_123",
            "avatar_url": "https://cdn.example.com/avatar.jpg",
            "participation_status": "active",
            "current_progress": 2500,
            "target_progress": 5000,
            "recharge_amount": 2500.00,
            "spending_amount": 3450.75,
            "ranking_position": 45,
            "rewards_earned": [
                {
                    "reward_id": 1,
                    "reward_type": "diamonds",
                    "reward_amount": 100,
                    "claimed_at": "2024-06-15T10:30:00Z"
                }
            ],
            "joined_at": "2024-06-01T08:15:00Z"
        }
    ],
    "pagination": {
        "current_page": 1,
        "per_page": 20,
        "total": 5234,
        "total_pages": 262
    }
}
```

#### 13. Get Event Statistics
```
GET /api/v1/events/{event_id}/statistics
Authorization: Bearer {token}

Response (200):
{
    "success": true,
    "data": {
        "event_id": 1,
        "event_name": "Summer Festival 2024",
        "total_participants": 5234,
        "total_active_participants": 3120,
        "total_completed_participants": 2114,
        
        "financial_stats": {
            "total_recharge_amount": 156420.50,
            "total_spending_amount": 234567.80,
            "total_rewards_value": 345678.90,
            "average_recharge_per_user": 29.88,
            "average_spending_per_user": 44.83
        },
        
        "reward_distribution": {
            "coins": 5000000,
            "diamonds": 123450,
            "vip_days": 12340,
            "frames": 234,
            "badges": 567,
            "gifts": 890
        },
        
        "participation_metrics": {
            "completion_rate": "40.4%",
            "average_login_streak": 15,
            "peak_participation_date": "2024-06-15"
        },
        
        "last_updated": "2024-06-20T12:30:00Z"
    }
}
```

#### 14. Export Event Report
```
GET /api/v1/events/{event_id}/export
Authorization: Bearer {token}
Content-Type: application/json

Request Parameters:
?format=csv&fields=user_id,username,participation_status,rewards_earned,recharge_amount

Response (200):
File download with CSV/Excel format

Headers:
Content-Disposition: attachment; filename="event_1_report_2024-06-20.csv"
Content-Type: text/csv
```

---

### Mobile/Client Endpoints

#### 15. Get Active Events (Public)
```
GET /api/v1/client/events/active
Content-Type: application/json

Request Parameters:
?user_id=12345&limit=20

Response (200):
{
    "success": true,
    "data": [
        {
            "id": 1,
            "event_type": "seasonal",
            "name": "Summer Festival 2024",
            "description": "Celebrate summer with amazing rewards",
            "banner_image_url": "https://cdn.example.com/banner.jpg",
            "icon_url": "https://cdn.example.com/icon.png",
            "event_rules": "Users must recharge minimum $10 to participate",
            "start_date": "2024-06-01T00:00:00Z",
            "end_date": "2024-08-31T23:59:59Z",
            "days_remaining": 72,
            "user_participation": {
                "is_participating": true,
                "joined_at": "2024-06-01T08:15:00Z",
                "current_progress": 2500,
                "target_progress": 5000,
                "progress_percentage": 50
            },
            "rewards": [
                {
                    "id": 1,
                    "reward_type": "diamonds",
                    "reward_amount": 100,
                    "reward_name": "Diamond Starter Pack",
                    "trigger_type": "recharge_amount",
                    "trigger_condition": 1000,
                    "is_claimed": false
                }
            ]
        }
    ]
}
```

#### 16. Get Upcoming Events (Public)
```
GET /api/v1/client/events/upcoming
Content-Type: application/json

Response (200):
{
    "success": true,
    "data": [
        {
            "id": 2,
            "event_type": "vip",
            "name": "VIP Exclusive Event",
            "description": "Special event for VIP members",
            "banner_image_url": "https://cdn.example.com/banner_vip.jpg",
            "start_date": "2024-07-01T00:00:00Z",
            "end_date": "2024-07-31T23:59:59Z",
            "days_until_start": 11
        }
    ]
}
```

#### 17. Get User Event Rewards (Public)
```
GET /api/v1/client/users/{user_id}/rewards
Content-Type: application/json

Response (200):
{
    "success": true,
    "data": {
        "total_rewards": [
            {
                "event_id": 1,
                "event_name": "Summer Festival 2024",
                "claimed_rewards": [
                    {
                        "reward_type": "diamonds",
                        "reward_amount": 100,
                        "claimed_at": "2024-06-15T10:30:00Z"
                    },
                    {
                        "reward_type": "coins",
                        "reward_amount": 5000,
                        "claimed_at": "2024-06-20T14:22:00Z"
                    }
                ]
            }
        ],
        "summary": {
            "total_coins": 45000,
            "total_diamonds": 350,
            "total_vip_days": 30,
            "total_frames": 5,
            "total_badges": 12
        }
    }
}
```

---

### Notification Endpoints

#### 18. Send Event Notification
```
POST /api/v1/notifications/send
Authorization: Bearer {token}
Content-Type: application/json

Request:
{
    "event_id": 1,
    "notification_type": "event_started",
    "title": "Event Started!",
    "message": "Summer Festival 2024 has started. Join now and earn amazing rewards!",
    "send_to": "all_users"  // or specific_users with user_ids array
}

Response (200):
{
    "success": true,
    "message": "Notification sent successfully",
    "data": {
        "notifications_sent": 5234,
        "sent_at": "2024-06-01T00:00:00Z"
    }
}
```

#### 19. Get User Notifications
```
GET /api/v1/client/notifications
Content-Type: application/json

Request Parameters:
?user_id=12345&unread_only=false&limit=20

Response (200):
{
    "success": true,
    "data": [
        {
            "id": 1,
            "event_id": 1,
            "notification_type": "event_started",
            "title": "Event Started!",
            "message": "Summer Festival 2024 has started...",
            "is_read": false,
            "created_at": "2024-06-01T00:00:00Z"
        }
    ]
}
```

---

## Error Response Formats

### Standard Error Response
```json
{
    "success": false,
    "message": "Error message here",
    "error_code": "INVALID_REQUEST",
    "status_code": 400
}
```

### Validation Error Response
```json
{
    "success": false,
    "message": "Validation failed",
    "error_code": "VALIDATION_ERROR",
    "status_code": 422,
    "errors": {
        "name": ["Name is required"],
        "start_date": ["Start date must be in the future"]
    }
}
```

### Authentication Error Response
```json
{
    "success": false,
    "message": "Unauthorized",
    "error_code": "UNAUTHORIZED",
    "status_code": 401
}
```

---

## Security Considerations

1. **Authentication**: Use JWT tokens with expiration (15 min access, 7 days refresh)
2. **Authorization**: Role-based access control (RBAC)
3. **Rate Limiting**: 100 requests per minute per user
4. **Data Validation**: Server-side validation for all inputs
5. **SQL Injection**: Use prepared statements
6. **CORS**: Restrict to allowed origins
7. **HTTPS**: All API calls must use HTTPS
8. **Audit Logging**: Log all admin actions
9. **Password Policy**: Minimum 12 characters, complexity requirements
10. **Data Encryption**: Encrypt sensitive fields

---

## Performance Optimization

1. **Database Indexing**: Added on frequently queried fields
2. **Pagination**: Limit responses to 20-100 items per page
3. **Caching**: Cache event data for 1 hour
4. **Lazy Loading**: Load statistics only when requested
5. **Database Connection Pooling**: Use connection pools
6. **API Response Compression**: Enable gzip compression

---

## Deployment Checklist

- [ ] Database migrations created
- [ ] Environment variables configured
- [ ] SSL certificates installed
- [ ] Rate limiting configured
- [ ] Backup strategy in place
- [ ] Monitoring and logging set up
- [ ] Admin user accounts created
- [ ] API documentation deployed
- [ ] Load testing completed
- [ ] Security audit passed

