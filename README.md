# DiskLruCache
Simple Thread Safe Disk Backed LRU Cache

This implementation supports two methods
1. Put(K, T)
2. T get(K)

Both the implementations are thread safe.

Cache size has to be provided via constructor.

A directory with name cache is created and all the data is stored here.
For every entry there will one file.
Key hashcode is file name and content is value object bytecode.

get() method will first get from cache object which is implemented using LinkedHashMap.
if key is not found in cache object then find from disk and add it into cache.

put() method add/refresh element in cache first then update the disk.
