# 🪐 Deltaspace - Reddit Clone

```text
██████╗ ███████╗██╗  ████████╗ █████╗ ███████╗██████╗  █████╗  ██████╗███████╗
██╔══██╗██╔════╝██║  ╚══██╔══╝██╔══██╗██╔════╝██╔══██╗██╔══██╗██╔════╝██╔════╝
██║  ██║█████╗  ██║     ██║   ███████║███████╗██████╔╝███████║██║     █████╗  
██║  ██║██╔══╝  ██║     ██║   ██╔══██║╚════██║██╔═══╝ ██╔══██║██║     ██╔══╝  
██████╔╝███████╗███████╗██║   ██║  ██║███████║██║     ██║  ██║╚██████╗███████╗
╚═════╝ ╚══════╝╚══════╝╚═╝   ╚═╝  ╚═╝╚══════╝╚═╝     ╚═╝  ╚═╝ ╚═════╝╚══════╝

✨ Key Features
-> User Authentication & Security: Secure JWT-based authentication. Features strict validation and soft-deletion (tombstoning) to preserve discussion history.

-> Subreddits: Users can create, join, and leave topic-based communities. Includes NSFW tags and membership tracking.

-> Rich Posts & Interactions:
   - Create posts with text and images.
   - Upvote/Downvote scoring system.

-> AWS S3 Integration: Highly scalable image storage.

-> Real-time Image Filters: A dedicated .NET microservice applies stunning visual filters (Grayscale, Invert, Sepia, Neon) to images upon upload.

-> AI Oracle (Groq AI): Automatically generates intelligent TL;DR summaries for long posts using the ultra-fast Groq API.

-> Nested Comments: Hierarchical, threaded comment system allowing users to reply, vote, and soft-delete their thoughts.

🏗️ Architecture & Tech Stack
Deltaspace is divided into three main components:

1)Spring Boot Backend (spring-backend) - Port 8080
-> Language: Java
-> Framework: Spring Boot 3, Spring Security, Hibernate/JPA
-> Database: PostgreSQL
-> Cloud: AWS S3 SDK for image hosting
-> AI: Groq REST API integration

2) Image Filter Microservice (ImageProcessor) - Port 5157
-> Language: C#
-> Framework: ASP.NET Core 9.0
-> Processing Engine: SixLabors.ImageSharp

3) CLI Frontend
-> Language: Java
-> Details: A fully interactive Command-Line Interface (CLI) application acting as the client, communicating with the Spring REST API using Spring's RestClient.

⚙️ Prerequisites
Before you begin, ensure you have the following installed:
-> Java Development Kit (JDK) (Version 21 or newer recommended)
-> .NET 9.0 SDK
-> PostgreSQL (Running on port 5432)
-> Maven

