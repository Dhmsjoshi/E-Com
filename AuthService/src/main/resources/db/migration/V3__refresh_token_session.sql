CREATE TABLE sessions (
                          id BINARY(16) NOT NULL,
                          refresh_token VARCHAR(512) NOT NULL,
                          expiring_at DATETIME(6) NOT NULL,
                          user_id BINARY(16) NOT NULL,
                          status VARCHAR(20) NOT NULL,
                          created_at DATETIME(6) NOT NULL,
                          updated_at DATETIME(6) NOT NULL,
                          is_deleted BIT(1) DEFAULT 0 NOT NULL,
                          PRIMARY KEY (id),
                          CONSTRAINT uc_sessions_refresh_token UNIQUE (refresh_token),
                          CONSTRAINT fk_session_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB;