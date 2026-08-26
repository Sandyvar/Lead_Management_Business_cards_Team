# 🚀 SaaS Lead Management Platform — Comprehensive Task Allocation & Project Guide
**Platform:** SaaS-Based Lead Management CRM with Digital Visiting Card & QR Lead Capture  
**Target Delivery Date:** 11 September 2026  
**Architecture:** Multi-Tenant Spring Boot 3 + Java 23 + MySQL + REST APIs  

---

## 📑 Table of Contents
1. [Project Overview & Core Architecture](#1-project-overview--core-architecture)
2. [Zero-Conflict Module-Wise Strategy](#2-zero-conflict-module-wise-strategy)
3. [Developer-Wise & Day-Wise Detailed Task Breakdown](#3-developer-wise--day-wise-detailed-task-breakdown)
   - [Pavan — Database & Schemas](#pavan--database--schemas)
   - [Dheeraj — Authentication, Security & Roles](#dheeraj--authentication-security--roles)
   - [Sandeep — Lead Management & Pipeline Kanban](#sandeep--lead-management--pipeline-kanban)
   - [Vaibhav — Timeline, Notifications & Scoring Automation](#vaibhav--timeline-notifications--scoring-automation)
   - [Chaitanya — Digital Visiting Card & QR Engine](#chaitanya--digital-visiting-card--qr-engine)
   - [Abrar — Customer Management & Follow-ups](#abrar--customer-management--follow-ups)
   - [Govardhan — Subscriptions, Payments & Super Admin](#govardhan--subscriptions-payments--super-admin)
4. [Git Branching & Integration Workflow](#4-git-branching--integration-workflow)
5. [Final Delivery Acceptance Checklist](#5-final-delivery-acceptance-checklist)

---

## 1. Project Overview & Core Architecture

The CRM manages the complete lead lifecycle:
```text
Lead Generation ➔ Lead Capture (QR/Web) ➔ Lead Assignment ➔ Follow-up ➔ Sales Pipeline (Kanban) ➔ Conversion ➔ Customer Management ➔ Analytics
```

### Standard Backend Package Structure:
```text
src/main/java/com/project/leadcrm/
├── config/             # Security, CORS, Swagger, Async, Schedulers
├── controller/         # REST API endpoints (One controller per feature)
├── service/            # Business interfaces & rules
│   └── impl/           # Service implementation classes
├── repository/         # Spring Data JPA interfaces
├── model/              # Database entities
│   └── enums/          # Shared status & category enums
├── dto/                # Request & Response Data Transfer Objects
└── util/               # Helper utilities (QR generator, JWT helper, DateTime)
```

---

## 2. Zero-Conflict Module-Wise Strategy

To eliminate Git merge conflicts and complete the project 3x faster:
1. **Module Ownership:** Each developer fully owns one distinct module and works inside their dedicated sub-package.
2. **Dedicated Branch:** Each developer pushes strictly to their personal branch (`backend/<name>`).
3. **No Direct Main Pushes:** All merges into `main` happen during planned integration milestones.

---

## 3. Developer-Wise & Day-Wise Detailed Task Breakdown

---

### 🗄️ Pavan — Database & Schemas
**Module Goal:** Build and maintain the complete MySQL database architecture, relationships, seed data, and query optimizations.

* **Day 1–3 (24–26 Aug): Core Schema Design & Tables**
  - Design ER diagram for all 10 core tables: `companies`, `users`, `employees`, `leads`, `activity_timeline`, `notifications`, `digital_cards`, `followups`, `customers`, `subscriptions`, `payments`.
  - Write SQL DDL migration scripts with primary keys (`PK`), foreign keys (`FK`), and unique constraints.
* **Day 4–7 (27–30 Aug): Relationships & Lead Pipeline Tables**
  - Implement `lead_sources`, `pipeline_stages`, and `assignments` tables.
  - Create database indexes on high-traffic columns (`lead_id`, `assigned_employee_id`, `created_date`, `status`).
  - Create seed script `data.sql` with realistic sample companies, employees, and leads.
* **Day 8–11 (31 Aug–3 Sep): Follow-up, Cards & Communication Logs Tables**
  - Implement `followups`, `digital_cards`, `card_analytics`, and `communication_logs` tables.
  - Ensure foreign key cascading constraints and soft-delete support where required.
* **Day 12–15 (4–7 Sep): Analytics Views & Subscriptions**
  - Create database views for dashboard analytics (leads by source, conversion ratios, monthly revenue).
  - Implement `subscriptions`, `payments`, and `campaigns` tables.
* **Day 16–18 (8–10 Sep): Query Optimization & Backup**
  - Optimize slow queries using `EXPLAIN ANALYZE`.
  - Validate multi-tenant company data isolation.
  - Prepare final database backup & restore scripts for deployment.

---

### 🔐 Dheeraj — Authentication, Security & Roles
**Module Goal:** Secure all REST endpoints, implement JWT token-based authentication, user registration/login, and Role-Based Access Control (RBAC).

* **Day 1–3 (24–26 Aug): Security Config & Password Hashing**
  - Configure Spring Security with `SecurityFilterChain` and `BCryptPasswordEncoder`.
  - Create `User` entity, `Role` enum (`SUPER_ADMIN`, `BUSINESS_OWNER`, `SALES_MANAGER`, `SALES_EXECUTIVE`).
  - Implement `JwtTokenProvider` to generate and validate HS256/RS256 JWT tokens.
* **Day 4–7 (27–30 Aug): Auth REST APIs**
  - Build `AuthController` with:
    - `POST /api/auth/register` (Register new business owner/user)
    - `POST /api/auth/login` (Returns JWT access token + user profile)
    - `GET /api/auth/me` (Returns currently authenticated user profile)
  - Create `JwtAuthenticationFilter` to validate headers (`Bearer <token>`) on protected routes.
* **Day 8–11 (31 Aug–3 Sep): Role-Based Route Protection**
  - Secure `/api/admin/**` for `SUPER_ADMIN` only.
  - Secure `/api/team/**` for `BUSINESS_OWNER` and `SALES_MANAGER`.
  - Allow public access to digital visiting cards (`/api/cards/public/**`) and lead capture forms.
* **Day 12–15 (4–7 Sep): Security Exception Handling & Refresh Tokens**
  - Implement custom `AuthenticationEntryPoint` and `AccessDeniedHandler` returning clean JSON error responses.
  - Implement token refresh mechanism (`POST /api/auth/refresh-token`).
* **Day 16–18 (8–10 Sep): Security Auditing & QA**
  - Perform authorization penetration tests on all endpoints.
  - End-to-end testing with Swagger UI and Postman.

---

### 📊 Sandeep — Lead Management & Pipeline Kanban
**Module Goal:** Implement the core CRM Lead CRUD operations, sales pipeline stage transitions, manual lead allocation, and search/filtering.

* **Day 1–3 (24–26 Aug): Lead CRUD APIs**
  - Create `LeadController`, `LeadService`, and `LeadRepository`.
  - Implement `POST /api/leads` (Create lead).
  - Implement `GET /api/leads` (List all leads with pagination, search by name/mobile/email).
  - Implement `GET /api/leads/{id}` and `DELETE /api/leads/{id}`.
* **Day 4–7 (27–30 Aug): Sales Pipeline & Kanban Layout**
  - Implement pipeline stages: `NEW` ➔ `CONTACTED` ➔ `INTERESTED` ➔ `FOLLOW_UP` ➔ `PROPOSAL_SENT` ➔ `NEGOTIATION` ➔ `WON` / `LOST`.
  - Implement `PUT /api/leads/{id}/stage` (Move lead to a new pipeline stage).
  - Implement `GET /api/leads/pipeline` (Returns leads grouped by stage for Kanban board view).
* **Day 8–11 (31 Aug–3 Sep): Lead Filtering & Assignment APIs**
  - Implement `POST /api/leads/{id}/assign` (Assign lead to a specific employee).
  - Implement filters: filter by `lead_status`, `priority` (LOW/MEDIUM/HIGH/CRITICAL), `lead_source`, and `assigned_employee`.
* **Day 12–15 (4–7 Sep): Bulk Operations & Data Validation**
  - Implement CSV/Excel bulk lead upload API with format validation.
  - Build pipeline summary metrics (Total active pipeline valuation).
* **Day 16–18 (8–10 Sep): Final Demo & Integration**
  - Connect with Vaibhav's timeline logs and Abrar's Customer conversion module.
  - Perform end-to-end pipeline validation.

---

### ⏱️ Vaibhav — Timeline, Notifications & Scoring Automation
**Module Goal:** Implement chronological activity history, internal operational notes, in-app/email alerts, and the automated lead scoring engine.

* **Day 1–3 (24–26 Aug): Notification Service & Activity Timeline (✅ COMPLETED)**
  - Built `Notification` entity, `NotificationService`, and `NotificationController` (`/api/notifications`).
  - Built `ActivityTimeline` entity, `ActivityTimelineService`, and `ActivityTimelineController` (`/api/leads/{id}/timeline`).
  - Implemented internal note logging (`POST /api/leads/{id}/timeline/note`) and unread notification badge counters.
* **Day 4–7 (27–30 Aug): Capture Workflow Automation & Lead Scoring Engine**
  - Build `LeadCaptureService` and `POST /api/leads/capture` for automated QR & web form intake.
  - Implement Automated Lead Scoring Algorithm:
    - QR Code Scan / Card Visit: **+5 pts**
    - Website Visit: **+5 pts**
    - WhatsApp Click: **+10 pts**
    - Call Action: **+10 pts**
    - Enquiry Form: **+20 pts**
    - Proposal Request: **+30 pts**
    - Meeting / Demo: **+40 pts**
    - Classification: `0–15 pts` = **COLD**, `16–35 pts` = **WARM**, `36+ pts` = **HOT**.
* **Day 8–11 (31 Aug–3 Sep): Overdue Alerts & Background Schedulers**
  - Build `@Scheduled` cron job to check pending follow-ups every morning.
  - Automatically dispatch overdue alert notifications to assigned sales executives.
  - Implement auto-notification triggers when leads are assigned or status is updated.
* **Day 12–15 (4–7 Sep): Conversion Tracking & Export Service**
  - Build Lead-to-Customer conversion analytics (`GET /api/analytics/conversion-rate`).
  - Implement multi-format export service (Export leads & reports to CSV and Excel).
* **Day 16–18 (8–10 Sep): End-to-End Testing & Handover Documentation**
  - Lead comprehensive API integration testing.
  - Write final API handover documentation for team and project manager.

---

### 📇 Chaitanya — Digital Visiting Card & QR Engine
**Module Goal:** Build digital business cards, dynamic QR code generation, public card landing pages, and scan/click tracking analytics.

* **Day 1–3 (24–26 Aug): Digital Card Management**
  - Create `DigitalCard` entity (`profile_name`, `designation`, `company`, `mobile`, `whatsapp`, `email`, `website`, `address`, `social_links`, `logo_url`).
  - Implement `POST /api/cards`, `GET /api/cards`, `PUT /api/cards/{id}`.
* **Day 4–7 (27–30 Aug): Dynamic QR Code Generator**
  - Integrate `ZXing` (Zebra Crossing) library for QR code rendering.
  - Implement `GET /api/cards/{id}/qr/png` and `GET /api/cards/{id}/qr/svg` (Generates dynamic scannable QR code).
  - Implement public card view endpoint: `GET /api/cards/public/{cardSlug}`.
* **Day 8–11 (31 Aug–3 Sep): Card Touchpoint Actions & Analytics Tracking**
  - Track customer actions on cards:
    - `POST /api/cards/{id}/track/scan` (Logs QR scan)
    - `POST /api/cards/{id}/track/call` (Logs call button click)
    - `POST /api/cards/{id}/track/whatsapp` (Logs WhatsApp button click)
  - Return card analytics summary (`GET /api/cards/{id}/analytics`: Total Scans, Unique Visitors, Clicks).
* **Day 12–15 (4–7 Sep): Digital Card Lead Capture Form**
  - Build direct enquiry submission endpoint from visiting cards.
  - Connect submitted card enquiries directly to Vaibhav's Lead Capture & Scoring workflow.
* **Day 16–18 (8–10 Sep): Mobile Responsiveness & QA**
  - Verify card rendering across mobile devices and browsers.
  - Test QR code download and scanning accuracy.

---

### 🤝 Abrar — Customer Management & Follow-ups
**Module Goal:** Manage scheduled follow-ups (Call, WhatsApp, Email, Meetings) and convert won opportunities into permanent Customer profiles.

* **Day 1–3 (24–26 Aug): Follow-up Matrix Entities & APIs**
  - Create `Followup` entity (`lead_id`, `employee_id`, `date`, `time`, `followup_type`, `reminder`, `notes`, `status`).
  - Implement `POST /api/followups` (Schedule follow-up).
  - Implement `GET /api/followups/today` and `GET /api/followups/pending`.
* **Day 4–7 (27–30 Aug): Follow-up Actions & Status Updates**
  - Implement `PUT /api/followups/{id}/complete` (Mark follow-up completed with notes).
  - Implement `PUT /api/followups/{id}/reschedule` (Reschedule date/time).
  - Filter follow-ups by Day, Week, and Month for calendar visualizations.
* **Day 8–11 (31 Aug–3 Sep): Customer Conversion Lifecycle**
  - Create `Customer` entity (`customer_id`, `name`, `company`, `contact_info`, `previous_leads`, `deals_value`, `documents`, `notes`).
  - Implement `POST /api/customers` and `GET /api/customers/{id}`.
  - Build conversion trigger: When lead status changes to `WON`, auto-create a permanent `Customer` record.
* **Day 12–15 (4–7 Sep): Communication History Log**
  - Implement `communication_logs` APIs (`POST /api/communication`, `GET /api/customers/{id}/history`).
  - Log past calls, emails, and meetings under customer profile.
* **Day 16–18 (8–10 Sep): Customer Analytics & QA**
  - Return customer lifetime value and deal history summaries.
  - Validate customer conversion workflows.

---

### 💼 Govardhan — Subscriptions, Payments & Super Admin
**Module Goal:** Implement multi-tenant SaaS subscription plans, simulated payment gateway integration, and the Super Admin control panel.

* **Day 1–3 (24–26 Aug): Subscription Plans & Plan Limit Enforcement**
  - Create `Subscription` and `Plan` entities (`plan_type` [MONTHLY/YEARLY/LIFETIME], `users_limit`, `leads_limit`, `cards_limit`, `storage_limit`).
  - Implement `GET /api/subscription/plans`.
  - Implement Plan Validator (Blocks creating leads/users if company exceeds subscription tier limits).
* **Day 4–7 (27–30 Aug): Payment Gateway Integration (Simulation)**
  - Create `Payment` entity (`payment_id`, `subscription_id`, `gateway` [Razorpay/Paytm/CCAvenue], `amount`, `status`, `transaction_date`, `invoice_id`).
  - Implement `POST /api/payment/checkout` and `POST /api/payment/verify-webhook`.
  - Implement `GET /api/payment/history` and invoice receipt generation.
* **Day 8–11 (31 Aug–3 Sep): Super Admin Workspace Dashboard**
  - Build Super Admin metrics API: `GET /api/admin/dashboard` (Total Companies, Active Subscriptions, Total Leads, System Revenue).
  - Implement company tenant management (`GET /api/admin/companies`, `PUT /api/admin/companies/{id}/status`).
* **Day 12–15 (4–7 Sep): System Settings & Team Departments**
  - Implement Department management APIs (`POST /api/team/departments`, `GET /api/team/departments`).
  - Global CMS & system settings endpoints (`/api/admin/settings`).
* **Day 16–18 (8–10 Sep): Multi-Tenant Security Validation & Final Review**
  - Verify complete workspace isolation between tenant companies.
  - Final project deployment support.

---

## 4. Git Branching & Integration Workflow

```text
                  ┌─── backend/dheeraj ─────┐
                  ├─── backend/Sandeep ─────┤
  origin/main ────┼─── backend/vaibhav ─────┼───➔ PR Review & Final Merge ➔ Production (11 Sep)
                  ├─── backend/chaitanya ───┤
                  ├─── backend/abarar ──────┤
                  └─── backend/govardhan ───┘
```

### Daily Workflow for Each Developer:
1. **Pull latest branch changes:** `git pull origin backend/<your-name>`
2. **Work inside your module.**
3. **Stage & Commit:**
   ```bash
   git add .
   git commit -m "feat(<module>): completed task description"
   ```
4. **Push to your branch:** `git push origin backend/<your-name>`

---

## 5. Final Delivery Acceptance Checklist (Target: 11 September 2026)

- [ ] All 10 database tables created and indexed in MySQL (Pavan)
- [ ] JWT Authentication & Role-Based authorization active on all endpoints (Dheeraj)
- [ ] Lead CRUD and drag-and-drop Kanban pipeline working smoothly (Sandeep)
- [ ] Activity Timeline audit logs, internal notes, and notifications functional (Vaibhav)
- [ ] Dynamic QR code generation & card tracking analytics working (Chaitanya)
- [ ] Follow-up calendar scheduler & Won-to-Customer conversion working (Abrar)
- [ ] Multi-tenant SaaS subscription limits & Razorpay payment simulation working (Govardhan)
- [ ] Swagger OpenAPI documentation available at `/swagger-ui.html`
- [ ] 0 failing unit tests across all test suites
