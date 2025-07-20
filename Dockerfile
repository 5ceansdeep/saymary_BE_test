FROM openjdk:17-jdk-slim
# 자바 17, 최소 슬림 이미지

RUN apt-get update && apt-get install -y dos2unix
# 윈도우에서 복사한 gradlew 스크립트 개행문자 수정용

WORKDIR /app
# 작업 디렉토리 설정

COPY gradlew ./
COPY gradle gradle
COPY build.gradle ./
COPY settings.gradle ./
RUN dos2unix gradlew

COPY src src
# 소스코드 복사 (resources 포함)

RUN ./gradlew build --no-daemon -x test
# 빌드 (테스트 제외)

EXPOSE 8080
# Spring Boot 기본 포트

ENTRYPOINT ["java", "-jar", "build/libs/user-0.0.1-SNAPSHOT.jar"]

# 실제 빌드된 jar 파일 이름에 맞게 조정 필요
