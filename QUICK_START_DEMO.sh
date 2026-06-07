#!/bin/bash

# Event Management System - Quick Start Demo Guide
# This script demonstrates how to run the complete system locally and on DigitalOcean

echo "======================================"
echo "Event Management System - Demo Guide"
echo "======================================"
echo ""

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# ============================================
# PART 1: LOCAL SETUP
# ============================================
echo -e "${BLUE}PART 1: LOCAL SETUP${NC}"
echo ""

echo -e "${YELLOW}Step 1: Clone Repository${NC}"
echo "git clone https://github.com/Info-touchvoice/TUIKit_Android.git"
echo "cd TUIKit_Android/backend/laravel"
echo ""

echo -e "${YELLOW}Step 2: Install Dependencies${NC}"
echo "composer install"
echo ""

echo -e "${YELLOW}Step 3: Setup Environment${NC}"
echo "cp .env.example .env"
echo "php artisan key:generate"
echo ""
echo "# Edit .env file with:"
echo "DB_HOST=localhost"
echo "DB_DATABASE=event_management"
echo "DB_USERNAME=root"
echo "DB_PASSWORD=your_password"
echo ""

echo -e "${YELLOW}Step 4: Create Database${NC}"
echo "# Using MySQL CLI:"
echo "mysql -u root -p"
echo "CREATE DATABASE event_management;"
echo "EXIT;"
echo ""

echo -e "${YELLOW}Step 5: Run Migrations${NC}"
echo "php artisan migrate:fresh --seed"
echo ""

echo -e "${YELLOW}Step 6: Generate JWT Secret${NC}"
echo "php artisan jwt:secret"
echo ""

echo -e "${YELLOW}Step 7: Start Development Server${NC}"
echo "php artisan serve"
echo "# Server running at: http://localhost:8000"
echo ""

echo "======================================"
echo ""

# ============================================
# PART 2: API TESTING
# ============================================
echo -e "${BLUE}PART 2: API TESTING (LOCAL)${NC}"
echo ""

echo -e "${YELLOW}Test 1: Admin Login${NC}"
echo "curl -X POST http://localhost:8000/api/v1/auth/login \\"
echo "  -H 'Content-Type: application/json' \\"
echo "  -d '{"
echo "    \"email\": \"admin@example.com\","
echo "    \"password\": \"password123\""
echo "  }'"
echo ""
echo "# Response:"
echo "{\"success\": true, \"data\": {\"token\": \"eyJhbGci...\", ...}}"
echo ""

echo -e "${YELLOW}Test 2: Create Event${NC}"
echo "curl -X POST http://localhost:8000/api/v1/events \\"
echo "  -H 'Authorization: Bearer YOUR_TOKEN' \\"
echo "  -H 'Content-Type: application/json' \\"
echo "  -d '{"
echo "    \"event_type\": \"seasonal\","
echo "    \"name\": \"Summer Festival 2024\","
echo "    \"description\": \"Celebrate summer with amazing rewards\","
echo "    \"start_date\": \"2024-06-01T00:00:00Z\","
echo "    \"end_date\": \"2024-08-31T23:59:59Z\","
echo "    \"is_active\": true"
echo "  }'"
echo ""

echo -e "${YELLOW}Test 3: Get All Events${NC}"
echo "curl -X GET http://localhost:8000/api/v1/events \\"
echo "  -H 'Authorization: Bearer YOUR_TOKEN' \\"
echo "  -H 'Content-Type: application/json'"
echo ""

echo -e "${YELLOW}Test 4: Create Event Reward${NC}"
echo "curl -X POST http://localhost:8000/api/v1/events/1/rewards \\"
echo "  -H 'Authorization: Bearer YOUR_TOKEN' \\"
echo "  -H 'Content-Type: application/json' \\"
echo "  -d '{"
echo "    \"reward_tier\": 1,"
echo "    \"reward_type\": \"diamonds\","
echo "    \"reward_amount\": 100,"
echo "    \"reward_name\": \"Diamond Starter Pack\","
echo "    \"trigger_type\": \"recharge_amount\","
echo "    \"trigger_condition\": 1000"
echo "  }'"
echo ""

