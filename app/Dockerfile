FROM eclipse-temurin:21

WORKDIR /app

COPY /app .

RUN gradle installDist

CMD ./build/install/app/bin/app