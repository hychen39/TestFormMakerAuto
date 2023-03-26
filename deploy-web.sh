# rebuild web container
docker compose build web

# restart web container
docker compose up --no-deps -d web
