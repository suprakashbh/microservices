In this Microservice POC, I used Spring Boot and Netflix OSS to implement microservice and try to cover below few core parts of microservice landscape :

- Service Discovery
- Routing
- Load balancer 
- Gate keeper

System Landscape of microservice of this POC looks like this below:

![alt tag](https://github.com/suprakashbh/microservices/blob/master/ms-landscape.png)

It contains three core microservices, weather, forecast and the other one which composite of weather and forecast services, called weather composite Microservice. And to support the microsrevice landscape, I used this below Netflix Infrastructure services and components :

- Netflix Eureka : Service Discovery Server
- Netflix Ribbon : Dynamic Routing and Load Balancer
- Netflix Zuul : Edge Server / Gate keeper 
- Turbine and Hystrix Dashboard
- Hystrix : Circuit breaker

# Source Code Walk through :

As you can see in github, this POC have have 7 components (3 microservice and 4 infrastructure components) and each of them  have their own pom.xml to build separately. This poc is also include a batch script (for windows), build-all.bat file to build all the component. 

Each Microservice (Weather, Forecast and Weather Composite) is developed by using Spring Boot application and uses Tomcat Server. Spring Rest Template is used to make outgoing rest api call and Spring MVC  is used to expose service as a Rest call.So this individual microservices are nothing but a Spring MVC application from which rest get exposed and wrapped by Spring Boot application.

Now lets see, how to use Spring Cloud and Netflix OSS to build the microservice landscape.

### POM Dependency

Its very easy to use Eureka, Ribbon, Zuul and Hystrix Dashboard in Spring clod by using Pom. Just add this below starter dependency , it will bring all the necessary other dependencies.

To use Eureka (client) and Ribbon (Load Balancer) in a microservice, just add this below dependency in your microservice pom.xml file. See in Weather,Forecast and WeatherComposite microservice pom.xml for details.
			
			` <dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-starter-eureka</artifactId>
				<version>1.0.3.RELEASE</version>
			</dependency>`
			
To set up Eureka server, just add this below dependency in pom.xml. See EServerTest\pom.xml

			` <dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-starter-eureka-server</artifactId>
				<version>1.0.3.RELEASE</version>
			</dependency>`
			
In WeatherComposite microservice have Hystrix as circuit breaker. To use Hystrix in your project, need to add this below dependency

			` <dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-starter-hystrix</artifactId>
				<version>1.0.0.RELEASE</version>
			</dependency>
			
			 <dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-starter-bus-amqp</artifactId>
				<version>1.0.0.RELEASE</version>
			</dependency>    
			
			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-netflix-hystrix-amqp</artifactId>
				<version>1.0.0.RELEASE</version>
			</dependency> `
			
As Hystrix use RabitMQ to communicate between Circuit breaker and Dashboard (monitoring application), thats why you see  dependency for RabitMQ as well along with Hystrix. And also make sure you have RabitMq installed in your machine, else Turbine will not work.

To set up Turbine server, you need below dependency and minimum java 8
			
			` <dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-starter-turbine-amqp</artifactId>
				<version>1.0.0.RELEASE</version>
			</dependency> `
			

### Severs

Developed and configure this Infrastructure servers by using Spring Cloud and Netflix OSS is super easy
	
Eureka Server

To create a Eureka Server by using Spring boot application just create a class like this below

		` 	@SpringBootApplication
			@EnableEurekaServer
			public class EServer {
			public static void main(String[] args) {
				SpringApplication.run(EServer.class, args);
				}		
			}`
			
For complete and configuration see EServerTest project.
	
Zuul Server

To set up a Zuul server,create a java class with @EnableZuulProxy annotation 
	
		` 	@SpringBootApplication
			@Controller
			@EnableZuulProxy
			public class ZuulServerApplication {
			public static void main(String[] args) {
					new SpringApplicationBuilder(ZuulServerApplication.class).web(true).run(args);
				}
			}`
			
Zuul work as a gatekeepr and with no configuration / default configuration, it will allow / route  every service registered with Eureka.
	
In this POC we have configured Zuul (in application.yml) to allow to route to only WeatherComposite microservice,see 	below.
			
		` 	zuul:
				ignoredServices: "*"
				routes:
				weathercompositems:
				path: /weathercomposite/**
			`
			
For detail configuration see ZuulServer project.
			
Turbine Server

Turbine provides information to Hystrix Dashboard from all Hystrix Circuit Breaker and Dashboard use this information to provide graphical representation.
	
To setup turbine, add @EnableTurbineAmqp annotation:
			
			` 	@SpringBootApplication
				@EnableTurbineAmqp
				@EnableDiscoveryClient
				public class TurbineServerApp {

					public static void main(String[] args) {
						new SpringApplicationBuilder(TurbineServerApp.class).run(args);
					}
				}	`
		
### Registered Other Component with Eureka Server
		
To registered microservice or other component with Eureka server, just add @EnableDiscoveryClient 
to the Spring Boot application
		
		` 	
		@SpringBootApplication
		@EnableCircuitBreaker
		@EnableDiscoveryClient
		public class WeatherServiceClientApplication {
			public static void main(String[] args) {
				SpringApplication.run(WeatherServiceClientApplication.class, args);
			}
		}	`

For detail example see Weather, Forecast and WeatherComposite microservice Project.
		
###  Use load balancer Ribbon from Microservice
		
In Netflix OSS microservice architecture , its the responsibility of client / consumer to do the load balancing. To look up and call any microservice use Ribbon component, see below and for full example see WeatherComposite Microservice.

		@Autowired
		private LoadBalancerClient loadBalancer;
		*****
		
		private URI getServiceUrl(String serviceId, String fallbackUri) {
		URI uri = null;
		try {
			ServiceInstance instance = loadBalancer.choose(serviceId); // serviceId is name of the microservice
			uri = instance.getUri();
			
		*****	
		String furl = furi.toString() + "/forecast/" + city;
		Forecast forecast = restTemplate.getForObject(furl, Forecast.class);
			`
# Start the Microservice Landscape :

To run all the component of Microservice landscape, there is a batch script start-all.bat, which will start all the servers and the microservices.Now remember, for Turbine Java 8 is minimum, so make sure you have Java 8 installed in your machine before start start turbine.To run the all the component individually, follow this below steps :
 
To start the servers (Eureka and Zuul), execute this :

		microservices\EServerTest\java -jar target/NetflixEurekaServer-1.0.jar	
		microservices\ZuulServer\java -jar target/NetflixZuulServer-1.0.jar
		microservices\NetflixTurbineServer\java -jar target/NetflixTurbineServer-1.0.jar
		microservices\HystrixDashboard\java -jar target/HystrixDashboard-1.0.jar
	`		
				
When the above server are started, launch the three microservices one by one :

		microservices\WeatherMicroService\java -jar target/WeatherMicroService-1.0.jar
		microservices\ForecastMicroService\java -jar target/ForecastMicroService-1.0.jar
		microservices\WeatherMCompositeService\java -jar target/WeatherMCompositeService-1.0.jar
	`
			
In the Eureka Server (Service discovery), we can see our three micro services,the edge server and Turbine server instances.
As per Eureka server configuration file, its running on 8761 port (see `src/main/resources/application.yml` file under EServerTest project).
(http://localhost:8761):
			
![alt tag](https://github.com/suprakashbh/microservices/blob/master/EServer-screenshot.png)

To find out more details (like how many instances, port info, ip address etc) about micro services, use this below eureka rest api  http://localhost:8761/eureka/apps. In this below example you can see details about Forecast microservice:

![alt tag](https://github.com/suprakashbh/microservices/blob/master/EServer-apps.png)
			

Now we will test our microservices by hit directly and via Edge server. By above `/eureka/apps` URL you will get all the individual microservice port and ip address and also Edge server ip address.

When I was doing testing, Forecast microservice was running in below address :
http://localhost:53092/forecast/mumbai (forecast for mumbai city)

![alt tag](https://github.com/suprakashbh/microservices/blob/master/ms-forecast.png)

The URL address for Weather microservice :
http://localhost:53110/weather/pune (weather details for pune city)

![alt tag](https://github.com/suprakashbh/microservices/blob/master/ms-weather.png)

And the last composite one, which combine the details of Forecast and weather (used ribbon load balancer and circuit breaker) runs on this below port :
http://localhost:60980/weathercomposite/mumbai

![alt tag](https://github.com/suprakashbh/microservices/blob/master/ms-composite-zuul.png)

Now in the above example we hit those microservices directly. But outside world will come via Edge Server or Gatekeepr. Now in this example, Zuul Edge Server is configured to run on 8777 port and only allow weather composite service to access. To access the weather composite service, the URL is very much similar like above, only you have to use port 8777.

http://localhost:8777/weathercomposite/mumbai  (via gate keepr Zuul Edge Server)


In this example we also used Netflix Hystrix Circuit Breaker.If a microservice service doesn’t respond due to error , Hystrix Circuit breaker can redirect the call to an internal fallback method. In this example Weather composite microservice calls weather and forecast service with circuit breaker and fallback method. See this class `com.infy.ms.client.weather.WeatherServiceIntegration` for more details.
If a service repeatedly fails to respond, Hystrix will open the circuit and will call fallback method without even try to call the service on every subsequent call until the service is available again. To determine wether the service is available again Hystrix allow some requests to try out the service even if the circuit is open.

Now this circuit breaker which are in composite service provides information to Turbine Server via Rabit MQ. Netflix Dashboard will use this information to provide graphical presentation. This informations are very useful to monitor the individual microservices. Without RabitMQ , Turbine will not work and its compatible with Java 8 only.

Go to the url http://localhost:7979 (as Netflix Dashboard configured to run on 7979 port, see application.yml file) in a web browser, enter the url http://localhost:8989/turbine/turbine.stream (turbine configured to run on 8989) and click on the “Monitor Stream” – button):

![alt tag](https://github.com/suprakashbh/microservices/blob/master/Netflix-Dashboard-apps.png)


###  And Dashboard and Turbine in Action :

![alt tag](https://github.com/suprakashbh/microservices/blob/master/Netflix-Dashboard-Turbine-apps.png)




		
