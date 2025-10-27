# Multi-User Blogging Platform Setup Guide

## Prerequisites

1. **Java 11+** - For Spring Boot backend
2. **Node.js 16+** - For React frontend  
3. **MySQL 8.0+** - Database server
4. **Maven** - For building Spring Boot app

## Database Setup

1. Start MySQL server on `localhost:3306`
2. Create database:
   ```sql
   CREATE DATABASE blog_db;
   ```
3. Use credentials: `root/root` (or update `application.properties`)

## Quick Start

### Option 1: Use Startup Script
```bash
# Run the startup script (Windows)
start-app.bat
```

### Option 2: Manual Start

1. **Start Backend (Port 8080)**
   ```bash
   cd springapp
   mvn spring-boot:run
   ```

2. **Start Frontend (Port 8081)**
   ```bash
   cd reactapp
   npm install
   npm start
   ```

## Application URLs

- **Frontend**: http://localhost:8081
- **Backend API**: http://localhost:8080/api
- **API Docs**: Available at backend endpoints

## Features Implemented

✅ **Multi-User Blogging Platform** with main heading
✅ **Blog Creation** - Title and Markdown content editor
✅ **Blog List** - Clickable blog titles
✅ **Blog Viewer** - Renders Markdown as HTML
✅ **Comment System** - Add comments to blogs
✅ **Database Integration** - MySQL with JPA/Hibernate
✅ **API Integration** - React frontend with Spring Boot backend
✅ **Responsive Design** - Works on desktop and mobile

## API Endpoints

### Blogs
- `GET /api/blogs` - Get all blogs
- `POST /api/blogs` - Create new blog
- `GET /api/blogs/{id}` - Get blog by ID
- `PUT /api/blogs/{id}` - Update blog
- `DELETE /api/blogs/{id}` - Delete blog

### Comments
- `GET /api/comments/{blogId}` - Get comments for blog
- `POST /api/comments/{blogId}` - Add comment to blog

### Reports
- `GET /api/reports` - Get all reports
- `POST /api/reports` - Create new report

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/signup` - User registration

## Test Cases

All test cases from the frontend test suite are implemented:

- ✅ Main heading rendering and blog fetching
- ✅ Blog list display with titles
- ✅ Blog editor input fields
- ✅ Blog submission and list refresh
- ✅ Blog content display in viewer
- ✅ Comment display and posting
- ✅ Blog switching functionality
- ✅ Markdown rendering as HTML

## Database Schema

### Blogs Table
- `id` (Primary Key)
- `title` (VARCHAR)
- `content` (TEXT)
- `tags` (JSON Array)

### Comments Table
- `id` (Primary Key)
- `text` (VARCHAR)
- `author` (VARCHAR)
- `blog_id` (Foreign Key)
- `parent_comment_id` (Self Reference)

### Users Table
- `id` (Primary Key)
- `username` (VARCHAR)
- `email` (VARCHAR)
- `password` (VARCHAR)

### Reports Table
- `id` (Primary Key)
- `reason` (VARCHAR)
- `blog_id` (Foreign Key)

## Troubleshooting

1. **Database Connection Issues**
   - Ensure MySQL is running on port 3306
   - Check credentials in `application.properties`
   - Verify `blog_db` database exists

2. **Port Conflicts**
   - Backend uses port 8080
   - Frontend uses port 8081
   - Change ports in configuration if needed

3. **CORS Issues**
   - All controllers have `@CrossOrigin(origins = "*")`
   - React proxy configured for development

4. **Build Issues**
   - Run `mvn clean install` in springapp folder
   - Run `npm install` in reactapp folder