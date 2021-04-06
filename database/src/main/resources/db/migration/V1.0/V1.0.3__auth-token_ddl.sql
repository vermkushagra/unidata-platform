CREATE TABLE system_auth_token (
  "id" TEXT NOT NULL,
  "user_login" TEXT NOT NULL,
  "token" TEXT NOT NULL,
  "create_date" TIMESTAMP NOT NULL DEFAULT now(),
  "update_date" TIMESTAMP NOT NULL DEFAULT now(),
  CONSTRAINT system_auth_token_pkey PRIMARY KEY (id)
);

CREATE INDEX auth_token_user_login_idx ON system_auth_token (user_login);