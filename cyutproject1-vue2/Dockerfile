FROM node:18.17.1-alpine3.18 as build

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

# Build the application
RUN npm run build

FROM nginx:1.25.2-alpine3.18
ENV ADMIN_USERNAME admin
ENV ADMIN_PASSWORD PASSWORD
# Install apache2-utils for htpasswd
RUN apk add apache2-utils;
# Copy the web build from Stage 1
COPY --from=build /app/dist /var/www

COPY ./container_config/* /etc/nginx
COPY ./entrypoint.sh /docker-entrypoint.d/40-htpasswd.sh
RUN chmod +x /docker-entrypoint.d/40-htpasswd.sh

# Expose port 80 to the Docker host, so we can access it
# from the outside.
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]

