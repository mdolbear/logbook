/*
 * Initial test data
 */

insert into logbooks (id, version, name, start_date)
values (nextval('logbooks_id_seq'::regclass), 0, 'My Log Book', now());

insert into logbook_entries (id, version, logbook_id, activity_date)
values (nextval('logbook_entries_id_seq'::regclass), 0, currval('logbooks_id_seq'::regclass), now()::date);

insert into activities (id, version, activity_type, duration, duration_units, log_entry_id)
values (nextval('activities_id_seq'::regclass), 0, 'ROWING', 30, 'MINUTES', currval('logbook_entries_id_seq'::regclass));

insert into comments (id, version, details, created_at)
values (nextval('comments_id_seq'::regclass), 0, 'Steady State - 230 pace', now());

insert into activity_comments (id, activity_id, comment_id)
values (nextval('activity_comments_id_seq'::regclass), currval('activities_id_seq'::regclass), currval('comments_id_seq'::regclass));

insert into comments (id, version, details, created_at)
values (nextval('comments_id_seq'::regclass), 0, 'Overall I was feeling pretty good today', now());

insert into log_entry_comments (id, log_entry_id, comment_id)
values (nextval('log_entry_comments_id_seq'::regclass), currval('logbook_entries_id_seq'::regclass), currval('comments_id_seq'::regclass));

insert into activity_details (id, version, details)
values (nextval('activity_details_id_seq'::regclass), 0, 'This is space for more comments or data about an activity.');

update activities set activity_details_id=currval('activity_details_id_seq'::regclass);