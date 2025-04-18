@echo off

echo "Clean Project ..."
call ./mvnw.cmd clean -f pom.xml

echo "Build Project ..."
call ./mvnw.cmd package -f pom.xml

:exit
pause