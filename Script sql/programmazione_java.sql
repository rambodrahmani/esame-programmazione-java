-- phpMyAdmin SQL Dump
-- version 4.2.12deb2+deb8u1
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Jun 12, 2016 at 08:34 AM
-- Server version: 10.0.23-MariaDB-0+deb8u1
-- PHP Version: 5.6.20-0+deb8u1

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `c0programmazione_java`
--

-- --------------------------------------------------------

--
-- Table structure for table `contatti`
--

CREATE TABLE IF NOT EXISTS `contatti` (
  `email` varchar(45) NOT NULL,
  `indirizzo_ip` varchar(45) NOT NULL,
  `data` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `contatti`
--

INSERT INTO `contatti` (`email`, `indirizzo_ip`, `data`) VALUES
('utente11@prova.com', '192.168.0.33', '2016-06-12 06:06:02'),
('utente17@prova.com', '192.168.0.66', '2016-06-12 06:06:02'),
('utente3@prova.com', '192.168.0.19', '2016-06-12 06:06:02'),
('utente5@prova.com', '192.168.0.44', '2016-06-12 06:06:02'),
('utente8@prova.com', '192.168.0.22', '2016-06-12 06:06:02');

-- --------------------------------------------------------

--
-- Table structure for table `messaggi`
--

CREATE TABLE IF NOT EXISTS `messaggi` (
`id` int(11) NOT NULL,
  `mittente` varchar(45) NOT NULL,
  `destinatario` varchar(45) NOT NULL,
  `testo` mediumtext NOT NULL,
  `data` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=latin1;

--
-- Dumping data for table `messaggi`
--

INSERT INTO `messaggi` (`id`, `mittente`, `destinatario`, `testo`, `data`) VALUES
(1, 'utente1@prova.com', 'utente2@prova.com', 'Ciao!', '2016-06-12 06:08:21'),
(2, 'utente2@prova.com', 'utente1@prova.com', 'Ciao utente1!', '2016-06-12 06:08:40'),
(3, 'utente1@prova.com', 'utente2@prova.com', 'Questa e'' una conversazione di prova per il manuale d''uso del progetto,', '2016-06-12 06:09:05'),
(4, 'utente2@prova.com', 'utente1@prova.com', 'Messaggi di prova 1', '2016-06-12 06:09:38'),
(5, 'utente2@prova.com', 'utente1@prova.com', 'Messaggi di prova 2', '2016-06-12 06:09:40'),
(6, 'utente2@prova.com', 'utente1@prova.com', 'Messaggi di prova 3', '2016-06-12 06:09:42'),
(7, 'utente2@prova.com', 'utente1@prova.com', 'Messaggi di prova 4', '2016-06-12 06:09:44'),
(8, 'utente2@prova.com', 'utente1@prova.com', 'Messaggi di prova 5', '2016-06-12 06:09:46'),
(9, 'utente2@prova.com', 'utente1@prova.com', 'Messaggi di prova 6', '2016-06-12 06:09:48'),
(10, 'utente2@prova.com', 'utente1@prova.com', 'Messaggi di prova 7', '2016-06-12 06:09:50'),
(11, 'utente2@prova.com', 'utente1@prova.com', 'Messaggi di prova 8', '2016-06-12 06:09:52'),
(12, 'utente2@prova.com', 'utente1@prova.com', 'Messaggi di prova 9', '2016-06-12 06:09:55'),
(13, 'utente2@prova.com', 'utente1@prova.com', 'Messaggi di prova 10', '2016-06-12 06:09:59'),
(14, 'utente2@prova.com', 'utente1@prova.com', 'Messaggi di prova 11', '2016-06-12 06:10:01');

-- --------------------------------------------------------

--
-- Table structure for table `storico`
--

CREATE TABLE IF NOT EXISTS `storico` (
  `email` varchar(45) NOT NULL,
  `durata` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `storico`
--

INSERT INTO `storico` (`email`, `durata`) VALUES
('utente11@prova.com', 0.047),
('utente1@prova.com', 0.003888888),
('utente20@prova.com', 0.005555555),
('utente2@prova.com', 0.063888888),
('utente3@prova.com', 0.034),
('utente5@prova.com', 0.022),
('utente7@prova.com', 0.005432),
('utente8@prova.com', 0.0032);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `contatti`
--
ALTER TABLE `contatti`
 ADD PRIMARY KEY (`email`);

--
-- Indexes for table `messaggi`
--
ALTER TABLE `messaggi`
 ADD PRIMARY KEY (`id`);

--
-- Indexes for table `storico`
--
ALTER TABLE `storico`
 ADD PRIMARY KEY (`email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `messaggi`
--
ALTER TABLE `messaggi`
MODIFY `id` int(11) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=15;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
