# kuehne-nagel-test
1. Build the application using maven
2. run `docker build --tag=docker-spring-boot:v1.1 .` to build image
3. run `docker run -p8080:8080 docker-spring-boot:v1.1` to start up the container
4. go to : http://localhost:8080/swagger-ui.html to test endpoints
5. go to : http://localhost:8080/h2-console to view h2 console
6. endpoint auth username: `kuehne.nagel` password: `secret`
7. h2 console jdbc url: `jdbc:h2:mem:kuehnenagel` username: `sa` password: `leave it empty`
