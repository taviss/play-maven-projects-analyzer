-- MySQL dump 10.13  Distrib 5.7.12, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: svu
-- ------------------------------------------------------
-- Server version	5.7.13-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `sonar_hosts`
--

DROP TABLE IF EXISTS `sonar_hosts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sonar_hosts` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `address` varchar(128) DEFAULT NULL,
  `username` varchar(128) DEFAULT NULL,
  `password` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sonar_hosts`
--

LOCK TABLES `sonar_hosts` WRITE;
/*!40000 ALTER TABLE `sonar_hosts` DISABLE KEYS */;
INSERT INTO `sonar_hosts` VALUES (3,'http://localhost:9005','admin','admin'),(4,'http://127.0.0.1:9005','admin','admin');
/*!40000 ALTER TABLE `sonar_hosts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `uploads`
--

DROP TABLE IF EXISTS `uploads`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `uploads` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) DEFAULT NULL,
  `timestamp` timestamp NULL DEFAULT NULL,
  `path` varchar(128) DEFAULT NULL,
  `project_name` varchar(128) DEFAULT NULL,
  `project_key` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `uploads`
--

LOCK TABLES `uploads` WRITE;
/*!40000 ALTER TABLE `uploads` DISABLE KEYS */;
INSERT INTO `uploads` VALUES (9,'Test Name','2016-09-01 15:13:07','(01-09-2016)(18-13-07)Test_Name-java-maven-simple.zip','Test project for Maven v2','org.test:test-proj-maven'),(10,'Some Test','2016-09-01 15:13:16','(01-09-2016)(18-13-16)Some_Test-java-maven-simple.zip','Test project for Maven v2','org.test:test-proj-maven'),(11,'Abc Test','2016-09-02 06:34:32','(02-09-2016)(09-34-31)Abc_Test-test-project.zip','Test project for Maven v2','org.test:test-proj-maven'),(13,'Abc Test','2016-09-02 06:38:39','(02-09-2016)(09-38-39)Abc_Test-some-project.zip','Some random project','org.test:some-proj-maven');
/*!40000 ALTER TABLE `uploads` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `u_name` varchar(64) DEFAULT NULL,
  `u_pass` varchar(256) DEFAULT NULL,
  `u_mail` varchar(45) DEFAULT NULL,
  `u_token` varchar(45) DEFAULT NULL,
  `u_active` tinyint(2) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (18,'admin','1000>81b6ff00ae45e1647f33089bdadc88475c063e376514f84b>361065b2b4353b2b263b42eddc6060e227e05d30ac80eb14','admin@admin.com','test',1),(19,'test','1000>afd1093940281dbaa88ccbe230b3854c3b5cb638f2a09459>31992d627e89cf6dcd6fe7b6313141ba10afc70305ba0210','test@test.com','0c785acc-dde4-4f71-b5c7-b0e33ebc4124',1),(20,'TestUser','1000>dd9cd4ab41a2b47c4c24fe0e0caab190417e6d75991f1d67>63cae05e077be19a3fb94ef8de4a9a840fb302d938454ffb','abc@abc.com','d72902f8-250b-42d3-92c3-c06b0148adf9',1);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-10-07 10:45:34
