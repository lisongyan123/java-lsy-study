CREATE DATABASE IF NOT EXISTS demo;

CREATE TABLE IF NOT EXISTS demo.hudi_orders (
  id BIGINT,
  customer STRING,
  amount DECIMAL(12, 2),
  ts BIGINT
) USING hudi
LOCATION 's3a://warehouse/hudi/demo/hudi_orders'
TBLPROPERTIES (
  type = 'cow',
  primaryKey = 'id',
  preCombineField = 'ts',
  'hoodie.datasource.hive_sync.enable' = 'true',
  'hoodie.datasource.hive_sync.mode' = 'hms',
  'hoodie.datasource.hive_sync.metastore.uris' = 'thrift://hive-metastore:9083'
);

INSERT INTO demo.hudi_orders VALUES
  (1, 'alice', 99.90, 1721383200000),
  (2, 'bob', 128.00, 1721383500000);

SELECT * FROM demo.hudi_orders ORDER BY id;