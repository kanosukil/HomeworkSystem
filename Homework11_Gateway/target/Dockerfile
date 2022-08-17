FROM openjdk:18
ENV WP /usr/src/app
COPY *.jar $WP/app.jar
WORKDIR $WP
EXPOSE 10001
CMD ["java", "-jar", "app.jar"]