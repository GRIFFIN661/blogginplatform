@echo off
echo Starting Blogging Platform...
echo.

echo Starting MySQL Database (ensure MySQL is running on localhost:3306)
echo Database: blog_db
echo Username: root
echo Password: root
echo.

echo Starting Spring Boot Backend (Port 8080)...
cd springapp
start "Backend" cmd /k "mvn spring-boot:run"
cd ..

echo Waiting for backend to start...
timeout /t 10 /nobreak > nul

echo Starting React Frontend (Port 8081)...
cd reactapp
start "Frontend" cmd /k "npm start"
cd ..

echo.
echo Both applications are starting...
echo Backend: http://localhost:8080
echo Frontend: http://localhost:8081
echo.
echo Press any key to exit...
pause > nul