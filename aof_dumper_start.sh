tail -f ~/work/redis-2.8.8/src/appendonly.aof | java -cp bin s3streampipe.S3StreamPipe bucketname
