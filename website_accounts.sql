CREATE DATABASE website_accounts;

USE website_accounts;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    personal_info TEXT,
    favorite_genres VARCHAR(255),
    recent_listening_history TEXT,
    user_type ENUM('premium', 'standard') DEFAULT 'standard',
    chat_names TEXT
);
