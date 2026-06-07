# Event Management System - DigitalOcean Deployment Guide

## Overview

This guide walks you through deploying the Event Management System on DigitalOcean using:
- **App Platform** (for Laravel application)
- **Managed Database** (MySQL)
- **Spaces** (for file storage - images, exports)
- **Load Balancer** (for scaling)

---

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│              DigitalOcean Infrastructure                    │
├─────────────────────────────────────────────────────────────┤
│                                                              │
│  ┌──────────────────────────────────────────────────────┐   │
│  │    DigitalOcean Load Balancer (Optional)             │   │
│  │    - Handle traffic distribution                     │   │
│  │    - SSL/TLS termination                             │   │
│  └──────────────────┬───────────────────────────────────┘   │
│                     │                                        │
│  ┌──────────────────▼───────────────────────────────────┐   │
│  │  DigitalOcean App Platform (Laravel App)             │   │
│  │  - PHP 8.2 Runtime                                   │   │
│  │  - Automatic scaling                                 │   │
│  │  - Built-in monitoring                               │   │
│  │  - Environment variables management                  │   │
│  └────────────┬──────────────────────────────────────────┘   │
│               │                                               │
│  ┌────────────▼──────────────────────────────────────────┐   │
│  │  DigitalOcean Managed MySQL Database                  │   │
│  │  - Automatic backups                                 │   │
│  │  - Replication (HA available)                        │   │
│  │  - Automated failover                                │   │
│  │  - VPC networking                                    │   │
│  └────────────────────────────────────────────────────────┘   │
│               ▲                                               │
│  ┌────────────┴──────────────────────────────────────────┐   │
│  │  DigitalOcean Spaces (File Storage)                  │   │
│  │  - Event banners & icons                             │   │
│  │  - User avatars                                      │   │
│  │  - Generated reports (CSV/Excel)                     │   │
│  │  - CDN integration available                         │   │
│  └────────────────────────────────────────────────────────┘   │
│               ▲                                               │
│  ┌────────────┴──────────────────────────────────────────┐   │
│  │  DigitalOcean Container Registry (Optional)          │   │
│  │  - Store Docker images                               │   │
│  │  - Private repository                                │   │
│  └────────────────────────────────────────────────────────┘   │
│                                                              │
└─────────────────────────────────────────────────────────────┘

External Clients:
├─ Mobile Apps (Android/iOS)
├─ Web Admin Panel
└─ Website Integration
```

---

## Step 1: DigitalOcean Account Setup

### 1.1 Create DigitalOcean Account
1. Go to https://www.digitalocean.com/
2. Sign up and verify email
3. Add billing information
4. Create a new project (e.g., "Event Management System")

### 1.2 Generate API Token
```
Settings → API → Tokens/Keys → Generate New Token
- Name: "Event Management API"
- Scope: Full Access
- Copy the token (keep it secure)
```

### 1.3 Create SSH Key (for server access)
```bash
# On your local machine
ssh-keygen -t rsa -b 4096 -f ~/.ssh/digitalocean_key

# Add to DigitalOcean
Settings → Security → SSH Keys → Add SSH Key
# Paste contents of ~/.ssh/digitalocean_key.pub
```

---

## Step 2: Create Managed MySQL Database

### 2.1 Create Database Cluster
```
Databases → Create Database → MySQL
```

**Configuration:**
- **Engine**: MySQL 8.0
- **Region**: Choose closest to your users (e.g., SFO3, NYC3, SGP1)
- **Cluster Size**: Start with "Basic" (1 node)
  - Development: 1GB RAM, $15/month
  - Production: High Memory (HA cluster recommended)
- **Name**: `event-management-db`
- **Database**: `event_management`

### 2.2 After Cluster Creation
```
Connection Details (found in cluster):
- Host: db-mysql-xxx.ondigitalocean.com
- Port: 25060
- Database: event_management
- User: doadmin
- Password: (auto-generated, save it!)
```

### 2.3 Create Admin User (Optional)
```sql
-- Connect to database
mysql -h db-mysql-xxx.ondigitalocean.com -u doadmin -p -D event_management