echo "======================================"
echo ""

# ============================================
# PART 3: DIGITALOCEAN DEPLOYMENT
# ============================================
echo -e "${BLUE}PART 3: DIGITALOCEAN DEPLOYMENT${NC}"
echo ""

echo -e "${YELLOW}Step 1: Push to GitHub${NC}"
echo "git add ."
echo "git commit -m 'Add Event Management System'"
echo "git push origin main"
echo ""

echo -e "${YELLOW}Step 2: Create DigitalOcean Resources${NC}"
echo ""
echo "2a. Create Managed MySQL Database:"
echo "    - Go to: https://cloud.digitalocean.com/databases"
echo "    - Click 'Create' → Select MySQL 8.0"
echo "    - Region: SFO3 (or closest to you)"
echo "    - Size: 1GB Basic ($15/month)"
echo "    - Save connection details"
echo ""

echo "2b. Create DigitalOcean Spaces:"
echo "    - Go to: https://cloud.digitalocean.com/spaces"
echo "    - Create Space: 'event-management-files'"
echo "    - Region: SFO3"
echo "    - Generate Access Keys"
echo ""

echo -e "${YELLOW}Step 3: Create App on DigitalOcean App Platform${NC}"
echo "    - Go to: https://cloud.digitalocean.com/apps"
echo "    - Click 'Create App'"
echo "    - Connect GitHub → Select your repository"
echo "    - Configure app.yaml (see DIGITALOCEAN_DEPLOYMENT_GUIDE.md)"
echo "    - Add environment variables:"
echo "      * DB_HOST=db-mysql-xxx.ondigitalocean.com"
echo "      * DB_PORT=25060"
echo "      * DB_DATABASE=event_management"
echo "      * DB_USERNAME=laravel_user"
echo "      * DB_PASSWORD=xxxx"
echo "      * AWS_ACCESS_KEY_ID=xxxx"
echo "      * AWS_SECRET_ACCESS_KEY=xxxx"
echo "      * AWS_BUCKET=event-management-files"
echo "      * AWS_ENDPOINT=https://sfo3.digitaloceanspaces.com"
echo ""

echo -e "${YELLOW}Step 4: Configure Domain${NC}"
echo "    - Apps → Settings → Domains"
echo "    - Add domain: api.yourdomain.com"
echo "    - Update DNS at your registrar (CNAME record)"
echo "    - SSL certificate auto-generated by Let's Encrypt"
echo ""

echo -e "${YELLOW}Step 5: Deploy${NC}"
echo "    - Click 'Deploy' button"
echo "    - Monitor build and deployment logs"
echo "    - Wait for health checks to pass"
echo ""

echo "======================================"
echo ""

# ============================================
# PART 4: VERIFY DEPLOYMENT
# ============================================
echo -e "${BLUE}PART 4: VERIFY DEPLOYMENT${NC}"
echo ""

echo -e "${YELLOW}Test Health Endpoint:${NC}"
echo "curl https://api.yourdomain.com/api/health"
echo ""

echo -e "${YELLOW}Test Admin Login (Production):${NC}"
echo "curl -X POST https://api.yourdomain.com/api/v1/auth/login \\"
echo "  -H 'Content-Type: application/json' \\"
echo "  -d '{"
echo "    \"email\": \"admin@yourdomain.com\","
echo "    \"password\": \"your_admin_password\""
echo "  }'"
echo ""

echo "======================================"
echo ""

# ============================================
# PART 5: ADMIN PANEL & MOBILE INTEGRATION
# ============================================
echo -e "${BLUE}PART 5: ADMIN PANEL & MOBILE INTEGRATION${NC}"
echo ""

echo -e "${YELLOW}Admin Panel (Web Dashboard)${NC}"
echo "Framework: React/Vue.js + TypeScript"
echo "Features:"
echo "  - Create/Edit/Delete events"
echo "  - Configure rewards"
echo "  - View participation stats"
echo "  - Export reports (CSV/Excel)"
echo "  - Real-time monitoring"
echo ""
echo "Coming soon: Admin panel code in /frontend/admin-panel/"
echo ""

