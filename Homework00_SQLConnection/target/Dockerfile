FROM openjdk:18
ENV WP /usr/src/app
COPY *.jar $WP/app.jar
WORKDIR $WP
EXPOSE 9990
CMD ["java", "-jar", "app.jar"]