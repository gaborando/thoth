CREATE TABLE IF NOT EXISTS INT_LOCK  (
                           LOCK_KEY CHAR(36),
                           REGION VARCHAR(100),
                           CLIENT_ID CHAR(36),
                           CREATED_DATE TIMESTAMP NOT NULL,
                           constraint INT_LOCK_PK primary key (LOCK_KEY, REGION)
);
