REPLACE INTO `role` (ROLE_ID, ROLE) VALUES (1,'ADMIN');
REPLACE INTO `user` (USER_ID, ACTIVE, USERNAME, NAME, LAST_NAME, PASSWORD, IS_USING2FA, SECRET)VALUES (10000,1, 'fred@b.com', 'Fre', 'D', '$2a$10$BlcGX3qpcC1jdbdSRQIbCuhbgK3NXWCfa7nTh.7KqLxhluS0o7GAS', 1, '5HUV2I4IEQHMYBBW')
#REPLACE INTO `user_role` (ROLE_ID, USER_ID) VALUES (1,10000);