-- Create admin user for app
CREATE USER 'laravel_user'@'%' IDENTIFIED BY 'strong_password_here';
GRANT ALL PRIVILEGES ON event_management.* TO 'laravel_user'@'%';
FLUSH PRIVILEGES;
```

### 2.4 Whitelist App Platform
In your database cluster settings:
```
Trusted Sources → Add App Platform App
(You'll do this after creating the app)
```

---

## Step 3: Create DigitalOcean Spaces (File Storage)

### 3.1 Create Space
```
Spaces → Create Space
```

**Configuration:**
- **Name**: `event-management-files`
- **Region**: Same as database region
- **CORS**: Enable (for app access)

### 3.2 Generate Access Keys
```
API → Spaces Keys → Generate New Key
- Name: "Event Management App"
- Save Access Key & Secret Key
```

### 3.3 Configure CORS
In Space settings:
```json
[
  {
    "AllowedHeaders": ["*"],
    "AllowedMethods": ["GET", "PUT", "POST", "DELETE"],
    "AllowedOrigins": [
      "https://yourdomain.com",
      "https://app.yourdomain.com",
      "https://admin.yourdomain.com"
    ],
    "ExposeHeaders": ["ETag"],
    "MaxAgeSeconds": 3600
  }
]
```

---

## Step 4: Prepare Laravel Application

### 4.1 Create New Laravel Project
```bash
# Locally first
composer create-project laravel/laravel event-management-system
cd event-management-system

# Or use existing project
git clone your-repo-url event-management-system
cd event-management-system
```

### 4.2 Install Dependencies
```bash
composer install

# Install DO SDK and S3 support
composer require league/flysystem-aws-s3-v3 aws/aws-sdk-php
```

### 4.3 Create Laravel Application Structure
Create these directories:
```bash
mkdir -p app/Http/Controllers/Api/v1
mkdir -p app/Models
mkdir -p database/migrations
mkdir -p routes/api
mkdir -p app/Services
mkdir -p storage/logs
```

### 4.4 Setup Environment File
Create `.env` file:
```env
APP_NAME="Event Management System"
APP_ENV=production
APP_KEY=base64:GENERATE_WITH_php_artisan_key:generate
APP_DEBUG=false
APP_URL=https://api.yourdomain.com

# Database (DigitalOcean Managed MySQL)
DB_CONNECTION=mysql
DB_HOST=db-mysql-xxx.ondigitalocean.com
DB_PORT=25060
DB_DATABASE=event_management
DB_USERNAME=laravel_user
DB_PASSWORD=strong_password_here

# DigitalOcean Spaces (S3 compatible)
FILESYSTEM_DISK=spaces
AWS_ACCESS_KEY_ID=your_spaces_access_key
AWS_SECRET_ACCESS_KEY=your_spaces_secret_key
AWS_DEFAULT_REGION=sfo3
AWS_BUCKET=event-management-files
AWS_ENDPOINT=https://sfo3.digitaloceanspaces.com
AWS_USE_PATH_STYLE_ENDPOINTS=true

# Cache & Sessions
CACHE_DRIVER=redis
SESSION_DRIVER=redis

# Queue
QUEUE_CONNECTION=redis

# Mail (SendGrid or similar)
MAIL_MAILER=sendgrid
MAIL_HOST=smtp.sendgrid.net
MAIL_PORT=587
MAIL_USERNAME=apikey
MAIL_PASSWORD=SG.your_sendgrid_api_key
MAIL_FROM_ADDRESS=noreply@yourdomain.com
MAIL_FROM_NAME="Event Management"

# Redis (if using caching)
REDIS_HOST=127.0.0.1
REDIS_PASSWORD=null
REDIS_PORT=6379

# JWT Authentication
JWT_SECRET=your_jwt_secret_here
JWT_ALGORITHM=HS256

# Admin Settings
ADMIN_EMAIL=admin@yourdomain.com
ADMIN_PASSWORD=initial_admin_password
```

### 4.5 Setup Filesystem Configuration
Update `config/filesystems.php`:

```php
'disks' => [
    // ... other disks ...
    
    'spaces' => [
        'driver' => 's3',
        'key' => env('AWS_ACCESS_KEY_ID'),
        'secret' => env('AWS_SECRET_ACCESS_KEY'),
        'region' => env('AWS_DEFAULT_REGION'),
        'bucket' => env('AWS_BUCKET'),
        'endpoint' => env('AWS_ENDPOINT'),
        'use_path_style_endpoint' => env('AWS_USE_PATH_STYLE_ENDPOINTS', false),
    ],
],
```

### 4.6 Database Migrations
Create migration for events:

```bash
php artisan make:migration create_events_table
php artisan make:migration create_event_rewards_table
php artisan make:migration create_user_event_participation_table
php artisan make:migration create_user_rewards_claimed_table
php artisan make:migration create_event_notifications_table
php artisan make:migration create_event_statistics_table
php artisan make:migration create_admin_users_table
```

See database schema in `EVENT_MANAGEMENT_SYSTEM.md` for migration contents.

### 4.7 Create Models
```bash
php artisan make:model Event
php artisan make:model EventReward
php artisan make:model UserEventParticipation
php artisan make:model UserRewardsClaimed
php artisan make:model EventNotification
php artisan make:model EventStatistic
php artisan make:model AdminUser
```

### 4.8 Setup Authentication
```bash
composer require tymondesigns/jwt-auth
php artisan jwt:secret
```

### 4.9 Commit to Git
```bash
git add .
git commit -m "Initial Laravel setup for Event Management System"
git push origin main
```

---

## Step 5: Deploy to DigitalOcean App Platform

### 5.1 Connect GitHub Repository
```
Apps → Create App → GitHub
- Select your repository
- Authorize DigitalOcean GitHub integration
```

### 5.2 Configure Build Settings
```
Choose Branch: main
Auto-deploy: Enable
```

### 5.3 Configure Application
```
Edit App Specification (app.yaml):
```

**Example app.yaml:**
```yaml
name: event-management-system
services:
- name: api
  github:
    branch: main
    repo: Info-touchvoice/TUIKit_Android
  build_command: composer install && php artisan migrate --force
  http_port: 80
  source_dir: /
  
  envs:
  - key: APP_ENV
    value: "production"
  - key: APP_DEBUG
    value: "false"
  - key: LOG_CHANNEL
    value: "stack"
  - key: APP_KEY
    scope: RUN_TIME
    value: ${APP_KEY}
  - key: DB_HOST
    value: db-mysql-xxx.ondigitalocean.com
  - key: DB_PORT
    value: "25060"
  - key: DB_DATABASE
    value: event_management
  - key: DB_USERNAME
    scope: RUN_TIME
    value: ${DB_USERNAME}
  - key: DB_PASSWORD
    scope: RUN_TIME
    value: ${DB_PASSWORD}
  - key: AWS_ACCESS_KEY_ID
    scope: RUN_TIME
    value: ${AWS_ACCESS_KEY_ID}
  - key: AWS_SECRET_ACCESS_KEY
    scope: RUN_TIME
    value: ${AWS_SECRET_ACCESS_KEY}
  - key: AWS_BUCKET
    value: event-management-files
  - key: AWS_ENDPOINT
    value: https://sfo3.digitaloceanspaces.com
  - key: AWS_REGION
    value: sfo3
  
  health_check:
    http_path: /api/health
    http_port: 80
  
  http_routes:
  - path: /
    preserve_path_prefix: true
  
  instance_count: 2
  instance_size_slug: basic-xs
  
  log_destinations:
  - name: default
    source: APP_LOG
```

### 5.4 Add Environment Secrets
In App Platform → Settings → Environment Variables:

```
APP_KEY = (generate: php artisan key:generate --show)
DB_USERNAME = laravel_user
DB_PASSWORD = (from database)
AWS_ACCESS_KEY_ID = (from Spaces)
AWS_SECRET_ACCESS_KEY = (from Spaces)
JWT_SECRET = (generate: openssl rand -hex 32)
ADMIN_EMAIL = admin@yourdomain.com
ADMIN_PASSWORD = (strong password)
```

### 5.5 Add Database Component
```
Apps → Components → Create Database Component
- Type: MySQL
- Engine: 8.0
- Select your managed database cluster
```

### 5.6 Add Storage Component (Optional)
```
Apps → Components → Create Storage Component
- Type: DigitalOcean Spaces
- Select your Spaces bucket
```

### 5.7 Deploy
```
Click "Deploy" button
- App will build and deploy automatically
- Monitor deployment logs
- Wait for health checks to pass
```

---

## Step 6: Setup Domain & SSL

### 6.1 Add Domain
```
Apps → Settings → Domains
- Add custom domain: api.yourdomain.com
- Add www domain: admin.yourdomain.com (for admin panel)
```

### 6.2 Configure DNS
```
At your domain registrar (GoDaddy, Namecheap, etc.):
Add CNAME record:
- Name: api
- Value: (from DigitalOcean)
- TTL: 3600

For admin panel:
- Name: admin
- Value: (from DigitalOcean)
```

### 6.3 SSL Certificate
```
DigitalOcean automatically provides Let's Encrypt SSL
- Apps → Domains → Auto-renew enabled
- Certificate auto-renews every 90 days
```

---

## Step 7: Database Migration & Seeding

### 7.1 Create Seeder for Admin User
```bash
php artisan make:seeder AdminUserSeeder
```

**database/seeders/AdminUserSeeder.php:**
```php
<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;
use App\Models\AdminUser;
use Illuminate\Support\Facades\Hash;

class AdminUserSeeder extends Seeder
{
    public function run()
    {
        AdminUser::create([
            'username' => 'admin',
            'email' => env('ADMIN_EMAIL'),
            'password_hash' => Hash::make(env('ADMIN_PASSWORD')),
            'role' => 'super_admin',
            'is_active' => true,
        ]);
    }
}
```

### 7.2 Run Migrations & Seeding
```bash
# Locally first (for testing)
php artisan migrate:fresh --seed

# On DigitalOcean (via build command)
# Already set in app.yaml: php artisan migrate --force
```

### 7.3 Verify Database
```bash
# SSH into your DO droplet
ssh -i ~/.ssh/digitalocean_key root@your-app-ip

# Connect to database
mysql -h db-mysql-xxx.ondigitalocean.com -u laravel_user -p event_management

# Check tables
SHOW TABLES;
SELECT * FROM admin_users;
```

---

## Step 8: Setup Monitoring & Backups

### 8.1 Database Backups
```
Databases → Backups → Auto-backup enabled
- Frequency: Daily
- Retention: 7 days (upgradeable to 30)
```

### 8.2 App Platform Monitoring
```
Apps → Insights
- CPU usage
- Memory usage
- Requests/second
- Error rates
```

### 8.3 Alert Policy (Optional)
```
Monitoring → Alert Policies → Create Policy
- Alert on high CPU (>80%)
- Alert on high memory (>85%)
- Alert on errors (>5% error rate)
- Notification: Email/Slack
```

### 8.4 Droplet Monitoring (if using VMs)
```
Monitoring → Enable monitoring on droplets
- View system metrics
- Set up alerts
```

---

## Step 9: Setup CDN for Spaces (Optional)

### 9.1 Enable CDN on Spaces
```
Spaces → Settings → CDN
- Enable CDN
- Custom subdomain: cdn.yourdomain.com
```

### 9.2 Update Environment
```env
SPACES_CDN_URL=https://cdn.yourdomain.com
```

### 9.3 Update Storage URL Helper
```php
// In your service or controller
$imageUrl = Storage::disk('spaces')->url('event-banner.jpg');
// Returns: https://cdn.yourdomain.com/event-management-files/event-banner.jpg
```

---

## Step 10: Setup Redis Cache (Optional but Recommended)

### 10.1 Create Redis Cluster
```
Databases → Create Database → Redis
- Region: Same as MySQL
- Size: 1GB ($15/month)
- Enable encryption in transit
```

### 10.2 Update .env
```env
REDIS_HOST=redis-xxx.ondigitalocean.com
REDIS_PORT=25061
REDIS_PASSWORD=redis_password
CACHE_DRIVER=redis
SESSION_DRIVER=redis
QUEUE_CONNECTION=redis
```

### 10.3 Configure in App
```
Apps → Components → Connect Redis Database
```

---

## Step 11: Email Service Setup

### 11.1 Using SendGrid (Recommended)
```
1. Sign up: https://sendgrid.com
2. Create API key
3. Add to .env:
   MAIL_MAILER=sendgrid
   SENDGRID_API_KEY=SG.xxx
```

### 11.2 Using Mailgun
```
1. Sign up: https://www.mailgun.com
2. Get API credentials
3. Add to .env:
   MAIL_MAILER=mailgun
   MAILGUN_DOMAIN=mg.yourdomain.com
   MAILGUN_SECRET=xxx
```

---

## Step 12: API Testing

### 12.1 Test Health Endpoint
```bash
curl https://api.yourdomain.com/api/health
# Should return: {"status": "ok"}
```

### 12.2 Test Admin Login
```bash
curl -X POST https://api.yourdomain.com/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@yourdomain.com",
    "password": "your_password"
  }'
