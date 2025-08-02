FROM registry.access.redhat.com/ubi8/ubi-minimal:8.9

# Install Java
RUN microdnf install java-21-openjdk-headless && microdnf clean all

WORKDIR /work/
RUN chown 1001 /work \
    && chmod "g+rwX" /work \
    && chown 1001:root /work

# Copy the pre-built Quarkus application
COPY --chown=1001:root target/quarkus-app/lib/ /work/lib/
COPY --chown=1001:root target/quarkus-app/app/ /work/app/
COPY --chown=1001:root target/quarkus-app/quarkus/ /work/quarkus/
COPY --chown=1001:root target/quarkus-app/quarkus-run.jar /work/quarkus-run.jar

EXPOSE 9090
USER 1001

ENV JAVA_OPTIONS_APPEND="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
ENV JAVA_APP_JAR="/work/quarkus-run.jar"

CMD ["java", "-jar", "/work/quarkus-run.jar"] 