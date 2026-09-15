CREATE TABLE IF NOT EXISTS problems (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    difficulty VARCHAR(50) NOT NULL,
    topic VARCHAR(100) NOT NULL,
    link VARCHAR(500) NOT NULL,
    pattern VARCHAR(100),
    created_at DATETIME NOT NULL,
    INDEX idx_problem_topic (topic),
    INDEX idx_problem_difficulty (difficulty)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
