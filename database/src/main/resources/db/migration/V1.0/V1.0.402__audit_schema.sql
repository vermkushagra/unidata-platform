CREATE TABLE IF NOT EXISTS s_custom_settings (
	key VARCHAR(255) NULL,
  user_name  VARCHAR(255) NULL,
	value text not null,
	update_date timestamp with time zone
);
