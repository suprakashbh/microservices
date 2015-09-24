pushd WeatherMicroService &                call mvn clean install & popd
pushd ForecastMicroService &         	   call mvn clean install & popd
pushd WeatherMCompositeService &          call mvn clean install & popd


pushd EServerTest &            				call mvn clean install & popd
pushd ZuulServer &                 		call mvn clean install & popd
pushd HystrixDashboard &                 		call mvn clean install & popd