```

### 12.3 Test Event Creation
```bash
curl -X POST https://api.yourdomain.com/api/v1/events \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "event_type": "seasonal",
    "name": "Test Event",
    "start_date": "2024-06-01T00:00:00Z",
    "end_date": "2024-08-31T23:59:59Z"
  }'
```

---

## Cost Estimation (Monthly)

| Service | Size | Cost |
|---------|------|------|
| App Platform | 2x Basic-XS | $12 |
| MySQL Database | 1GB (Basic) | $15 |
| Spaces (Storage) | 250GB | $5 |
| Spaces (Bandwidth) | 1TB | $10 |
| Redis (Optional) | 1GB | $15 |
| Load Balancer (Optional) | Standard | $12 |
| **Total (Basic Setup)** | | **$42+** |
| **Total (Production)** | | **$100+** |

---

## Performance Tuning

### 12.1 Database Optimization
```sql
-- Add indexes for frequently queried fields
CREATE INDEX idx_user_id ON user_event_participation(user_id);
CREATE INDEX idx_event_id ON user_event_participation(event_id);
CREATE INDEX idx_status ON user_event_participation(participation_status);

-- Analyze tables
ANALYZE TABLE events;
ANALYZE TABLE user_event_participation;
```

### 12.2 Laravel Optimization
```bash
# Cache configuration
php artisan config:cache

