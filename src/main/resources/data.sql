REPLACE INTO `role` (ROLE_ID, ROLE) VALUES (1,'ADMIN');
#REPLACE INTO `user` (USER_ID, ACTIVE, USERNAME, NAME, LAST_NAME, PASSWORD, IS_USING2FA, SECRET)VALUES (7,1, 'fred@b.com', 'Fre', 'D', '$2a$10$r9Nv6o/sD.eGd8gjhR7f9.LAWupt2va1LqNCy1trDJfPIPKnaAdMi', 1, 'XKL3TPKMEPBS2PE4')
#REPLACE INTO `user_role` (ROLE_ID, USER_ID) VALUES (1,10000);