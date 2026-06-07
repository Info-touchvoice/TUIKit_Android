# Event Management System - Quick Start Demo

## 🚀 Quick Demo in 5 Minutes

### **Option 1: Run Locally**

```bash
# 1. Clone and navigate
git clone https://github.com/Info-touchvoice/TUIKit_Android.git
cd TUIKit_Android/backend/laravel

# 2. Install & Setup
composer install
cp .env.example .env
php artisan key:generate

# 3. Create database
mysql -u root -p
CREATE DATABASE event_management;
EXIT;

# 4. Run migrations
php artisan migrate:fresh --seed

# 5. Generate JWT secret
php artisan jwt:secret

# 6. Start server
php artisan serve
```

**API will be available at:** `http://localhost:8000/api/v1`

---

### **Option 2: Test with cURL**

#### Login as Admin
```bash
curl -X POST http://localhost:8000/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@example.com",
    "password": "password123"
  }'
```

**Response:**
```json
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
```

#### Create an Event
```bash
curl -X POST http://localhost:8000/api/v1/events \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "event_type": "seasonal",
    "name": "Summer Festival 2024",
    "description": "Celebrate summer with amazing rewards",
    "banner_image_url": "https://example.com/banner.jpg",
    "start_date": "2024-06-01T00:00:00Z",
    "end_date": "2024-08-31T23:59:59Z",
    "is_active": true
  }'
```

#### Get All Events
```bash
curl -X GET "http://localhost:8000/api/v1/events?page=1&per_page=10" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

#### Create Event Reward
```bash
curl -X POST http://localhost:8000/api/v1/events/1/rewards \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -H "Content-Type: application/json" \
  -d '{
    "reward_tier": 1,
    "reward_type": "diamonds",
    "reward_amount": 100,
    "reward_name": "Diamond Starter Pack",
    "reward_description": "Get 100 diamonds when you recharge $10",
    "trigger_type": "recharge_amount",
    "trigger_condition": 1000,
    "max_claims": null
  }'
```

---

### **Option 3: Deploy to DigitalOcean**

#### Prerequisites:
- DigitalOcean Account (https://www.digitalocean.com)
- GitHub Repository Connected

#### Steps:

**1. Create Database**
```
DigitalOcean Console → Databases → Create Database
- Engine: MySQL 8.0
- Size: 1GB Basic ($15/month)
- Region: SFO3
```

**2. Create Spaces Storage**
```
DigitalOcean Console → Spaces → Create Space
- Name: event-management-files
- Region: SFO3
```

**3. Deploy App**
```
DigitalOcean Console → Apps → Create App
- Connect GitHub repository
- Select branch: main
- Configure app.yaml (see DIGITALOCEAN_DEPLOYMENT_GUIDE.md)
```

**4. Add Environment Variables**
```
DB_HOST=db-mysql-xxx.ondigitalocean.com
DB_PORT=25060
DB_DATABASE=event_management
DB_USERNAME=laravel_user
DB_PASSWORD=xxxx
AWS_ACCESS_KEY_ID=xxxx
AWS_SECRET_ACCESS_KEY=xxxx
AWS_BUCKET=event-management-files
AWS_ENDPOINT=https://sfo3.digitaloceanspaces.com
```

**5. Deploy & Configure Domain**
```
Apps → Domains → Add Domain
- api.yourdomain.com
- SSL auto-configured
```

---

## 📁 Project Structure

```
backend/laravel/
├── app/
│   ├── Models/
│   │   ├── Event.php
│   │   ├── EventReward.php
│   │   ├── UserEventParticipation.php
│   │   ├── UserRewardsClaimed.php
│   │   ├── EventNotification.php
│   │   ├── EventStatistic.php
│   │   └── AdminUser.php
│   └── Http/
│       └── Controllers/
│           └── Api/V1/
│               ├── AuthController.php
│               ├── EventController.php
│               └── EventRewardController.php
├── database/
│   └── migrations/
│       ├── 2024_01_01_000001_create_events_table.php
│       ├── 2024_01_01_000002_create_event_rewards_table.php
│       ├── 2024_01_01_000003_create_user_event_participation_table.php
│       ├── 2024_01_01_000004_create_user_rewards_claimed_table.php
│       ├── 2024_01_01_000005_create_event_notifications_table.php
│       ├── 2024_01_01_000006_create_event_statistics_table.php
│       └── 2024_01_01_000007_create_admin_users_table.php
└── routes/
    └── api.php
