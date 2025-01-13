.DEFAULT_GOAL := build-run

clean:
	./gradlew clean

build:
	./gradlew build

install:
	./gradlew clean install

run:
	./gradlew run

test:
	./gradlew test

report:
	./gradlew jacocoTestReport

lint:
	./gradlew checkstyleMain

lint-tests:
	./gradlew checkstyleTest

install-dist:
	./gradlew installDist

build-run: build run

do-all: clean build report run

do-totalCheckStyle: clean lint lint-tests
.PHONY: build
