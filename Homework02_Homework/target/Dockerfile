FROM openjdk:18
ENV WP /usr/src/app
COPY *.jar $WP/app.jar
WORKDIR $WP
EXPOSE 9992
CMD ["java", "-jar", "app.jar"]