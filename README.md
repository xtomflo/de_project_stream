# Streaming Data Project - Fraudulent User Detection using Apache Flink

This is my attempt to learn some Scala, get familiar with Flink and gain experience working with streaming data in practice.
To kick-off the project I've use the starting form of the repo from @josephmachado which is described in this article: [Data Engineering Project: Stream Edition](https://www.startdataengineering.com/post/data-engineering-project-for-beginners-stream-edition/)

## Prerequisites

You will need to install

1. [docker](https://docs.docker.com/get-docker/) (make sure to have docker-compose as well)
2. [pgcli](https://github.com/dbcli/pgcli) to connect to our `postgres` instance
3. [git](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git) to clone the starter repo
4. **Optional:** [tmux](https://github.com/tmux/tmux/wiki)

# Design

![Engineering Design](assets/images/system_design.png)

# Data

The data will be generated by a data generation script at `src/main/scala/com.startdataengineering/ServerLogGenerator.scala`.

# Setup and run

Everything is dockerized. Run the below commands in the project directory.

```bash
docker-compose up -d # -d mean run in detached mode (in the background)
docker ps # display all running containers
```

Do some manual checks using

```bash

docker exec -t de_project_stream_kafka_1 kafka-console-consumer.sh --bootstrap-server :9092  --topic server-logs --from-beginning --max-messages 10 # used to check the first 10 messages in the server-logs  topic
docker exec -t de_project_stream_kafka_1 kafka-console-consumer.sh --bootstrap-server :9092  --topic alerts --from-beginning --max-messages 10 # used to check the first 10 messages in the alerts topic
```

and

```bash
pgcli -h localhost -p 5432 -U startdataengineer events
```
password is `password`

```sql
select * from server_log limit 5; -- should match the first 5 from the server-logs topic
select count(*) from server_log; -- 100000
\q -- to exit pgcli
```

take down all the running containers using down in the project repository

```bash
docker-compose down
```

# Contact

website: https://www.startdataengineering.com/

twitter: https://twitter.com/start_data_eng