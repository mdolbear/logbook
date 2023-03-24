/*
 * Initial schema creation
 */

--
-- Disable key constraints
--
-- SET session_replication_role = 'replica';
CREATE TABLE IF NOT EXISTS logbooks (
                                        id bigserial NOT NULL,
                                        version bigint NOT NULL,
                                        name varchar(256),
    start_date timestamp without time zone NOT NULL,
    CONSTRAINT logbooks_pkey PRIMARY KEY (id)
    );

ALTER TABLE logbooks ADD CONSTRAINT logbooks_name_unique UNIQUE (name);

CREATE TABLE IF NOT EXISTS logbook_entries
(
    id bigserial NOT NULL,
    version bigint NOT NULL,
    logbook_id bigint NOT NULL,
    activity_date timestamp without time zone NOT NULL,
    CONSTRAINT logbook_entries_pkey PRIMARY KEY (id),
    CONSTRAINT logbook_ent_to_logbooks_fk FOREIGN KEY (logbook_id)
    REFERENCES logbooks (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
    );

CREATE TABLE IF NOT EXISTS comments
(
    id bigserial NOT NULL,
    version bigint NOT NULL,
    details TEXT,
    created_at timestamp without time zone NOT NULL,
    CONSTRAINT comments_pkey PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS log_entry_comments
(
    id bigserial NOT NULL,
    log_entry_id bigint NOT NULL,
    comment_id bigint NOT NULL,
    CONSTRAINT log_entry_comments_pkey PRIMARY KEY (id),
    CONSTRAINT log_entry_comments_to_logent_fk FOREIGN KEY (log_entry_id)
    REFERENCES logbook_entries (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION,
    CONSTRAINT log_entry_comments_to_comments_fk FOREIGN KEY (comment_id)
    REFERENCES comments (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
    );

CREATE TABLE IF NOT EXISTS activity_details
(
    id bigserial NOT NULL,
    version bigint NOT NULL,
    details TEXT,
    CONSTRAINT activity_details_pkey PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS activities
(
    id bigserial NOT NULL,
    version bigint NOT NULL,
    activity_type varchar(40) NOT NULL,
    duration bigint,
    duration_units varchar(40) NOT NULL,
    log_entry_id bigint NOT NULL,
    activity_details_id bigint,
    CONSTRAINT activities_pkey PRIMARY KEY (id),
    CONSTRAINT activities_to_logent_fk FOREIGN KEY (log_entry_id)
    REFERENCES logbook_entries (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION,
    CONSTRAINT activities_activity_details_fk FOREIGN KEY (activity_details_id)
    REFERENCES activity_details (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
    );

CREATE TABLE IF NOT EXISTS activity_comments
(
    id bigserial NOT NULL,
    activity_id bigint NOT NULL,
    comment_id bigint NOT NULL,
    CONSTRAINT activity_comments_pkey PRIMARY KEY (id),
    CONSTRAINT activity_comments_to_logent_fk FOREIGN KEY (activity_id)
    REFERENCES activities (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION,
    CONSTRAINT activity_comments_to_comments_fk FOREIGN KEY (comment_id)
    REFERENCES comments (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION
    );


--
-- Re-enable key constraints
--
-- SET session_replication_role = 'origin';
