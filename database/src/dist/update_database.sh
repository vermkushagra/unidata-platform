#!/bin/sh

. ./init_env.sh

./flyway/flyway \
-url=jdbc:postgresql://$UNISTORE_DB_HOST:$UNISTORE_DB_PORT/$UNISTORE_DB_NAME\?user=$UNISTORE_DB_USER_NAME\&password=$UNISTORE_DB_USER_PASSWORD\&currentSchema=$UNISTORE_DB_SCHEMA \
-locations=filesystem:./migration \
migrate



