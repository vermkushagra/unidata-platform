ALTER TABLE s_user_property ADD CONSTRAINT uq_s_user_property_display_name UNIQUE (display_name);
ALTER TABLE s_role_property ADD CONSTRAINT uq_s_role_property_display_name UNIQUE (display_name);
