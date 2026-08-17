-- ============================================================
--  UniMate - database schema
--  CSE 327  |  Md. Ashikur Rahman Siyam & Shudarshan Ghosh Durjoy
--
--  Run this once:
--     CREATE DATABASE unimate;
--     USE unimate;
--     SOURCE schema.sql;
-- ============================================================

DROP TABLE IF EXISTS group_members;
DROP TABLE IF EXISTS study_groups;
DROP TABLE IF EXISTS materials;
DROP TABLE IF EXISTS enrollments;
DROP TABLE IF EXISTS market_items;
DROP TABLE IF EXISTS opportunities;
DROP TABLE IF EXISTS courses;
DROP TABLE IF EXISTS users;

-- ------------------------------------------------------------
-- 1. users  -  one table for Student, Instructor and Admin
--    (matches the User -> Student/Instructor/Admin hierarchy)
-- ------------------------------------------------------------
CREATE TABLE users (
    user_id       INT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100)  NOT NULL,
    email         VARCHAR(120)  NOT NULL UNIQUE,
    password_hash VARCHAR(64)   NOT NULL,
    role          ENUM('STUDENT','INSTRUCTOR','ADMIN') NOT NULL DEFAULT 'STUDENT',
    university    VARCHAR(20),
    department    VARCHAR(50),
    student_id    VARCHAR(20),          -- students only
    designation   VARCHAR(60),          -- instructors only
    semester      VARCHAR(20),          -- students only
    status        ENUM('PENDING','ACTIVE','BLOCKED') NOT NULL DEFAULT 'ACTIVE',
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ------------------------------------------------------------
-- 2. courses
-- ------------------------------------------------------------
CREATE TABLE courses (
    course_id     INT AUTO_INCREMENT PRIMARY KEY,
    code          VARCHAR(15)  NOT NULL UNIQUE,
    title         VARCHAR(120) NOT NULL,
    credits       INT          NOT NULL DEFAULT 3,
    prerequisites VARCHAR(100),
    difficulty    ENUM('EASY','MEDIUM','HARD') DEFAULT 'MEDIUM',
    description   TEXT,
    instructor_id INT,
    FOREIGN KEY (instructor_id) REFERENCES users(user_id) ON DELETE SET NULL
);

-- ------------------------------------------------------------
-- 3. enrollments  -  association between Student and Course
-- ------------------------------------------------------------
CREATE TABLE enrollments (
    enrollment_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id    INT NOT NULL,
    course_id     INT NOT NULL,
    enrolled_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY one_enrollment (student_id, course_id),
    FOREIGN KEY (student_id) REFERENCES users(user_id)   ON DELETE CASCADE,
    FOREIGN KEY (course_id)  REFERENCES courses(course_id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- 4. materials  -  notes, slides, past questions, lab files
-- ------------------------------------------------------------
CREATE TABLE materials (
    material_id INT AUTO_INCREMENT PRIMARY KEY,
    course_id   INT NOT NULL,
    type        ENUM('NOTES','SLIDES','PAST_QUESTIONS','LAB') DEFAULT 'NOTES',
    title       VARCHAR(150) NOT NULL,
    file_path   VARCHAR(255) NOT NULL,
    uploaded_by INT,
    downloads   INT DEFAULT 0,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id)   REFERENCES courses(course_id) ON DELETE CASCADE,
    FOREIGN KEY (uploaded_by) REFERENCES users(user_id)     ON DELETE SET NULL
);

-- ------------------------------------------------------------
-- 5. study_groups
-- ------------------------------------------------------------
CREATE TABLE study_groups (
    group_id    INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(120) NOT NULL,
    course_code VARCHAR(15),
    university  VARCHAR(30),
    description TEXT,
    max_members INT DEFAULT 25,
    created_by  INT,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE SET NULL
);

-- ------------------------------------------------------------
-- 6. group_members
-- ------------------------------------------------------------
CREATE TABLE group_members (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    group_id   INT NOT NULL,
    student_id INT NOT NULL,
    joined_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY one_membership (group_id, student_id),
    FOREIGN KEY (group_id)   REFERENCES study_groups(group_id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES users(user_id)         ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- 7. market_items  -  nothing is visible until an admin approves
-- ------------------------------------------------------------
CREATE TABLE market_items (
    item_id        INT AUTO_INCREMENT PRIMARY KEY,
    title          VARCHAR(150) NOT NULL,
    description    TEXT,
    category       ENUM('BOOKS','EQUIPMENT','NOTES','OTHER') DEFAULT 'BOOKS',
    price          DECIMAL(10,2) NOT NULL,
    item_condition ENUM('NEW','LIKE_NEW','GOOD','WORN') DEFAULT 'GOOD',
    pickup_point   VARCHAR(100),
    status         ENUM('PENDING','LIVE','SOLD','REMOVED') DEFAULT 'PENDING',
    seller_id      INT,
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (seller_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- 8. opportunities  -  internships, hackathons, scholarships
-- ------------------------------------------------------------
CREATE TABLE opportunities (
    opportunity_id INT AUTO_INCREMENT PRIMARY KEY,
    title          VARCHAR(150) NOT NULL,
    type           ENUM('INTERNSHIP','HACKATHON','SCHOLARSHIP','CONTEST') DEFAULT 'INTERNSHIP',
    company        VARCHAR(100),
    description    TEXT,
    stipend        VARCHAR(60),
    deadline       DATE,
    apply_link     VARCHAR(255),
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ------------------------------------------------------------
-- helpful indexes for the search screens
-- ------------------------------------------------------------
CREATE INDEX idx_courses_code   ON courses(code);
CREATE INDEX idx_items_status   ON market_items(status);
CREATE INDEX idx_opp_deadline   ON opportunities(deadline);
CREATE INDEX idx_groups_course  ON study_groups(course_code);
