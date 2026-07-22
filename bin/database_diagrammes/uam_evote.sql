-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : localhost
-- Généré le : ven. 17 juil. 2026 à 02:23
-- Version du serveur : 10.4.32-MariaDB
-- Version de PHP : 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `uam_evote`
--
DROP DATABASE IF EXISTS `uam_evote`;
CREATE DATABASE IF NOT EXISTS `uam_evote` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `uam_evote`;

-- --------------------------------------------------------

--
-- Structure de la table `ufr`
--

CREATE TABLE `ufr` (
  `id` int(11) NOT NULL,
  `nom` enum('POLYTECHNIQUE','UFR SEG','UFR STA','UFR TECNA') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Structure de la table `departements`
--

CREATE TABLE `departements` (
  `id` int(11) NOT NULL,
  `ufr_id` int(11) NOT NULL,
  `nom` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Structure de la table `filieres`
--

CREATE TABLE `filieres` (
  `id` int(11) NOT NULL,
  `departement_id` int(11) NOT NULL,
  `nom` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Structure de la table `users`
-- ✅ CORRIGÉ : code_permanent en INT(6) pour 6 chiffres
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `code_permanent` INT(6) NOT NULL,  -- ✅ Changé de BIGINT(20) à INT(6)
  `nom` varchar(50) NOT NULL,
  `prenom` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `profession` enum('ETUDIANT','ENSEIGNANT','ADMIN') NOT NULL DEFAULT 'ETUDIANT',
  `filiere_id` int(11) DEFAULT NULL,
  `niveau` enum('L1','L2','L3','M1','M2') DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Structure de la table `admin`
-- ✅ CORRIGÉ : code_permanent en INT(6) pour 6 chiffres
--

CREATE TABLE `admin` (
  `id` int(11) NOT NULL,
  `code_permanent` INT(6) NOT NULL,  -- ✅ Changé de BIGINT(20) à INT(6)
  `nom` varchar(50) NOT NULL,
  `prenom` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `enseignant_filieres`
--

CREATE TABLE `enseignant_filieres` (
  `enseignant_id` int(11) NOT NULL,
  `filiere_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Structure de la table `elections`
--

CREATE TABLE `elections` (
  `id` int(11) NOT NULL,
  `titre` varchar(200) NOT NULL,
  `type_election` varchar(200) NOT NULL,
  `date_debut` datetime NOT NULL,
  `date_fin` datetime NOT NULL,
  `statut` enum('En préparation','Ouverte','Fermée') NOT NULL DEFAULT 'En préparation',
  `cible_ufr_id` int(11) DEFAULT NULL,
  `cible_departement_id` int(11) DEFAULT NULL,
  `cible_filiere_id` int(11) DEFAULT NULL,
  `cible_niveau` enum('L1','L2','L3','M1','M2') DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Structure de la table `candidats`
--

CREATE TABLE `candidats` (
  `id` int(11) NOT NULL,
  `election_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `programme` text NOT NULL,
  `photo` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Structure de la table `votes`
--

CREATE TABLE `votes` (
  `id` int(11) NOT NULL,
  `election_id` int(11) NOT NULL,
  `candidat_id` int(11) NOT NULL,
  `utilisateur_id` int(11) NOT NULL,
  `date_vote` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `ufr`
--
ALTER TABLE `ufr`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `nom` (`nom`);

--
-- Index pour la table `departements`
--
ALTER TABLE `departements`
  ADD PRIMARY KEY (`id`),
  ADD KEY `ufr_id` (`ufr_id`);

--
-- Index pour la table `filieres`
--
ALTER TABLE `filieres`
  ADD PRIMARY KEY (`id`),
  ADD KEY `departement_id` (`departement_id`);

--
-- Index pour la table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `code_permanent` (`code_permanent`),
  ADD UNIQUE KEY `email` (`email`),
  ADD KEY `filiere_id` (`filiere_id`);

--
-- Index pour la table `admin`
--
ALTER TABLE `admin`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`),
  ADD UNIQUE KEY `code_permanent` (`code_permanent`);

--
-- Index pour la table `enseignant_filieres`
--
ALTER TABLE `enseignant_filieres`
  ADD PRIMARY KEY (`enseignant_id`,`filiere_id`),
  ADD KEY `filiere_id` (`filiere_id`);

--
-- Index pour la table `elections`
--
ALTER TABLE `elections`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_elections_statut` (`statut`),
  ADD KEY `cible_ufr_id` (`cible_ufr_id`),
  ADD KEY `cible_departement_id` (`cible_departement_id`),
  ADD KEY `cible_filiere_id` (`cible_filiere_id`);

--
-- Index pour la table `candidats`
--
ALTER TABLE `candidats`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `election_id` (`election_id`,`user_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Index pour la table `votes`
--
ALTER TABLE `votes`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `election_id` (`election_id`,`utilisateur_id`),
  ADD KEY `candidat_id` (`candidat_id`),
  ADD KEY `utilisateur_id` (`utilisateur_id`),
  ADD KEY `idx_votes_election` (`election_id`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `ufr`
--
ALTER TABLE `ufr`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `departements`
--
ALTER TABLE `departements`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `filieres`
--
ALTER TABLE `filieres`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `admin`
--
ALTER TABLE `admin`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `elections`
--
ALTER TABLE `elections`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `candidats`
--
ALTER TABLE `candidats`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `votes`
--
ALTER TABLE `votes`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `departements`
--
ALTER TABLE `departements`
  ADD CONSTRAINT `contenir` FOREIGN KEY (`ufr_id`) REFERENCES `ufr` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `filieres`
--
ALTER TABLE `filieres`
  ADD CONSTRAINT `appartenir` FOREIGN KEY (`departement_id`) REFERENCES `departements` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `users`
--
ALTER TABLE `users`
  ADD CONSTRAINT `fk_users_filier` FOREIGN KEY (`filiere_id`) REFERENCES `filieres` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Contraintes pour la table `enseignant_filieres`
--
ALTER TABLE `enseignant_filieres`
  ADD CONSTRAINT `enseigner` FOREIGN KEY (`enseignant_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `enseigner_filieres` FOREIGN KEY (`filiere_id`) REFERENCES `filieres` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `elections`
--
ALTER TABLE `elections`
  ADD CONSTRAINT `elections_departement` FOREIGN KEY (`cible_departement_id`) REFERENCES `departements` (`id`) ON DELETE SET NULL,
  ADD CONSTRAINT `elections_filiere` FOREIGN KEY (`cible_filiere_id`) REFERENCES `filieres` (`id`) ON DELETE SET NULL,
  ADD CONSTRAINT `elections_ufr` FOREIGN KEY (`cible_ufr_id`) REFERENCES `ufr` (`id`) ON DELETE SET NULL;

--
-- Contraintes pour la table `candidats`
--
ALTER TABLE `candidats`
  ADD CONSTRAINT `candidater` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `candidater_election` FOREIGN KEY (`election_id`) REFERENCES `elections` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `votes`
--
ALTER TABLE `votes`
  ADD CONSTRAINT `voter` FOREIGN KEY (`candidat_id`) REFERENCES `candidats` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `voter_candidat` FOREIGN KEY (`utilisateur_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `votes_election` FOREIGN KEY (`election_id`) REFERENCES `elections` (`id`) ON DELETE CASCADE;

-- ============================================================
-- INSERTION DES DONNÉES
-- ============================================================

-- ==========================================
-- 1. INSERTION DES UFR
-- ==========================================
INSERT INTO ufr (nom) VALUES 
('UFR STA'),
('UFR SEG'),
('UFR TECNA'),
('POLYTECHNIQUE');

-- ==========================================
-- 2. INSERTION DES DÉPARTEMENTS
-- ==========================================
INSERT INTO departements (ufr_id, nom) VALUES 
-- UFR STA (id = 1)
(1, 'Mathématiques, Informatique et Modélisation (MIM)'),
(1, 'Sciences de la Matière et de l\'Univers (SMU)'),

-- UFR SEG (id = 2)
(2, 'Économie'),
(2, 'Gestion'),

-- UFR TECNA (id = 3)
(3, 'Communication Numérique et Multimédia'),
(3, 'Infographie et Design Numérique'),
(3, 'Technologies de l\'Audiovisuel et du Spectacle Vivant'),

-- POLYTECHNIQUE (id = 4)
(4, 'Sciences et Techniques de l\'Ingénieur (DSTI)'),
(4, 'Géosciences Appliquées et Environnement (DGAE)'),
(4, 'Sciences et Techniques Agricoles, Alimentaires et Nutritionnelles (DST2AN)'),
(4, 'Urbanisme, Architecture et Aménagement Durable des Territoires (DU2ADT)'),
(4, 'Gestion des Organisations (DGO)');

-- ==========================================
-- 3. INSERTION DES FILIÈRES
-- ==========================================
INSERT INTO filieres (departement_id, nom) VALUES 
-- Département MIM (id = 1)
(1, 'Mathématiques, Physique et Informatique (MPI)'),
(1, 'Mathématiques Appliquées aux Sciences Sociales (MASS)'),
(1, 'Sciences de la Mer et du Littoral (SML)'),

-- Département SMU (id = 2)
(2, 'Sciences de la Matière et de l\'Univers (SMU)'),

-- Département Économie (id = 3)
(3, 'Économie'),

-- Département Gestion (id = 4)
(4, 'Gestion'),

-- Département Communication Numérique (id = 5)
(5, 'Communication Numérique et Multimédia'),

-- Département Infographie (id = 6)
(6, 'Infographie et Design Numérique'),

-- Département Audiovisuel (id = 7)
(7, 'Technologies de l\'Audiovisuel et du Spectacle Vivant'),

-- Département DSTI (id = 8)
(8, 'Génie des Procédés'),
(8, 'Infrastructures et Génie Civil'),
(8, 'Ingénierie Informatique'),
(8, 'Électronique et Télécommunications'),
(8, 'Systèmes Électriques et Énergétiques'),

-- Département DGAE (id = 9)
(9, 'Mines et Géologie'),
(9, 'Environnement'),
(9, 'Hydraulique et Assainissement'),
(9, 'Géomatique'),

-- Département DST2AN (id = 10)
(10, 'Agroécologie'),
(10, 'Productions Végétales'),
(10, 'Productions Animales'),
(10, 'Technologies Agroalimentaires'),

-- Département DU2ADT (id = 11)
(11, 'Urbanisme et Aménagement'),
(11, 'Architecture'),

-- Département DGO (id = 12)
(12, 'Finance et Comptabilité'),
(12, 'Management des Organisations');

-- ==========================================
-- 4. INSERTION DES UTILISATEURS (ÉTUDIANTS)
-- Codes permanents : 6 chiffres entre 100000 et 999999
-- ==========================================
INSERT INTO users (code_permanent, nom, prenom, email, password, profession, filiere_id, niveau) VALUES 
-- UFR STA - MIM (filiere_id 1, 2, 3)
(123456, 'DIALLO', 'Mamadou', 'mamadou.diallo@uam.edu.sn', NULL, 'ETUDIANT', 1, 'L1'),
(123457, 'NDIAYE', 'Aissatou', 'aissatou.ndiaye@uam.edu.sn', SHA2('MonMotDePasse123', 256), 'ETUDIANT', 2, 'M2'),
(234567, 'BA', 'Oumar', 'oumar.ba@uam.edu.sn', NULL, 'ETUDIANT', 3, 'L3'),

-- UFR STA - SMU (filiere_id 4)
(234568, 'SOW', 'Fatou', 'fatou.sow@uam.edu.sn', SHA2('Fatou2026', 256), 'ETUDIANT', 4, 'M1'),

-- UFR SEG - Économie (filiere_id 5)
(345678, 'GUEYE', 'Aminata', 'aminata.gueye@uam.edu.sn', SHA2('Aminata123', 256), 'ETUDIANT', 5, 'L2'),

-- UFR SEG - Gestion (filiere_id 6)
(345679, 'SY', 'Moussa', 'moussa.sy@uam.edu.sn', NULL, 'ETUDIANT', 6, 'L3'),

-- UFR TECNA - Communication (filiere_id 7)
(456789, 'DIOP', 'Khadija', 'khadija.diop@uam.edu.sn', SHA2('Khadija2026', 256), 'ETUDIANT', 7, 'M1'),

-- UFR TECNA - Infographie (filiere_id 8)
(456790, 'FALL', 'Papa', 'papa.fall@uam.edu.sn', NULL, 'ETUDIANT', 8, 'L1'),

-- UFR TECNA - Audiovisuel (filiere_id 9)
(567890, 'SECK', 'Mariama', 'mariama.seck@uam.edu.sn', SHA2('Mariama2026', 256), 'ETUDIANT', 9, 'M2'),

-- POLYTECHNIQUE - DSTI (filiere_id 10, 11, 12, 13, 14)
(567891, 'NDOUR', 'Amadou', 'amadou.ndour@uam.edu.sn', NULL, 'ETUDIANT', 10, 'L3'),
(678901, 'DIAGNE', 'Fatoumata', 'fatoumata.diagne@uam.edu.sn', SHA2('Fatoumata2026', 256), 'ETUDIANT', 11, 'M1'),
(678902, 'SARR', 'Modou', 'modou.sarr@uam.edu.sn', NULL, 'ETUDIANT', 12, 'L2'),
(789012, 'NDIAYE', 'Khadim', 'khadim.ndiaye@uam.edu.sn', SHA2('Khadim2026', 256), 'ETUDIANT', 13, 'L3'),
(789013, 'FALL', 'Awa', 'awa.fall@uam.edu.sn', NULL, 'ETUDIANT', 14, 'M1'),

-- POLYTECHNIQUE - DGAE (filiere_id 15, 16, 17, 18)
(890123, 'SOW', 'Mamadou', 'mamadou.sow@uam.edu.sn', SHA2('Mamadou2026', 256), 'ETUDIANT', 15, 'L2'),
(890124, 'GUEYE', 'Fatou', 'fatou.gueye@uam.edu.sn', NULL, 'ETUDIANT', 16, 'L3'),
(901234, 'DIOP', 'Moussa', 'moussa.diop@uam.edu.sn', SHA2('Moussa2026', 256), 'ETUDIANT', 17, 'M2'),
(901235, 'SY', 'Khadija', 'khadija.sy@uam.edu.sn', NULL, 'ETUDIANT', 18, 'L1'),

-- POLYTECHNIQUE - DST2AN (filiere_id 19, 20, 21, 22)
(123458, 'FALL', 'Ibrahima', 'ibrahima.fall@uam.edu.sn', SHA2('Ibrahima2026', 256), 'ETUDIANT', 19, 'L3'),
(123459, 'NDIAYE', 'Mariama', 'mariama.ndiaye@uam.edu.sn', NULL, 'ETUDIANT', 20, 'M1'),
(234569, 'SARR', 'Aminata', 'aminata.sarr@uam.edu.sn', SHA2('Aminata2026', 256), 'ETUDIANT', 21, 'L2'),
(234570, 'DIAGNE', 'Papa', 'papa.diagne@uam.edu.sn', NULL, 'ETUDIANT', 22, 'L3'),

-- POLYTECHNIQUE - DU2ADT (filiere_id 23, 24)
(345680, 'SECK', 'Mamadou', 'mamadou.seck@uam.edu.sn', SHA2('MamadouSeck2026', 256), 'ETUDIANT', 23, 'M1'),
(345681, 'NDOUR', 'Awa', 'awa.ndour@uam.edu.sn', NULL, 'ETUDIANT', 24, 'L2'),

-- POLYTECHNIQUE - DGO (filiere_id 25, 26)
(456791, 'DIALLO', 'Moussa', 'moussa.diallo@uam.edu.sn', SHA2('MoussaDiallo2026', 256), 'ETUDIANT', 25, 'L3'),
(456792, 'BA', 'Fatoumata', 'fatoumata.ba@uam.edu.sn', NULL, 'ETUDIANT', 26, 'M1');

-- ==========================================
-- 5. INSERTION DES ENSEIGNANTS
-- Codes permanents : 6 chiffres entre 100000 et 999999
-- ==========================================
INSERT INTO users (code_permanent, nom, prenom, email, password, profession, filiere_id, niveau) VALUES 
-- Enseignants UFR STA
(234571, 'SECK', 'Ibrahima', 'ibrahima.seck.prof@uam.edu.sn', SHA2('Enseignant2026', 256), 'ENSEIGNANT', NULL, NULL),
(345682, 'NDIAYE', 'Mariama', 'mariama.ndiaye.prof@uam.edu.sn', SHA2('Prof2026', 256), 'ENSEIGNANT', NULL, NULL),

-- Enseignants UFR SEG
(456793, 'SOW', 'Amadou', 'amadou.sow.prof@uam.edu.sn', SHA2('SowProf', 256), 'ENSEIGNANT', NULL, NULL),
(567892, 'GUEYE', 'Fatou', 'fatou.gueye.prof@uam.edu.sn', SHA2('GueyeProf', 256), 'ENSEIGNANT', NULL, NULL),

-- Enseignants UFR TECNA
(678903, 'DIOP', 'Moussa', 'moussa.diop.prof@uam.edu.sn', SHA2('DiopProf', 256), 'ENSEIGNANT', NULL, NULL),
(789014, 'SY', 'Khadija', 'khadija.sy.prof@uam.edu.sn', SHA2('SyProf', 256), 'ENSEIGNANT', NULL, NULL),

-- Enseignants POLYTECHNIQUE
(890125, 'FALL', 'Ibrahima', 'ibrahima.fall.prof@uam.edu.sn', SHA2('FallProf', 256), 'ENSEIGNANT', NULL, NULL),
(901236, 'NDIAYE', 'Moussa', 'moussa.ndiaye.prof@uam.edu.sn', SHA2('NdiayeProf', 256), 'ENSEIGNANT', NULL, NULL),
(123460, 'SARR', 'Aminata', 'aminata.sarr.prof@uam.edu.sn', SHA2('SarrProf', 256), 'ENSEIGNANT', NULL, NULL),
(234572, 'DIAGNE', 'Papa', 'papa.diagne.prof@uam.edu.sn', SHA2('DiagneProf', 256), 'ENSEIGNANT', NULL, NULL);

-- ==========================================
-- 6. INSERTION DES ADMINISTRATEURS
-- Codes permanents : 6 chiffres entre 100000 et 999999
-- ==========================================
INSERT INTO admin (code_permanent, nom, prenom, email, password) VALUES 
(345683, 'FALL', 'Moustapha', 'moustapha.fall@uam.edu.sn', SHA2('AdminSecure2026', 256)),
(456794, 'DIALLO', 'Aminata', 'aminata.diallo@uam.edu.sn', SHA2('AdminPass123', 256)),
(567893, 'SOW', 'Mamadou', 'mamadou.sow@uam.edu.sn', SHA2('AdminUAM2026', 256));

-- ==========================================
-- 7. INSERTION DES ENSEIGNANTS DANS LA TABLE enseignant_filieres
-- ==========================================
INSERT INTO enseignant_filieres (enseignant_id, filiere_id) VALUES
-- SECK Ibrahima enseigne en MPI et MASS
(27, 1),
(27, 2),

-- NDIAYE Mariama enseigne en SMU
(28, 4),

-- SOW Amadou enseigne en Économie
(29, 5),

-- GUEYE Fatou enseigne en Gestion
(30, 6),

-- DIOP Moussa enseigne en Communication Numérique
(31, 7),

-- SY Khadija enseigne en Infographie
(32, 8),

-- FALL Ibrahima enseigne en Génie des Procédés et Ingénierie Informatique
(33, 10),
(33, 12),

-- NDIAYE Moussa enseigne en Mines et Géologie
(34, 15),

-- SARR Aminata enseigne en Agroécologie
(35, 19),

-- DIAGNE Papa enseigne en Finance et Comptabilité
(36, 25);

-- ==========================================
-- 8. INSERTION D'UNE ÉLECTION EXEMPLE
-- ==========================================
INSERT INTO elections (titre, type_election, date_debut, date_fin, statut, cible_ufr_id, cible_departement_id, cible_filiere_id, cible_niveau) VALUES 
('Élection des représentants des étudiants - 2026', 'Représentants étudiants', 
 '2026-09-01 08:00:00', '2026-09-30 18:00:00', 'En préparation', 
 NULL, NULL, NULL, NULL);

-- ==========================================
-- 9. INSERTION DE CANDIDATS EXEMPLE
-- ==========================================
INSERT INTO candidats (election_id, user_id, programme, photo) VALUES 
(1, 1, 'Programme de Mamadou DIALLO - Promouvoir l\'excellence académique et le bien-être étudiant', 'mamadou_diallo.jpg'),
(1, 4, 'Programme de Fatou SOW - Pour une université plus inclusive et innovante', 'fatou_sow.jpg'),
(1, 7, 'Programme de Khadija DIOP - Engagement pour la réussite de tous les étudiants', 'khadija_diop.jpg');

-- ==========================================
-- 10. INSERTION D'UN VOTE EXEMPLE
-- ==========================================
INSERT INTO votes (election_id, candidat_id, utilisateur_id, date_vote) VALUES 
(1, 1, 3, NOW());

COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;