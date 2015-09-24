start /D EServerTest             	mvn spring-boot:run
start /D ZuulServer          	    mvn spring-boot:run
start /D HystrixDashboard          	mvn spring-boot:run

start /D WeatherMicroService     	mvn spring-boot:run
start /D ForecastMicroService       mvn spring-boot:run
start /D WeatherMCompositeService   mvn spring-boot:run
