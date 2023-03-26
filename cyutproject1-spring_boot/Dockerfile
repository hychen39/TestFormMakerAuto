#### Stage 1: Build the application
FROM gradle:jdk17 as build

# Configure the main working directory inside the docker image.
# This is the base directory used in any further RUN, COPY, and ENTRYPOINT
# commands.
WORKDIR /app

# Only copy dependency-related files
COPY build.gradle settings.gradle /app/

# Only download dependencies
# Eat the expected build failure since no source code has been copied yet
RUN gradle clean build --no-daemon > /dev/null 2>&1 || true

COPY . ./

# Build application
RUN gradle clean build --no-daemon

EXPOSE 8000
CMD ["java", "-jar", "/app/build/libs/cyut_bkend_project-0.0.1-SNAPSHOT.jar"]
