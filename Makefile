.PHONY: clean compile install test start

clean:
	mvn clean
compile:
	mvn clean compile
install:
	mvn clean install -DskipTests
test:
	mvn test
start:
	mvn clean spring-boot:run
