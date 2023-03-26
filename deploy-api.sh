# rebuild api container
docker compose build api

# restart api container
docker compose up --no-deps -d api
