Metrics processor for Pretend You're Xyzzy.

Takes metrics information out of Apache Kafka (0.10.1.0 or higher) and saves them into PostgreSQL (9.5 or higher; uses INSERT ... ON CONFLICT DO NOTHING).

Safe to re-run after deleting consumer offsets (see the help output), and will ensure each event in Kafka results in exactly one row in Postgres. If you make a schema change, you can just truncate the affected table(s), reset the offsets, and re-process all of the data.