-- DevConnect Database Schema
-- Run this in pgAdmin or psql before starting the app

-- Create database (run separately if needed)
-- CREATE DATABASE devconnect;

-- Users table
CREATE TABLE IF NOT EXISTS users (
                                     id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('DEVELOPER', 'RECRUITER', 'ADMIN')),
    status VARCHAR(20) DEFAULT 'active' CHECK (status IN ('active', 'suspended')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- Developer profiles
CREATE TABLE IF NOT EXISTS developer_profiles (
                                                  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(150),
    bio TEXT,
    location VARCHAR(100),
    experience VARCHAR(50),
    github_url VARCHAR(255),
    linkedin_url VARCHAR(255),
    resume_url VARCHAR(255),
    embedding TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- Developer skills (separate table for easy querying)
CREATE TABLE IF NOT EXISTS developer_skills (
                                                id BIGSERIAL PRIMARY KEY,
                                                profile_id UUID NOT NULL REFERENCES developer_profiles(id) ON DELETE CASCADE,
    skill VARCHAR(100) NOT NULL
    );

-- Job postings
CREATE TABLE IF NOT EXISTS jobs (
                                    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(200) NOT NULL,
    company VARCHAR(150) NOT NULL,
    location VARCHAR(150),
    type VARCHAR(50),
    salary VARCHAR(100),
    experience VARCHAR(50),
    description TEXT,
    status VARCHAR(20) DEFAULT 'active' CHECK (status IN ('active', 'closed', 'flagged')),
    recruiter_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    embedding TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- Job required skills
CREATE TABLE IF NOT EXISTS job_skills (
                                          id BIGSERIAL PRIMARY KEY,
                                          job_id UUID NOT NULL REFERENCES jobs(id) ON DELETE CASCADE,
    skill VARCHAR(100) NOT NULL
    );

-- Job applications
CREATE TABLE IF NOT EXISTS applications (
                                            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    job_id UUID NOT NULL REFERENCES jobs(id) ON DELETE CASCADE,
    developer_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    match_score INTEGER,
    status VARCHAR(30) DEFAULT 'new' CHECK (status IN ('new', 'reviewed', 'shortlisted', 'rejected')),
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(job_id, developer_id)
    );

-- Notifications
CREATE TABLE IF NOT EXISTS notifications (
                                             id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(200) NOT NULL,
    message TEXT,
    type VARCHAR(50),
    is_read BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- Indexes for performance
CREATE INDEX IF NOT EXISTS idx_jobs_recruiter ON jobs(recruiter_id);
CREATE INDEX IF NOT EXISTS idx_jobs_status ON jobs(status);
CREATE INDEX IF NOT EXISTS idx_applications_developer ON applications(developer_id);
CREATE INDEX IF NOT EXISTS idx_applications_job ON applications(job_id);
CREATE INDEX IF NOT EXISTS idx_notifications_user ON notifications(user_id);
CREATE INDEX IF NOT EXISTS idx_developer_skills_profile ON developer_skills(profile_id);

-- Default admin user (password: admin123)
INSERT INTO users (name, email, password, role) VALUES
    ('Admin', 'admin@devconnect.com',
     '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKSaGSHXJwKHGBHNOxRmkF9yBRoO',
     'ADMIN')
    ON CONFLICT (email) DO NOTHING;