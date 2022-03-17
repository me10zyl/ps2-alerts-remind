nohup ./mvnw spring-boot:run > out.log 2>&1 &
tail -f out.log
