CREATE TABLE hudi_orders (
  id BIGINT,
  customer STRING,
  amount DECIMAL(12, 2),
  ts BIGINT,
  PRIMARY KEY (id) NOT ENFORCED
) WITH (
  'connector' = 'hudi',
  'path' = 's3a://warehouse/hudi/demo/hudi_orders',
  'table.type' = 'COPY_ON_WRITE',
  'precombine.field' = 'ts',
  'hive_sync.enable' = 'true',
  'hive_sync.mode' = 'hms',
  'hive_sync.metastore.uris' = 'thrift://hive-metastore:9083',
  'hive_sync.db' = 'demo',
  'hive_sync.table' = 'hudi_orders'
);

SELECT * FROM hudi_orders;