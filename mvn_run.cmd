call javac -version
call mvnw clean package -D skipTests exec:java
pause