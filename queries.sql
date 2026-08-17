-- ============================================================
--  UniMate - queries used while building and testing
--  Run these in MySQL Workbench to check the data by hand
-- ============================================================

-- how many rows landed in each table
SELECT 'users' AS table_name, COUNT(*) AS rows_count FROM users
UNION ALL SELECT 'courses',       COUNT(*) FROM courses
UNION ALL SELECT 'enrollments',   COUNT(*) FROM enrollments
UNION ALL SELECT 'materials',     COUNT(*) FROM materials
UNION ALL SELECT 'study_groups',  COUNT(*) FROM study_groups
UNION ALL SELECT 'group_members', COUNT(*) FROM group_members
UNION ALL SELECT 'market_items',  COUNT(*) FROM market_items
UNION ALL SELECT 'opportunities', COUNT(*) FROM opportunities;

-- users split by role
SELECT role, status, COUNT(*) AS total FROM users GROUP BY role, status;

-- the course list exactly as CourseDAO.search() builds it
SELECT c.code, c.title, c.difficulty, u.name AS instructor,
       (SELECT COUNT(*) FROM enrollments e WHERE e.course_id = c.course_id) AS enrolled
FROM courses c LEFT JOIN users u ON u.user_id = c.instructor_id
ORDER BY c.code;

-- what Siyam (user_id 7) is enrolled in
SELECT c.code, c.title FROM enrollments e
JOIN courses c ON c.course_id = e.course_id
WHERE e.student_id = 7;

-- materials for CSE 327
SELECT m.title, m.type, m.downloads, u.name AS uploaded_by
FROM materials m
JOIN courses c ON c.course_id = m.course_id
LEFT JOIN users u ON u.user_id = m.uploaded_by
WHERE c.code = 'CSE 327';

-- study groups with how full they are
SELECT g.name, g.course_code, g.university,
       (SELECT COUNT(*) FROM group_members m WHERE m.group_id = g.group_id) AS members,
       g.max_members
FROM study_groups g ORDER BY members DESC;

-- what a student sees in the marketplace (LIVE only)
SELECT i.title, i.price, i.item_condition, u.name AS seller
FROM market_items i LEFT JOIN users u ON u.user_id = i.seller_id
WHERE i.status = 'LIVE' ORDER BY i.item_id DESC;

-- what the admin has waiting
SELECT 'pending user' AS kind, name AS detail FROM users WHERE status = 'PENDING'
UNION ALL
SELECT 'pending item', title FROM market_items WHERE status = 'PENDING';

-- opportunities closing soonest first
SELECT title, type, company, stipend, deadline,
       DATEDIFF(deadline, CURDATE()) AS days_left
FROM opportunities ORDER BY deadline;

-- most downloaded materials
SELECT m.title, c.code, m.downloads FROM materials m
JOIN courses c ON c.course_id = m.course_id
ORDER BY m.downloads DESC LIMIT 5;

-- reset everything and start over
-- SOURCE schema.sql;
-- SOURCE seed.sql;
