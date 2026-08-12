# syntax=docker/dockerfile:1

# ---------- build stage ----------
# Full JDK, only used to produce the jar. Nothing from this stage ships.
FROM eclipse-temurin:21-jdk AS build

WORKDIR /build

# Copy the wrapper and the pom first: as long as pom.xml is unchanged, Docker
# reuses the cached dependency layer instead of re-downloading Maven Central.
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -B -ntp dependency:go-offline

COPY src/ src/

# Tests are skipped on purpose: BackendApplicationTests is a @SpringBootTest that
# needs a live MySQL, which does not exist during an image build. The Jenkins
# pipeline runs the suite before this image is ever built.
RUN ./mvnw -B -ntp clean package -DskipTests

# ---------- runtime stage ----------
# JRE only — roughly half the size of the JDK image, and no compiler shipped to production.
FROM eclipse-temurin:21-jre

WORKDIR /app

# Never run the app as root.
RUN useradd --system --create-home --shell /usr/sbin/nologin spring

COPY --from=build /build/target/backend-*.jar app.jar
RUN chown spring:spring app.jar

USER spring

EXPOSE 8080

# Container memory, not host memory, decides the heap.
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75"

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
