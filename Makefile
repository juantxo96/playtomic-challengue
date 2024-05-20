.PHONY: clean compile install test start

clean:
	mvn clean
compile:
	mvn clean compile
install:
	mvn clean install -DskipTests
test:
	mvn test -Dspring-boot.run.profiles=test
start:
	mvn clean spring-boot:run -Dspring-boot.run.profiles=develop
