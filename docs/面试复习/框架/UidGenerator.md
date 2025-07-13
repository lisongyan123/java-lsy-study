# 百度的 [UidGenerator](https://github.com/baidu/uid-generator/blob/master/README.zh_cn.md)

# snowflake

- sign(1bit) 固定1bit符号标识, 即生成的UID为正数
- delta seconds (28 bits) 当前时间, 相对于时间基点"2016-05-20"的增量值, 单位: 秒, 最多可支持约8.7年
- worker id (22 bits) 机器id, 最多可支持约420w次机器启动, 内置实现为在启动时由数据库分配, 默认分配策略为用后即弃, 后续可提供复用策略 
- sequence (13 bits) 每秒下的并发序列, 13 bits可支持每秒8192个并发

# UidGenerator

UidGenerator 通过借用未来时间来解决 sequence 天然存在的并发限制, 采用 RingBuffer 来缓存已生成的 UID, 并行化 UID 的生产和消费, 同时对 CacheLine 补齐, 避免了由 RingBuffer 带来的硬件级`伪共享`问题, 单机 QPS 可达 600w 

## CachedUidGenerator
1. Ringbugger
   1. Tail指针: Producer 生产的最大序号, tail 不能超过 Cursor, 生产者不能覆盖未消费的 Slot, 赶上了可以用 rejectedPutBufferHandler 指定 PutRejectPolicy
   2. Cursor指针: Consumer 消费到的最小序号, Cursor 不能超过 Tail, 赶上了 rejectedTakeBufferHandler 指定 TakeRejectPolicy
2. CachedUidGenerator 采用了双 RingBuffer
   1. Uid-RingBuffer 用于存储 Uid
   2. Flag-RingBuffer 用于存储 Uid 状态(是否可填充、是否可消费)
