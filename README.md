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

▶️ Lancer l’application
1️⃣ Cloner le projet
git clone https://github.com/your-username/jurist-backend.git
cd jurist-backend

2️⃣ Compiler et exécuter
mvn clean install
mvn spring-boot:run


Le backend sera disponible sur :
👉 http://localhost:8080



🧑‍💻 Auteur

👩‍💻 Ranim Abassi
Développeuse Full Stack — Java | Spring Boot | Angular | Docker
📍 Tunisie
