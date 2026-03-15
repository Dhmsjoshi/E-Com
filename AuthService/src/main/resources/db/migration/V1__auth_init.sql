CREATE TABLE roles (
                       id BINARY(16) NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                       is_deleted BIT(1) DEFAULT b'0' NOT NULL,
                       name VARCHAR(50) NOT NULL,
                       description VARCHAR(255) NULL,
                       CONSTRAINT pk_roles PRIMARY KEY (id),
                       CONSTRAINT uc_roles_name UNIQUE (name)
);

CREATE TABLE users (
                       id BINARY(16) NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                       is_deleted BIT(1) DEFAULT b'0' NOT NULL,
                       email VARCHAR(255) NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       username VARCHAR(50) NULL,
                       phone_number VARCHAR(20) NULL,
                       is_email_verified BIT(1) DEFAULT b'0' NOT NULL,
                       CONSTRAINT pk_users PRIMARY KEY (id),
                       CONSTRAINT uc_users_email UNIQUE (email),
                       CONSTRAINT uc_users_username UNIQUE (username)
);

CREATE TABLE sessions (
                          id BINARY(16) NOT NULL,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          is_deleted BIT(1) DEFAULT b'0' NOT NULL,
                          token VARCHAR(1000) NOT NULL,
                          expiring_at DATETIME NOT NULL,
                          user_id BINARY(16) NOT NULL,
                          status VARCHAR(20) NOT NULL,
                          CONSTRAINT pk_sessions PRIMARY KEY (id),
                          CONSTRAINT fk_sessions_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE user_roles (
                            user_id BINARY(16) NOT NULL,
                            role_id BINARY(16) NOT NULL,
                            CONSTRAINT pk_user_roles PRIMARY KEY (user_id, role_id),
                            CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users (id),
                            CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles (id)
);

CREATE INDEX idx_user_email ON users (email);
CREATE INDEX idx_user_username ON users (username);