CREATE NAMESPACE IF NOT EXISTS iceberg.demo;

CREATE TABLE IF NOT EXISTS iceberg.demo.iceberg_orders (
	id BIGINT,
	customer STRING,
	amount DECIMAL(12, 2),
	created_at TIMESTAMP
) USING iceberg;

INSERT INTO iceberg.demo.iceberg_orders VALUES
	(1, 'alice', 99.90, TIMESTAMP '2026-07-19 10:00:00'),
	(2, 'bob', 128.00, TIMESTAMP '2026-07-19 10:05:00');

SELECT * FROM iceberg.demo.iceberg_orders ORDER BY id;
