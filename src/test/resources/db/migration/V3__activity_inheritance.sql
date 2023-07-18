/*
 * Support for activity inheritance
 */
ALTER TABLE activities ADD COLUMN IF NOT EXISTS distance numeric;
ALTER TABLE activities ADD COLUMN IF NOT EXISTS distance_units varchar(40) not null default 'METERS';
ALTER TABLE activities ADD COLUMN IF NOT EXISTS avg_watts numeric;
ALTER TABLE activities ADD COLUMN IF NOT EXISTS total_calories numeric;
ALTER TABLE activities ADD COLUMN IF NOT EXISTS avg_heart_rate numeric;
ALTER TABLE activities ADD COLUMN IF NOT EXISTS activity_discriminator varchar(128);
