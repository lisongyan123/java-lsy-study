CREATE SCHEMA IF NOT EXISTS iceberg.demo
WITH (location = 's3://warehouse/iceberg/demo');

CREATE TABLE IF NOT EXISTS iceberg.demo.iceberg_orders (
	id BIGINT,
	customer VARCHAR,
	amount DECIMAL(12, 2),
	created_at TIMESTAMP(6)
) WITH (format = 'PARQUET');

SELECT * FROM iceberg.demo.iceberg_orders ORDER BY id;
