/*
 * Users table
 */
CREATE TABLE IF NOT EXISTS users (
                                     id bigserial NOT NULL,
                                     version bigint NOT NULL,
                                     user_name varchar(256),
                                     CONSTRAINT users_pkey PRIMARY KEY (id)
);

ALTER TABLE users ADD CONSTRAINT users_username_unique UNIQUE (user_name);
ALTER TABLE logbooks ADD COLUMN IF NOT EXISTS user_id bigint;
alter table logbooks add constraint logbooks_to_user_fk FOREIGN KEY (user_id) REFERENCES users (id)
    ON DELETE NO ACTION;

insert into users (id, version, user_name) values (nextval('users_id_seq'::regclass), 0, 'athelete');
update logbooks set user_id = currval('users_id_seq'::regclass);
