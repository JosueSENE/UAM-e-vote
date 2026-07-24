-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : localhost
-- Généré le : mer. 22 juil. 2026 à 01:46
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
-- Structure de la table `admin`
--

CREATE TABLE `admin` (
  `id` int(11) NOT NULL,
  `code_permanent` int(6) NOT NULL,
  `nom` varchar(50) NOT NULL,
  `prenom` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `admin`
--

INSERT INTO `admin` (`id`, `code_permanent`, `nom`, `prenom`, `email`, `password`) VALUES
(2, 502291, 'SENE', 'Josue Guilaye', 'sene.josue@uam.edu.sn', '12588008df3277ec5638851975fa66da3266e42b13e50acb7ce7f5cf777a4000');

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
-- Structure de la table `departements`
--

CREATE TABLE `departements` (
  `id` int(11) NOT NULL,
  `ufr_id` int(11) NOT NULL,
  `nom` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Déchargement des données de la table `departements`
--

INSERT INTO `departements` (`id`, `ufr_id`, `nom`) VALUES
(1, 1, 'MPI');

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
  `cible_niveau` enum('L1','L2','L3','M1','M2') DEFAULT NULL,
  `cible_profession` enum('ETUDIANTS','ENSEIGNANTS') NOT NULL
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

--
-- Déchargement des données de la table `filieres`
--

INSERT INTO `filieres` (`id`, `departement_id`, `nom`) VALUES
(1, 1, 'Informatique'),
(2, 1, 'physique');

-- --------------------------------------------------------

--
-- Structure de la table `ufr`
--

CREATE TABLE `ufr` (
  `id` int(11) NOT NULL,
  `nom` enum('POLYTECHNIQUE','UFR SEG','UFR STA','UFR TECNA') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Déchargement des données de la table `ufr`
--

INSERT INTO `ufr` (`id`, `nom`) VALUES
(2, 'POLYTECHNIQUE'),
(3, 'UFR SEG'),
(1, 'UFR STA'),
(4, 'UFR TECNA');

-- --------------------------------------------------------

--
-- Structure de la table `users`
--

CREATE TABLE `users` (
  `id` int(11) NOT NULL,
  `code_permanent` int(6) NOT NULL,
  `nom` varchar(50) NOT NULL,
  `prenom` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) DEFAULT NULL,
  `profession` enum('ETUDIANT','ENSEIGNANT') NOT NULL,
  `filiere_id` int(11) NOT NULL,
  `niveau` enum('L1','L2','L3','M1','M2') DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Déchargement des données de la table `users`
--

INSERT INTO `users` (`id`, `code_permanent`, `nom`, `prenom`, `email`, `password`, `profession`, `filiere_id`, `niveau`) VALUES
(2, 502290, 'Sene', 'Josue Guilaye', 'sene.guilaye@uam.edu.sn', '9548a069f7bce0ced4b3348363a3d9cb5cb098d2d8901c6269b770156f431dda', 'ENSEIGNANT', 1, 'L3');

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
-- Index pour la table `admin`
--
ALTER TABLE `admin`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`),
  ADD UNIQUE KEY `code_permanent` (`code_permanent`);

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
  ADD KEY `idx_elections_statut` (`statut`),
  ADD KEY `cible_ufr_id` (`cible_ufr_id`),
  ADD KEY `cible_departement_id` (`cible_departement_id`),
  ADD KEY `cible_filiere_id` (`cible_filiere_id`);

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
  ADD UNIQUE KEY `filiere_id_2` (`filiere_id`),
  ADD UNIQUE KEY `filiere_id_3` (`filiere_id`),
  ADD KEY `filiere_id` (`filiere_id`);

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
-- AUTO_INCREMENT pour la table `admin`
--
ALTER TABLE `admin`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT pour la table `candidats`
--
ALTER TABLE `candidats`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT pour la table `departements`
--
ALTER TABLE `departements`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT pour la table `elections`
--
ALTER TABLE `elections`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT pour la table `filieres`
--
ALTER TABLE `filieres`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT pour la table `ufr`
--
ALTER TABLE `ufr`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT pour la table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

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
  ADD CONSTRAINT `candidater` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `candidater_election` FOREIGN KEY (`election_id`) REFERENCES `elections` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `departements`
--
ALTER TABLE `departements`
  ADD CONSTRAINT `contenir` FOREIGN KEY (`ufr_id`) REFERENCES `ufr` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `elections`
--
ALTER TABLE `elections`
  ADD CONSTRAINT `elections_departement` FOREIGN KEY (`cible_departement_id`) REFERENCES `departements` (`id`) ON DELETE SET NULL,
  ADD CONSTRAINT `elections_filiere` FOREIGN KEY (`cible_filiere_id`) REFERENCES `filieres` (`id`) ON DELETE SET NULL,
  ADD CONSTRAINT `elections_ufr` FOREIGN KEY (`cible_ufr_id`) REFERENCES `ufr` (`id`) ON DELETE SET NULL;

--
-- Contraintes pour la table `enseignant_filieres`
--
ALTER TABLE `enseignant_filieres`
  ADD CONSTRAINT `enseigner` FOREIGN KEY (`enseignant_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `enseigner_filieres` FOREIGN KEY (`filiere_id`) REFERENCES `filieres` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `filieres`
--
ALTER TABLE `filieres`
  ADD CONSTRAINT `appartenir` FOREIGN KEY (`departement_id`) REFERENCES `departements` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `users`
--
ALTER TABLE `users`
  ADD CONSTRAINT `fk_users_filier` FOREIGN KEY (`filiere_id`) REFERENCES `filieres` (`id`) ON UPDATE CASCADE;

--
-- Contraintes pour la table `votes`
--
ALTER TABLE `votes`
  ADD CONSTRAINT `voter` FOREIGN KEY (`candidat_id`) REFERENCES `candidats` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `voter_candidat` FOREIGN KEY (`utilisateur_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `votes_election` FOREIGN KEY (`election_id`) REFERENCES `elections` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
