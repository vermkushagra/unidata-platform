CREATE TABLE s_ext_user (
  id         BIGSERIAL UNIQUE         NOT NULL,
  login      VARCHAR(255)             NOT NULL,
  source     VARCHAR(255)             NOT NULL,
  created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
  updated_at  TIMESTAMP WITH TIME ZONE,
  created_by CHARACTER VARYING(256)   NOT NULL,
  updated_by CHARACTER VARYING(256),
  CONSTRAINT pk_s_ext_user PRIMARY KEY (id),
  CONSTRAINT u_source_login UNIQUE (source, login)
);

CREATE TABLE s_ext_token (
  id            BIGSERIAL UNIQUE         NOT NULL,
  token         VARCHAR(255),
  created_at    TIMESTAMPTZ DEFAULT now(),
  updated_at    TIMESTAMPTZ,
  created_by    VARCHAR(255),
  updated_by    VARCHAR(255),
  s_ext_user_id BIGINT                   NOT NULL,
  CONSTRAINT pk_s_ext_token PRIMARY KEY (id),
  CONSTRAINT s_ext_user_id_fk FOREIGN KEY (s_ext_user_id) REFERENCES s_ext_user (id)
);
