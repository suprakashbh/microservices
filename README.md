In this Microservice POC, I used Spring Boot and Netflix OSS to implement microservice and try to cover few core parts of microservice architecture
- Service Discovery
- Routing
- Load balancer 
- Gate keeper

Add image tag : System Landscape of microservices :

![alt tag](https://raw.github.com/username/projectname/branch/path/to/img.png)

It contains three core microservices, weather, forecast and the other one which composite of weather and forecast services, called weather composite Microservice. And to support the microsrevice landscape, I used this below Netflix Infrastructure services and components :

- Netflix Eureka : Service Discovery Server
- Netflix Ribbon : Dynamic Routing and Load Balancer
- Netflix Zuul : Edge Server / Gate keeper 
- Turbine and Hystrix Dashboard : Hystrix
- Hystrix : Circuit breaker


1. Source Code Walk through :
As you can see in github, this POC have have 7 components (3 microservice and 4 infrastructure components) and each of them is build separately, so each component have their own pom.xml. This poc is also include a batch script (for windows), build-all.bat file to build all the component. 

Each Microservice (Weather, Forecast and Weather Composite) is developed by using Spring Boot application and uses Tomcat Server.
Spring Rest Template is used to make outgoing rest api call and Spring MVC  is used to expose service as a Rest call.So this microservices are nothing but a Spring MVC application from which rest get exposed and wrapped by Spring Boot.

Now , lets concentrate on how to use Spring Cloud and Netflix OSS to build the microservice landscape.

1.1 POM Dependency :
		Its very easy to use Eureka, Ribbon, Zuul and Hystrix Dashboard in Spring clod by using Pom. Just add this below starter dependency , it will bring 
        all the necessary dependencies.

			- to use Eureka (client) and Ribbon (Load Balancer) in a microservice, just add this below dependency in your microservice pom.xml file. See in Weather,Forecast and WeatherComposite microservice pom.xml.
			
			` <dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-starter-eureka</artifactId>
				<version>1.0.3.RELEASE</version>
			</dependency>`
			
			- To set up Eureka server, just add this below dependency in pom.xml. See EServerTest\pom.xml

			` <dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-starter-eureka-server</artifactId>
				<version>1.0.3.RELEASE</version>
			</dependency>`
			
			- In WeatherComposite microservice have Hystrix as circuit breaker. To use Hystrix in your project, need to add this below dependency :
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
			
			As Hystrix use RabitMQ to communicate between Circuit breaker and Dashboard (monitoring application), that why you see  dependency for RabitMQ as well along with Hystrix.
			- To set up Turbine server, you need below dependency and minimum java 8
			` 
			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-starter-turbine-amqp</artifactId>
				<version>1.0.0.RELEASE</version>
			</dependency> `
			
			< TO DO >
1.2 Severs :
		Developed and configure this Infrastructure servers by using Spring Cloud and Netflix OSS is super easy.
		Eureka Server :
			To create a Eureka Server Spring boot application just create a class like this below :
			` 
			@SpringBootApplication
			@EnableEurekaServer
			public class EServer {
				public static void main(String[] args) {
				SpringApplication.run(EServer.class, args);
				}		
			}`		
			For complete and configuration see EServerTest project.
		Zuul Server :
			To set up a Zuul server , create a java class with @EnableZuulProxy annotation 
			` @SpringBootApplication
			  @Controller
			  @EnableZuulProxy
			  public class ZuulServerApplication {
				public static void main(String[] args) {
					new SpringApplicationBuilder(ZuulServerApplication.class).web(true).run(args);
				}
			}`
			
			Zuul work as a gatekeepr and with no configuration / default configuration, it will allow / route to every service registered with Eureka.
			In this POC we have configured Zuul (in application.yml) to allow to route to only WeatherComposite microservice, see below.
			
			` zuul:
				ignoredServices: "*"
				routes:
				weathercompositems:
				path: /weathercomposite/**
				`
			
			For detail configuration see ZuulServer project.
			
		Turbine Server :
			Turbine provides information to Hystrix Dashboard from all Hystrix Circuit Breaker and Dashboard use this information to provide graphical representation.
			To setup turbine, add @EnableTurbineAmqp annotation :
			
			` @SpringBootApplication
				@EnableTurbineAmqp
				@EnableDiscoveryClient
				public class TurbineServerApp {

					public static void main(String[] args) {
						new SpringApplicationBuilder(TurbineServerApp.class).run(args);
					}
				}`
		
1.3 Registered Other Component with Eureka Server
		
		To registered microservice or other component with Eureka server, just add @EnableDiscoveryClient to the Spring Boot application.
		` 
		@SpringBootApplication
		@EnableCircuitBreaker
		@EnableDiscoveryClient
		public class WeatherServiceClientApplication {
			public static void main(String[] args) {
				SpringApplication.run(WeatherServiceClientApplication.class, args);
			}
		} `
		For detail example see Weather, Forecast and WeatherComposite microservice Project.
		
1.4 Use load balancer Ribbon from Microservice
		
		In Netflix OSS microservice architecture , its the responsibility of client / consumer to do the load balancing. To look up and call any 
		microservice use Ribbon component, see below and for full example see WeatherComposite Microservice.
		` 
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
2. Start the Microservice Landscape :

		To run all the component of Microservice landscape, there is a batch script start-all.bat. To run the all the component individually, follow this below steps :
		
		- To start the servers (Eureka and Zuul), execute this below 
				microservices\EServerTest\java -jar target/NetflixEurekaServer-1.0.jar
				microservices\ZuulServer\java -jar target/NetflixZuulServer-1.0.jar
				microservices\NetflixTurbineServer\java -jar target/NetflixTurbineServer-1.0.jar
				microservices\HystrixDashboard\java -jar target/HystrixDashboard-1.0.jar
				
			When the above server are started, launch the three microservices one by one :	
				microservices\WeatherMicroService\java -jar target/WeatherMicroService-1.0.jar
				microservices\ForecastMicroService\java -jar target/ForecastMicroService-1.0.jar
				microservices\WeatherMCompositeService\java -jar target/WeatherMCompositeService-1.0.jar
			
			Eureka server image with services + 2 instance image
			
			![alt tag](https://raw.github.com/username/projectname/branch/path/to/img.png)

			
			
			Hystrix Dashboard and Turbine Monitoring screen image :
			
			enter url + image
			
			call microservice individually 
			
			call composite service via Zuul
