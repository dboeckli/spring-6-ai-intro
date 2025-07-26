# Remove all stopped containers
docker container prune --force

# Remove all unused volumes
docker volume prune --force

# Remove unused images
docker image prune --force

# Remove unused network
docker network prune --force

# Remove all unused containers, volumes, images (in this order)
docker system prune --force

# remove all
docker system prune --all --volumes --force

