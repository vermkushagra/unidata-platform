ALTER TABLE public.s_user_s_role
DROP CONSTRAINT fk_s_users_s_roles_s_roles,
ADD CONSTRAINT fk_s_users_s_roles_s_roles
   FOREIGN KEY (s_roles_id)
   REFERENCES s_role( id )
   ON DELETE CASCADE;
   
ALTER TABLE public.s_user_s_role
DROP CONSTRAINT fk_s_users_s_roles_s_user,
ADD CONSTRAINT fk_s_users_s_roles_s_user
   FOREIGN KEY (s_users_id)
   REFERENCES s_user( id )
   ON DELETE CASCADE;