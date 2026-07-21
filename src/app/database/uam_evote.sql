-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : localhost
-- Généré le : lun. 13 juil. 2026 à 20:19
-- Version du serveur : 10.4.32-MariaDB
-- Version de PHP : 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
DROP DATABASE IF EXISTS `uam_evote`;
CREATE DATABASE IF NOT EXISTS `uam_evote` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `uam_evote`;

--
-- Base de données : `uam_evote`
--

-- --------------------------------------------------------


--
-- Structure de la table `candidats`
--

CREATE TABLE `candidats` (
  `id` int(11) NOT NULL,
  `election_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `programme` text DEFAULT NULL,
  `photo` varchar(255) DEFAULT NULL
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
-- Structure de la table `elections`
--

CREATE TABLE `elections` (
  `id` int(11) NOT NULL,
  `titre` varchar(200) NOT NULL,
  `type_election` enum('DELEGUE','CHEF_DEPARTEMENT','DIRECTEUR_UFR','REPRESENTANT') NOT NULL,
  `date_debut` datetime NOT NULL,
  `date_fin` datetime NOT NULL,
  `statut` enum('En préparation','Ouverte','Fermée') NOT NULL DEFAULT 'En préparation',
  `cible_ufr_id` int(11) DEFAULT NULL,
  `cible_departement_id` int(11) DEFAULT NULL,
  `cible_filiere_id` int(11) DEFAULT NULL,
  `cibe_niveau` enum('L1','L2','L3','M1','M2') DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

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
-- Structure de la table `filieres`
--

CREATE TABLE `filieres` (
  `id` int(11) NOT NULL,
  `departement_id` int(11) NOT NULL,
  `nom` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

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
-- Structure de la table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `code_permanent` int(11) NOT NULL,
  `nom` varchar(50) NOT NULL,
  `prenom` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `login` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('ADMIN','ETUDIANT','ENSEIGNANT') NOT NULL,
  `filiere_id` int(11) DEFAULT NULL,
  `niveau` enum('L1','L2','L3','M1','M2') DEFAULT NULL,
  `ufr_id` int(11) DEFAULT NULL
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
  `date_vote` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `candidats`
--
ALTER TABLE `candidats`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `election_id` (`election_id`,`user_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Index pour la table `departements`
--
ALTER TABLE `departements`
  ADD PRIMARY KEY (`id`),
  ADD KEY `ufr_id` (`ufr_id`);

--
-- Index pour la table `elections`
--
ALTER TABLE `elections`
  ADD PRIMARY KEY (`id`),
  ADD KEY `cible_ufr_id` (`cible_ufr_id`),
  ADD KEY `cible_departement_id` (`cible_departement_id`),
  ADD KEY `cible_filiere_id` (`cible_filiere_id`),
  ADD KEY `idx_elections_statut` (`statut`);

--
-- Index pour la table `enseignant_filieres`
--
ALTER TABLE `enseignant_filieres`
  ADD PRIMARY KEY (`enseignant_id`,`filiere_id`),
  ADD KEY `filiere_id` (`filiere_id`);

--
-- Index pour la table `filieres`
--
ALTER TABLE `filieres`
  ADD PRIMARY KEY (`id`),
  ADD KEY `departement_id` (`departement_id`);

--
-- Index pour la table `ufr`
--
ALTER TABLE `ufr`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `nom` (`nom`);

--
-- Index pour la table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `code_permanent` (`code_permanent`),
  ADD UNIQUE KEY `email` (`email`),
  ADD UNIQUE KEY `login` (`login`),
  ADD UNIQUE KEY `password` (`password`),
  ADD KEY `filiere_id` (`filiere_id`),
  ADD KEY `ufr_id` (`ufr_id`),
  ADD KEY `idx_users_login` (`login`);

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
-- AUTO_INCREMENT pour la table `candidats`
--
ALTER TABLE `candidats`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `departements`
--
ALTER TABLE `departements`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `elections`
--
ALTER TABLE `elections`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `filieres`
--
ALTER TABLE `filieres`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `ufr`
--
ALTER TABLE `ufr`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `users`
--
ALTER TABLE `users`
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
-- Contraintes pour la table `candidats`
--
ALTER TABLE `candidats`
  ADD CONSTRAINT `candidats_ibfk_1` FOREIGN KEY (`election_id`) REFERENCES `elections` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `candidats_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `departements`
--
ALTER TABLE `departements`
  ADD CONSTRAINT `departements_ibfk_1` FOREIGN KEY (`ufr_id`) REFERENCES `ufr` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `elections`
--
ALTER TABLE `elections`
  ADD CONSTRAINT `elections_ibfk_1` FOREIGN KEY (`cible_ufr_id`) REFERENCES `ufr` (`id`) ON DELETE SET NULL,
  ADD CONSTRAINT `elections_ibfk_2` FOREIGN KEY (`cible_departement_id`) REFERENCES `departements` (`id`) ON DELETE SET NULL,
  ADD CONSTRAINT `elections_ibfk_3` FOREIGN KEY (`cible_filiere_id`) REFERENCES `filieres` (`id`) ON DELETE SET NULL;

--
-- Contraintes pour la table `enseignant_filieres`
--
ALTER TABLE `enseignant_filieres`
  ADD CONSTRAINT `enseignant_filieres_ibfk_1` FOREIGN KEY (`enseignant_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `enseignant_filieres_ibfk_2` FOREIGN KEY (`filiere_id`) REFERENCES `filieres` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `filieres`
--
ALTER TABLE `filieres`
  ADD CONSTRAINT `filieres_ibfk_1` FOREIGN KEY (`departement_id`) REFERENCES `departements` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `users`
--
ALTER TABLE `users`
  ADD CONSTRAINT `users_ibfk_1` FOREIGN KEY (`filiere_id`) REFERENCES `filieres` (`id`) ON DELETE SET NULL,
  ADD CONSTRAINT `users_ibfk_2` FOREIGN KEY (`ufr_id`) REFERENCES `ufr` (`id`) ON DELETE SET NULL;

--
-- Contraintes pour la table `votes`
--
ALTER TABLE `votes`
  ADD CONSTRAINT `votes_ibfk_1` FOREIGN KEY (`election_id`) REFERENCES `elections` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `votes_ibfk_2` FOREIGN KEY (`candidat_id`) REFERENCES `candidats` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `votes_ibfk_3` FOREIGN KEY (`utilisateur_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