# Cache routes
php artisan route:cache

# Cache views
php artisan view:cache

# Optimize autoloader
composer dumpautoload --optimize
```

### 12.3 App Scaling
```
Apps → Settings → Instance Configuration
- Increase instance count for load distribution
- Choose appropriate instance size based on load
- Enable autoscaling (if available)
```

---

## Monitoring & Logs

### 13.1 View Logs
```bash
# In App Platform
Apps → Logs → Select service
# Real-time log streaming

# Or SSH into app
ssh -i ~/.ssh/digitalocean_key root@your-app-ip
tail -f storage/logs/laravel.log
```

### 13.2 Database Logs
```
Databases → Logs
- Slow queries
- Errors
- General log
```

### 13.3 Setup Error Tracking (Optional)
```
Install Sentry integration:
composer require sentry/sentry-laravel

# Add to .env
SENTRY_LARAVEL_DSN=https://xxx@xxx.ingest.sentry.io/xxx
```

---

## Security Hardening

### 14.1 Firewall Rules
```
Networking → Firewalls → Create Firewall
- Inbound Rules:
  - HTTP: Port 80 (Allow all)
  - HTTPS: Port 443 (Allow all)
  - SSH: Port 22 (Your IP only)
- Outbound Rules: Allow all
```

### 14.2 Database Security
```
Databases → Trusted Sources
- Add App Platform app
- Restrict access to specific IPs
```

### 14.3 API Rate Limiting
```php
// In routes/api.php
Route::middleware('throttle:60,1')->group(function () {
    Route::post('/auth/login', [AuthController::class, 'login']);
});

