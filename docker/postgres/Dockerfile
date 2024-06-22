FROM postgres:16.2-alpine3.19
COPY create-db.sh /docker-entrypoint-initdb.d/
RUN chmod +x /docker-entrypoint-initdb.d/create-db.sh
