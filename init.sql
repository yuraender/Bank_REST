/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- Dumping database structure for bank
CREATE DATABASE IF NOT EXISTS `bank` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;
USE `bank`;

-- Dumping structure for table bank.cards
CREATE TABLE IF NOT EXISTS `cards` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `number` varchar(255) NOT NULL,
  `number_hash` varchar(255) NOT NULL,
  `holder` varchar(100) NOT NULL,
  `expiry_date` date NOT NULL,
  `status` varchar(10) NOT NULL,
  `balance` decimal(19,4) NOT NULL DEFAULT '0.0000',
  `deleted` bit(1) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_cards_user_id` (`user_id`),
  CONSTRAINT `fk_cards_user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Dumping structure for table bank.transactions
CREATE TABLE IF NOT EXISTS `transactions` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `from_id` bigint(20) NOT NULL,
  `to_id` bigint(20) NOT NULL,
  `amount` decimal(19,4) NOT NULL DEFAULT '0.0000',
  `comment` varchar(100) NOT NULL,
  `date` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_transactions_from_id` (`from_id`),
  KEY `fk_transactions_to_id` (`to_id`),
  CONSTRAINT `fk_transactions_from_id` FOREIGN KEY (`from_id`) REFERENCES `cards` (`id`),
  CONSTRAINT `fk_transactions_to_id` FOREIGN KEY (`to_id`) REFERENCES `cards` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4;

-- Dumping structure for table bank.users
CREATE TABLE IF NOT EXISTS `users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` varchar(20) NOT NULL,
  `enabled` bit(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4;

-- Dumping data for table bank.users: ~1 rows (approximately)
DELETE FROM `users`;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` (`id`, `username`, `password`, `role`, `enabled`) VALUES
	(1, 'admin', '$2a$12$GBJock2qGIaOUY2nXPDgUOdXdj9cGW88EQNYbTE.T0y5dFCO8kyXC', 'ADMIN', b'1');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
