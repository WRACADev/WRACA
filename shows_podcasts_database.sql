
CREATE DATABASE ShowsPodcastsDB;

USE ShowsPodcastsDB;

CREATE TABLE Hosts (
    host_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    bio TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Shows (
    show_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    genre VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Podcasts (
    podcast_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    show_id INT,
    host_id INT,
    release_date DATE,
    duration TIME,
    file_path VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (show_id) REFERENCES Shows(show_id),
    FOREIGN KEY (host_id) REFERENCES Hosts(host_id)
);

CREATE TABLE Episodes (
    episode_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    podcast_id INT,
    release_date DATE,
    duration TIME,
    file_path VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (podcast_id) REFERENCES Podcasts(podcast_id)
);
