# lawyers
⚖️ Jurist Backend

Backend de l’application Jurist, une plateforme de gestion des avocats, affaires juridiques et administration du cabinet.
Développée avec Spring Boot, Spring Security (JWT), JPA/Hibernate, et PostgreSQL.
Ce backend gère l’authentification des administrateurs, la gestion des avocats, des clients, et le suivi des affaires.

🚀 Fonctionnalités principales
🔐 Authentification & Sécurité

Authentification JWT (JSON Web Token) pour les admins.

Gestion sécurisée des rôles et permissions.

Middleware Spring Security configuré pour autoriser les routes publiques et sécuriser les endpoints internes.

👩‍⚖️ Gestion des Avocats

Création, mise à jour et suppression d’avocats.

Attribution automatique des affaires selon la disponibilité et la charge de travail.

Suivi du nombre d’affaires en cours.

📂 Gestion des Affaires

Création et suivi des affaires juridiques.

Association à un avocat assigné.

Statuts d’avancement : en cours, clôturée, en attente, etc.

📧 Notifications par Email

Notification automatique envoyée à l’avocat lors de l’attribution d’une nouvelle affaire.

Contenu personnalisé de l’email (titre, numéro d’affaire, date d’audience, etc).

👨‍💼 Tableau de bord Admin

Visualisation globale des avocats et affaires.

Recherche et filtrage.

🏗️ Architecture du projet
Jurist-backend/
│
├── src/main/java/com/onat/jurist/lawyer/
│   ├── controller/        # Contrôleurs REST
│   ├── entity/            # Entités JPA
│   ├── repository/        # DAO avec Spring Data JPA
│   ├── service/           # Services métier
│   ├── security/          # Configurations JWT et Spring Security
│   └── dto/               # Objets de transfert de données
│
├── src/main/resources/
│   ├── application.properties  # Configuration de la base de données et du serveur
│   └── templates/ (si emails)
│
└── pom.xml               # Dépendances Maven

⚙️ Prérequis

Avant de démarrer le projet, assure-toi d’avoir :

Java 17+ ou Corretto 21

Maven 3.8+

PostgreSQL (ou une autre base compatible JPA)

Lombok activé dans ton IDE (par exemple IntelliJ → Settings → Plugins → Lombok)

🧩 Configuration
🗂️ application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/juristdb
spring.datasource.username=postgres
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

jwt.secret=your_secret_key
jwt.expiration=86400000

▶️ Lancer l’application
1️⃣ Cloner le projet
git clone https://github.com/your-username/jurist-backend.git
cd jurist-backend

2️⃣ Compiler et exécuter
mvn clean install
mvn spring-boot:run


Le backend sera disponible sur :
👉 http://localhost:8080

🧠 Endpoints principaux
Méthode	Endpoint	Description
POST	/api/auth/login	Authentification de l’admin
GET	/api/avocats	Liste des avocats
POST	/api/affaires	Créer une nouvelle affaire
PUT	/api/affaires/{id}	Modifier une affaire
POST	/api/assign	Assigner automatiquement une affaire à un avocat
GET	/api/notifications	Lister les notifications envoyées
🧰 Dépendances principales

Spring Boot Starter Web

Spring Boot Starter Security

Spring Boot Starter Data JPA

JJWT (io.jsonwebtoken)

PostgreSQL Driver

Lombok

Spring Mail (pour les emails)

🧾 Notes techniques

Les méthodes signWith() et parser() de io.jsonwebtoken sont dépréciées :
utilise plutôt la version moderne avec Jwts.parserBuilder() et signWith(Key, SignatureAlgorithm).

Active Annotation Processing dans ton IDE pour éviter les erreurs Lombok :

IntelliJ : File → Settings → Build, Execution, Deployment → Compiler → Annotation Processors → Enable.

🧑‍💻 Auteur

👩‍💻 Ranim Abassi
Développeuse Full Stack — Java | Spring Boot | Angular | Docker
📍 Tunisie
