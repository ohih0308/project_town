-- categories
CREATE TABLE categories
(
    category_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(200) NOT NULL
);

-- boards
CREATE TABLE boards
(
    board_id    BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    category_id BIGINT UNSIGNED NOT NULL,
    name        VARCHAR(200) NOT NULL,
    FOREIGN KEY (category_id) REFERENCES categories (category_id)
);

-- users
CREATE TABLE users
(
    user_id    BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_type  TINYINT UNSIGNED NOT NULL,
    email      VARCHAR(100) NOT NULL UNIQUE,
    username   VARCHAR(100) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    created_at DATETIME     NOT NULL
);

-- profile_images
CREATE TABLE profile_images
(
    saved_file_name    VARCHAR(100) PRIMARY KEY,
    user_id            BIGINT UNSIGNED NOT NULL,
    original_file_name VARCHAR(100) NOT NULL,
    extension          VARCHAR(100) NOT NULL,
    directory          VARCHAR(300) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (user_id)
);

-- posts
CREATE TABLE posts
(
    post_id    BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    board_id   BIGINT UNSIGNED NOT NULL,
    user_id    BIGINT UNSIGNED,
    user_type  TINYINT UNSIGNED NOT NULL,
    ip         VARCHAR(45)   NOT NULL,
    author     VARCHAR(100)  NOT NULL DEFAULT '',
    password   VARCHAR(255)  NOT NULL DEFAULT '',
    subject    VARCHAR(2000) NOT NULL,
    body       MEDIUMTEXT    NOT NULL,
    views      BIGINT UNSIGNED NOT NULL,
    created_at DATETIME      NOT NULL,
    updated_at DATETIME      NOT NULL,
    FOREIGN KEY (board_id) REFERENCES boards (board_id),
    FOREIGN KEY (user_id) REFERENCES users (user_id)
);

-- attachments
CREATE TABLE attachments
(
    file_name VARCHAR(50) PRIMARY KEY,
    post_id   BIGINT UNSIGNED NOT NULL,
    extension VARCHAR(10)  NOT NULL,
    directory VARCHAR(300) NOT NULL,
    FOREIGN KEY (post_id) REFERENCES posts (post_id)
);

-- thumbnails
CREATE TABLE thumbnails
(
    post_id   BIGINT UNSIGNED NOT NULL,
    file_name VARCHAR(50),
    directory VARCHAR(300) NOT NULL,
    PRIMARY KEY (post_id, file_name),
    FOREIGN KEY (post_id) REFERENCES posts (post_id)
);

-- post_appraisals
CREATE TABLE post_appraisals
(
    post_appraisal_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id           BIGINT UNSIGNED,
    post_id           BIGINT UNSIGNED NOT NULL,
    is_like           BIT         NOT NULL,
    ip                VARCHAR(45) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (user_id),
    FOREIGN KEY (post_id) REFERENCES posts (post_id)
);

-- comments
CREATE TABLE comments
(
    comment_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    post_id    BIGINT UNSIGNED NOT NULL,
    user_type  TINYINT UNSIGNED NOT NULL,
    user_id    VARCHAR(100),
    ip         VARCHAR(45)  NOT NULL,
    author     VARCHAR(100) NOT NULL,
    password   VARCHAR(255) NOT NULL DEFAULT '',
    content    MEDIUMTEXT   NOT NULL,
    created_at DATETIME     NOT NULL,
    FOREIGN KEY (post_id) REFERENCES posts (post_id)
);

-- comment_appraisals
CREATE TABLE comment_appraisals
(
    comment_appraisal_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id             BIGINT UNSIGNED,
    comment_id          BIGINT UNSIGNED NOT NULL,
    is_like             BIT             NOT NULL,
    ip                  VARCHAR(45)     NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (user_id),
    FOREIGN KEY (comment_id) REFERENCES comments (comment_id)
);

-- guestbook_configs
CREATE TABLE guestbook_configs
(
    user_id       BIGINT UNSIGNED NOT NULL UNIQUE,
    activation    BIT             NOT NULL,
    private_read  BIT             NOT NULL,
    member_write  BIT             NOT NULL,
    guest_write   BIT             NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (user_id)
);

-- guestbook_posts
CREATE TABLE guestbook_posts
(
    guestbook_post_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    to_user_id        BIGINT UNSIGNED NOT NULL,
    from_user_id      BIGINT UNSIGNED,
    user_type         TINYINT UNSIGNED NOT NULL,
    ip                VARCHAR(45)     NOT NULL,
    author            VARCHAR(100)    NOT NULL DEFAULT '',
    password          VARCHAR(100)    NOT NULL DEFAULT '',
    seal              BIT             NOT NULL,
    subject           VARCHAR(2000)   NOT NULL,
    body              MEDIUMTEXT      NOT NULL,
    created_at        DATETIME        NOT NULL,
    FOREIGN KEY (to_user_id) REFERENCES users (user_id),
    FOREIGN KEY (from_user_id) REFERENCES users (user_id)
);

-- guestbook_comments
CREATE TABLE guestbook_comments
(
    guestbook_comment_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    guestbook_post_id    BIGINT UNSIGNED NOT NULL,
    user_type            TINYINT UNSIGNED NOT NULL,
    user_id              VARCHAR(100),
    ip                   VARCHAR(45)     NOT NULL,
    author               VARCHAR(100)    NOT NULL,
    password             VARCHAR(100),
    comment              MEDIUMTEXT      NOT NULL,
    created_at           DATETIME        NOT NULL,
    FOREIGN KEY (guestbook_post_id) REFERENCES guestbook_posts (guestbook_post_id)
);

-- notifications
CREATE TABLE notifications
(
    notification_id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT UNSIGNED,
    type            TINYINT         NOT NULL,
    message         VARCHAR(255)    NOT NULL,
    created_at      DATETIME        NOT NULL,
    updated_at      DATETIME        NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (user_id)
);
