CREATE DATABASE secure_file_db1;
USE secure_file_db1;

CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE,
    password VARCHAR(255),
    role VARCHAR(20)
);

CREATE TABLE files (
    id INT PRIMARY KEY AUTO_INCREMENT,
    filename VARCHAR(255),
    filepath VARCHAR(255),
    uploaded_by INT,
    upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (uploaded_by) REFERENCES users(id)
);

CREATE TABLE download_logs (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    file_id INT,
    download_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (file_id) REFERENCES files(id)
);
SELECT * FROM users;
SELECT * FROM files;
SELECT * FROM download_logs;

SELECT dl.id, u.username AS downloaded_by, f.filename, dl.download_time
FROM download_logs dl
JOIN users u ON dl.user_id = u.id
JOIN files f ON dl.file_id = f.id;

INSERT INTO users(username, password, role) VALUES ('admin', 'admin123', 'admin');

