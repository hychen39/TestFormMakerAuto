#### Stage 1: Build the web application
FROM node:16.19-alpine as build

# Install python3
RUN apk add python3

# Configure the main working directory inside the docker image.
# This is the base directory used in any further RUN, COPY, and ENTRYPOINT
# commands.
WORKDIR /app

# Copy the package.json as well as the package-lock.json and install
# the dependencies. This is a separate step so the dependencies
# will be cached unless changes to one of those two files
# are made.
COPY package.json package-lock.json ./
RUN npm install

# Copy the main application
COPY . ./

# Environments
ARG VUE_APP_SPRING_BOOT_REST_API
ARG VUE_APP_AUTOPAPER_MINIMUM_ITEMS
ENV VUE_APP_SPRING_BOOT_REST_API=${VUE_APP_SPRING_BOOT_REST_API}
ENV VUE_APP_AUTOPAPER_MINIMUM_ITEMS=${VUE_APP_AUTOPAPER_MINIMUM_ITEMS}

# Build the application
RUN npm run build

#### Stage 2: Serve the web application from Nginx
FROM nginx:1.17.0-alpine

# Copy the web build from Stage 1
COPY --from=build /app/dist /var/www

# Copy our custom nginx config
COPY ./container_config/nginx.conf /etc/nginx/nginx.conf

# Expose port 80 to the Docker host, so we can access it
# from the outside.
EXPOSE 80
ENTRYPOINT ["nginx", "-g", "daemon off;"]