Route::middleware('throttle:100,1')->group(function () {
    // Protected routes
});
```

### 14.4 CORS Configuration
```php
// config/cors.php
'allowed_origins' => [
    'https://yourdomain.com',
    'https://app.yourdomain.com',
    'https://admin.yourdomain.com',
],
```

---

## Troubleshooting

### Issue: Database Connection Fails
```
Solution:
1. Check .env variables (DB_HOST, DB_PORT, credentials)
2. Verify database cluster trusted sources
3. Check firewall rules
4. Test connection: mysql -h db-host -u user -p -D database
```

### Issue: Spaces Files Not Uploading
```
Solution:
1. Verify AWS credentials (Access Key, Secret Key)
2. Check bucket name and region
3. Verify CORS settings on Spaces
4. Check file permissions in Laravel
```

### Issue: High Memory Usage
```
Solution:
1. Enable query caching (config/database.php)
2. Reduce instance memory by optimizing code
3. Use Redis for sessions/cache
4. Increase instance size
```

### Issue: Slow API Responses
```
Solution:
1. Check database indexes
2. Use Laravel debugbar (dev only)
3. Enable Redis caching
4. Optimize N+1 queries with eager loading
5. Scale horizontally (add more instances)
```

---

## Deployment Checklist

- [ ] DigitalOcean account created with billing
- [ ] Managed MySQL database created & configured
- [ ] Spaces bucket created & configured
- [ ] SSH keys generated & added to DO
- [ ] Laravel project prepared with migrations
- [ ] .env file configured with DO credentials
- [ ] GitHub repository connected to App Platform
- [ ] app.yaml configured correctly
- [ ] Environment variables added to App Platform
- [ ] Application deployed successfully
- [ ] Domain configured & SSL certificate active
- [ ] Database migrations completed
- [ ] Admin user seeded & tested
- [ ] API endpoints tested
- [ ] Monitoring enabled
- [ ] Backup strategy configured
- [ ] Firewall rules configured
- [ ] Error tracking setup (optional)
- [ ] CDN enabled on Spaces (optional)
- [ ] Load testing completed
- [ ] Security audit passed

---

## Additional Resources

- **DigitalOcean Docs**: https://docs.digitalocean.com
- **App Platform Docs**: https://docs.digitalocean.com/products/app-platform/
- **MySQL Database Docs**: https://docs.digitalocean.com/products/databases/mysql/
- **Spaces Docs**: https://docs.digitalocean.com/products/spaces/
- **Laravel Docs**: https://laravel.com/docs
- **JWT Auth**: https://laravel-jwt-auth.readthedocs.io/

---

## Support & Maintenance

### Monthly Maintenance Tasks
- [ ] Review database backups
- [ ] Check application logs for errors
- [ ] Update composer packages: `composer update`
- [ ] Review performance metrics
- [ ] Check disk space usage on Spaces
- [ ] Verify SSL certificate renewal
- [ ] Test disaster recovery procedures

### Security Updates
- [ ] Keep Laravel framework updated
- [ ] Update PHP version quarterly
- [ ] Review and update database passwords
- [ ] Audit API access logs
- [ ] Review CORS and firewall rules