echo -e "${YELLOW}Mobile Integration (Android/Kotlin)${NC}"
echo "Location: /android/event_management/"
echo "Features:"
echo "  - Display active events"
echo "  - Show user progress"
echo "  - Claim rewards"
echo "  - Push notifications"
echo ""
echo "Coming soon: Android integration code"
echo ""

echo "======================================"
echo ""

# ============================================
# PART 6: MONITORING & LOGS
# ============================================
echo -e "${BLUE}PART 6: MONITORING & LOGS${NC}"
echo ""

echo -e "${YELLOW}View Application Logs:${NC}"
echo "# Local Development"
echo "tail -f storage/logs/laravel.log"
echo ""
echo "# DigitalOcean App Platform"
echo "Apps → Select your app → Logs → View live logs"
echo ""

echo -e "${YELLOW}Monitor Database${NC}"
echo "Databases → Your MySQL cluster → Metrics"
echo "Monitor:"
echo "  - CPU usage"
echo "  - Memory usage"
echo "  - Storage"
echo "  - Connections"
echo ""

echo -e "${YELLOW}Setup Alerts${NC}"
echo "Monitoring → Alert Policies"
echo "Create alerts for:"
echo "  - High CPU (>80%)"
echo "  - High Memory (>85%)"
echo "  - High Error Rate (>5%)"
echo ""

echo "======================================"
echo ""

# ============================================
# PART 7: USEFUL COMMANDS
# ============================================
echo -e "${BLUE}PART 7: USEFUL COMMANDS${NC}"
echo ""

echo -e "${YELLOW}Laravel Commands:${NC}"
echo "php artisan tinker                          # Interactive shell"
echo "php artisan db:seed --class=AdminUserSeeder # Seed admin user"
echo "php artisan migrate:rollback               # Rollback migrations"
echo "php artisan route:list                     # Show all routes"
echo "php artisan config:cache                   # Cache config"
echo "php artisan cache:clear                    # Clear cache"
echo ""

echo -e "${YELLOW}Database Commands:${NC}"
echo "mysql -h db-host -u user -p -D database    # Connect to database"
echo "SHOW TABLES;                                # List tables"
echo "SELECT * FROM events;                      # View events"
echo "SELECT * FROM admin_users;                 # View admins"
echo ""

echo -e "${YELLOW}DigitalOcean CLI Commands:${NC}"
echo "doctl auth init                            # Setup DO CLI"
echo "doctl apps list                            # List apps"
echo "doctl databases list                       # List databases"
echo "doctl compute ssh <droplet-id>             # SSH to droplet"
echo ""

echo "======================================"
echo ""

# ============================================
# PART 8: TROUBLESHOOTING
# ============================================
echo -e "${BLUE}PART 8: TROUBLESHOOTING${NC}"
echo ""

echo -e "${YELLOW}Database Connection Error:${NC}"
echo "1. Verify DB_HOST, DB_PORT in .env"
echo "2. Check database is running: php artisan tinker → DB::connection()->getPdo()"
echo "3. For DO: Verify trusted sources in database cluster"
echo "4. Check firewall rules"
echo ""

echo -e "${YELLOW}JWT Authentication Error:${NC}"
echo "1. Generate JWT secret: php artisan jwt:secret"
echo "2. Check JWT_SECRET in .env"
echo "3. Verify token format: 'Authorization: Bearer TOKEN'"
echo ""

echo -e "${YELLOW}Slow API Response:${NC}"
echo "1. Enable query logging: Log::enableQueryLog()"
echo "2. Add database indexes"
echo "3. Use Redis caching"
echo "4. Scale horizontally on DO App Platform"
echo ""

echo "======================================"
echo ""

echo -e "${GREEN}✓ Demo setup complete!${NC}"
echo ""
echo "Next Steps:"
echo "1. Follow Local Setup steps above"
echo "2. Test API endpoints"
echo "3. Deploy to DigitalOcean"
echo "4. Build admin panel"
echo "5. Integrate with Android app"
echo ""
echo "Documentation:"
echo "- API Spec: EVENT_MANAGEMENT_SYSTEM.md"
echo "- Deployment: DIGITALOCEAN_DEPLOYMENT_GUIDE.md"
echo ""