```

---

## 🔧 Admin User Credentials (Default)

After seeding:
- **Email:** admin@example.com
- **Password:** password123
- **Role:** super_admin

> ⚠️ Change these credentials in production!

---

## 📊 Supported Event Types

1. **Seasonal** - Holiday and seasonal events
2. **VIP** - Exclusive VIP member events
3. **Recharge** - Events based on user recharge amount
4. **Consumption** - Events based on spending
5. **Ranking** - Events based on user ranking
6. **Family/Guild** - Group/family-based events
7. **Election** - Voting/election events
8. **Login** - Daily login reward events
9. **Cooperative** - Team cooperation events
10. **Custom** - Custom event types

---

## 💰 Virtual Rewards Supported

- **Coins** - In-game currency
- **Diamonds** - Premium currency
- **VIP Days** - VIP membership days
- **Frames** - Avatar frames
- **Badges** - Achievement badges
- **Gifts** - Virtual gifts
- **Custom** - Custom rewards

---

## 📈 Reward Triggers

Events can be triggered by:
- **Recharge Amount** - How much user recharged
- **Spending Amount** - How much user spent
- **Login Streak** - Consecutive login days
- **Ranking Position** - User's ranking
- **Task Completion** - Completed tasks count

---

## 🔐 Security Features

✅ JWT Authentication
✅ Role-based Access Control (RBAC)
✅ SQL Injection Prevention (Prepared Statements)
✅ CORS Configuration
✅ Rate Limiting
✅ Encrypted Passwords
✅ Audit Logging
✅ HTTPS/SSL

---

## 📊 Monitoring & Analytics

The system provides:
- **Real-time Statistics** - Participants, rewards, revenue
- **Export Reports** - CSV/Excel downloads
- **User Analytics** - Participation metrics
- **Financial Reports** - Recharge and spending data
- **Reward Distribution** - Track virtual rewards given

---

## 🆘 Common Issues & Solutions

### Database Connection Failed
```
Error: SQLSTATE[HY000] [2002] Connection refused

Solution:
1. Check MySQL is running
2. Verify DB_HOST, DB_PORT in .env
3. For DigitalOcean: Add app to trusted sources in database cluster
```

### JWT Token Invalid
```
Error: Unauthorized

Solution:
1. Generate new JWT secret: php artisan jwt:secret
2. Check token format: "Authorization: Bearer TOKEN"
3. Verify token hasn't expired
```

### File Upload Fails
```
Error: FileNotFound on Spaces

Solution:
1. Verify AWS credentials (Access Key, Secret Key)
2. Check bucket name and region match
3. Verify CORS settings on Spaces
```

---

## 📚 Full Documentation

- **Database Schema & APIs**: `EVENT_MANAGEMENT_SYSTEM.md`
- **DigitalOcean Deployment**: `DIGITALOCEAN_DEPLOYMENT_GUIDE.md`
- **Quick Start Demo**: `QUICK_START_DEMO.sh`

---

## 🎯 Next Steps

1. ✅ Clone repository
2. ✅ Run locally and test APIs
3. ✅ Deploy to DigitalOcean
4. ⏳ Build Admin Panel UI (React/Vue)
5. ⏳ Integrate with Android app (Kotlin)
6. ⏳ Setup CI/CD pipelines

---

## 📧 Support & Questions

For detailed documentation, see:
- `EVENT_MANAGEMENT_SYSTEM.md` - Complete API reference
- `DIGITALOCEAN_DEPLOYMENT_GUIDE.md` - Deployment guide
- `backend/laravel/` - Source code with comments

---

**Happy Coding! 🚀**
