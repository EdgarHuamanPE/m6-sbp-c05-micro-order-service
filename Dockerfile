# dockerfile for user Services
#Use eclipse temurin JRE17  as the  base image
#FROM eclipse-temurin:17-jdk
FROM eclipse-temurin:17-jre-alpine
RUN apk add --no-cache curl
#set the  working directory inside  the container
WORKDIR /app

#copy the build JAr file from  target  directory to the container
COPY target/*.jar /app/order-service.jar
#Expose port 8080 for the user Service
EXPOSE 8080

#Delete the command  to run  the user Service
CMD ["java", "-jar", "/app/order-service.jar"]