-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: localhost    Database: gradedb
-- ------------------------------------------------------
-- Server version	8.0.40

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `academic_year`
--

DROP TABLE IF EXISTS `academic_year`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `academic_year` (
  `id` int NOT NULL AUTO_INCREMENT,
  `year` char(9) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `year` (`year`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `academic_year`
--

LOCK TABLES `academic_year` WRITE;
/*!40000 ALTER TABLE `academic_year` DISABLE KEYS */;
INSERT INTO `academic_year` VALUES (1,'2022-2023');
/*!40000 ALTER TABLE `academic_year` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `classroom`
--

DROP TABLE IF EXISTS `classroom`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `classroom` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `lecturer_id` int NOT NULL,
  `course_id` int NOT NULL,
  `semester_id` int NOT NULL,
  `grade_status` enum('DRAFT','LOCKED') NOT NULL DEFAULT 'DRAFT',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`,`semester_id`,`course_id`),
  KEY `lecturer_id` (`lecturer_id`),
  KEY `course_id` (`course_id`),
  KEY `semester_id` (`semester_id`),
  CONSTRAINT `classroom_ibfk_1` FOREIGN KEY (`lecturer_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `classroom_ibfk_2` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `classroom_ibfk_3` FOREIGN KEY (`semester_id`) REFERENCES `semester` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `classroom`
--

LOCK TABLES `classroom` WRITE;
/*!40000 ALTER TABLE `classroom` DISABLE KEYS */;
INSERT INTO `classroom` VALUES (10,'IT2201PTHTW',21,3,1,'DRAFT');
/*!40000 ALTER TABLE `classroom` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `classroom_student`
--

DROP TABLE IF EXISTS `classroom_student`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `classroom_student` (
  `id` int NOT NULL AUTO_INCREMENT,
  `classroom_id` int NOT NULL,
  `student_id` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `classroom_id` (`classroom_id`,`student_id`),
  KEY `student_id` (`student_id`),
  CONSTRAINT `classroom_student_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`) ON DELETE CASCADE,
  CONSTRAINT `classroom_student_ibfk_2` FOREIGN KEY (`classroom_id`) REFERENCES `classroom` (`id`) ON DELETE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=65 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `classroom_student`
--

LOCK TABLES `classroom_student` WRITE;
/*!40000 ALTER TABLE `classroom_student` DISABLE KEYS */;
/*!40000 ALTER TABLE `classroom_student` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `course`
--

DROP TABLE IF EXISTS `course`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `course` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course`
--

LOCK TABLES `course` WRITE;
/*!40000 ALTER TABLE `course` DISABLE KEYS */;
INSERT INTO `course` VALUES (3,'IT2201PTHTW');
/*!40000 ALTER TABLE `course` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `extra_grade`
--

DROP TABLE IF EXISTS `extra_grade`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `extra_grade` (
  `id` int NOT NULL AUTO_INCREMENT,
  `grade_detail_id` int NOT NULL,
  `grade` float DEFAULT NULL,
  `grade_index` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_grade_detail_index` (`grade_detail_id`,`grade_index`),
  CONSTRAINT `extra_grade_ibfk_1` FOREIGN KEY (`grade_detail_id`) REFERENCES `grade_detail` (`id`) ON DELETE CASCADE,
  CONSTRAINT `extra_grade_chk_1` CHECK (((`grade` >= 0.0) and (`grade` <= 10.0)))
) ENGINE=InnoDB AUTO_INCREMENT=193 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `extra_grade`
--

LOCK TABLES `extra_grade` WRITE;
/*!40000 ALTER TABLE `extra_grade` DISABLE KEYS */;
INSERT INTO `extra_grade` VALUES (139,29,NULL,1),(153,29,NULL,0),(165,26,NULL,1),(175,26,NULL,0),(180,26,NULL,2);
/*!40000 ALTER TABLE `extra_grade` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `forum_post`
--

DROP TABLE IF EXISTS `forum_post`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `forum_post` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `content` text NOT NULL,
  `image` varchar(255) DEFAULT NULL,
  `user_id` int NOT NULL,
  `classroom_id` int NOT NULL,
  `created_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `classroom_id` (`classroom_id`),
  CONSTRAINT `forum_post_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `forum_post_ibfk_2` FOREIGN KEY (`classroom_id`) REFERENCES `classroom` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `forum_post`
--

LOCK TABLES `forum_post` WRITE;
/*!40000 ALTER TABLE `forum_post` DISABLE KEYS */;
/*!40000 ALTER TABLE `forum_post` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `forum_reply`
--

DROP TABLE IF EXISTS `forum_reply`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `forum_reply` (
  `id` int NOT NULL AUTO_INCREMENT,
  `content` text NOT NULL,
  `image` varchar(255) DEFAULT NULL,
  `forum_post_id` int NOT NULL,
  `parent_id` int DEFAULT NULL,
  `user_id` int NOT NULL,
  `created_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `forum_post_id` (`forum_post_id`),
  KEY `user_id` (`user_id`),
  KEY `parent_id` (`parent_id`),
  CONSTRAINT `forum_reply_ibfk_1` FOREIGN KEY (`forum_post_id`) REFERENCES `forum_post` (`id`) ON DELETE CASCADE,
  CONSTRAINT `forum_reply_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `forum_reply_ibfk_3` FOREIGN KEY (`parent_id`) REFERENCES `forum_reply` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `forum_reply`
--

LOCK TABLES `forum_reply` WRITE;
/*!40000 ALTER TABLE `forum_reply` DISABLE KEYS */;
/*!40000 ALTER TABLE `forum_reply` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `grade_detail`
--

DROP TABLE IF EXISTS `grade_detail`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `grade_detail` (
  `id` int NOT NULL AUTO_INCREMENT,
  `student_id` int NOT NULL,
  `course_id` int NOT NULL,
  `semester_id` int NOT NULL,
  `final_grade` float DEFAULT NULL,
  `midterm_grade` float DEFAULT NULL,
  `updated_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `student_id` (`student_id`,`course_id`,`semester_id`),
  KEY `course_id` (`course_id`),
  KEY `semester_id` (`semester_id`),
  CONSTRAINT `grade_detail_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `student` (`id`) ON DELETE CASCADE,
  CONSTRAINT `grade_detail_ibfk_2` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `grade_detail_ibfk_3` FOREIGN KEY (`semester_id`) REFERENCES `semester` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `grade_detail_chk_1` CHECK (((`final_grade` >= 0.0) and (`final_grade` <= 10.0))),
  CONSTRAINT `grade_detail_chk_2` CHECK (((`midterm_grade` >= 0.0) and (`midterm_grade` <= 10.0)))
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `grade_detail`
--

LOCK TABLES `grade_detail` WRITE;
/*!40000 ALTER TABLE `grade_detail` DISABLE KEYS */;
INSERT INTO `grade_detail` VALUES (24,31,3,1,NULL,NULL,NULL),(25,32,3,1,NULL,NULL,NULL),(26,33,3,1,NULL,NULL,NULL),(28,36,3,1,NULL,NULL,NULL),(29,37,3,1,NULL,NULL,NULL);
/*!40000 ALTER TABLE `grade_detail` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `semester`
--

DROP TABLE IF EXISTS `semester`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `semester` (
  `id` int NOT NULL AUTO_INCREMENT,
  `academic_year_id` int NOT NULL,
  `semester_type` enum('FIRST_TERM','SECOND_TERM','THIRD_TERM') NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `academic_year_id` (`academic_year_id`,`semester_type`),
  CONSTRAINT `semester_ibfk_1` FOREIGN KEY (`academic_year_id`) REFERENCES `academic_year` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `semester`
--

LOCK TABLES `semester` WRITE;
/*!40000 ALTER TABLE `semester` DISABLE KEYS */;
INSERT INTO `semester` VALUES (1,1,'FIRST_TERM');
/*!40000 ALTER TABLE `semester` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `student`
--

DROP TABLE IF EXISTS `student`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `student` (
  `id` int NOT NULL,
  `code` char(10) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`),
  CONSTRAINT `student_ibfk_1` FOREIGN KEY (`id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `chk_student_code` CHECK (regexp_like(`code`,_utf8mb4'^[0-9]{10}$'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `student`
--

LOCK TABLES `student` WRITE;
/*!40000 ALTER TABLE `student` DISABLE KEYS */;
INSERT INTO `student` VALUES (31,'3123456789'),(32,'4123456789'),(33,'5123456789'),(36,'6123456789'),(37,'7123456789');
/*!40000 ALTER TABLE `student` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `first_name` varchar(255) NOT NULL,
  `last_name` varchar(225) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) DEFAULT '1',
  `avatar` varchar(255) NOT NULL DEFAULT 'https://res.cloudinary.com/dqw4mc8dg/image/upload/w_1000,c_fill,ar_1:1,g_auto,r_max,bo_5px_solid_red,b_rgb:262c35/v1733391370/aj6sc6isvelwkotlo1vw.png',
  `created_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_date` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `active` tinyint(1) NOT NULL DEFAULT '0',
  `role` enum('ROLE_ADMIN','ROLE_LECTURER','ROLE_STUDENT') CHARACTER SET armscii8 COLLATE armscii8_general_ci NOT NULL DEFAULT 'ROLE_STUDENT',
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  CONSTRAINT `chk_email_ou` CHECK (regexp_like(`email`,_utf8mb4'^[A-Za-z0-9._%+-]+@ou\\.edu\\.vn$'))
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'min','ad','2251052065minh@ou.edu.vn','$2a$10$6n7jmydVQRFF5u2daYWnDuHpoK3C6QBjhZ4tZzGDvsrDh2ViyKOWS','https://res.cloudinary.com/dqw4mc8dg/image/upload/v1744183632/kagdbiirsk2aca0y9scy.png','2025-04-15 03:29:57','2025-04-15 03:29:57',1,'ROLE_ADMIN'),(21,'Trọng Tín','Vũ','1tin@ou.edu.vn','$2a$10$j65YdGZAkMLd4uwIVhn.VuGXFk6.xIVLql6PIwCdZ4ui4oQmd8nqi','https://res.cloudinary.com/dqw4mc8dg/image/upload/v1744274409/bbajk4zmitgwrbsf12ks.png','2025-04-10 08:40:07','2025-04-10 08:40:09',1,'ROLE_LECTURER'),(28,'Trung Hiếu','Phùng','hieu@ou.edu.vn','$2a$10$8UmwfBI2HdfKFBf8WZ4Nh.MLZALQ41Mkoucp6RStiwWY9Ju0NtcFu','https://res.cloudinary.com/dqw4mc8dg/image/upload/v1744334980/o3perguzkwi6zbmkidjn.png','2025-04-13 08:30:18','2025-04-13 08:30:18',1,'ROLE_LECTURER'),(31,'c','c','c@ou.edu.vn','$2a$10$UtKeFfMJRGCnjw/8Wq5dkuxNSs/4x7U0YkaR9A5wsN27sMAx5m86i','https://res.cloudinary.com/dqw4mc8dg/image/upload/v1744533195/tryf9uuc3eed6kzmt4ve.png','2025-04-13 08:34:16','2025-04-13 08:34:16',1,'ROLE_STUDENT'),(32,'d','d','d@ou.edu.vn','$2a$10$ju2YaWTiVI7jElVlyEP6ReOhGG6KNxOpCxp2g0leG1D6vWzVjizCm','https://res.cloudinary.com/dqw4mc8dg/image/upload/v1744533631/mede2zf09eqdpdvq6ycm.png','2025-04-13 08:40:31','2025-04-13 08:40:32',1,'ROLE_STUDENT'),(33,'e','e','e@ou.edu.vn','$2a$10$JV9DXWP5/M7HLuSMOTq2teemjQ4KHGozx2uPdXZ0scwH0hMwGR.Uu','https://res.cloudinary.com/dqw4mc8dg/image/upload/v1744533796/cq6ywoy05nleji96ssl8.png','2025-04-13 08:43:15','2025-04-13 08:43:17',1,'ROLE_STUDENT'),(36,'f','f','f@ou.edu.vn','$2a$10$2Yegpt8EOhuT2GG9xW1CquThtVA96o9XYWbXKpKPFsk8XRHpCTeGm','https://res.cloudinary.com/dqw4mc8dg/image/upload/v1744534666/pqwgl8yl68hmounlkmn7.png','2025-04-13 08:57:46','2025-04-13 08:57:47',1,'ROLE_STUDENT'),(37,'g','g','g@ou.edu.vn','$2a$10$pB1.tyQG.BvrRH0E1OPNvebGkzBIkeihK/H8xOtW8JSHaUhUNIUve','https://res.cloudinary.com/dqw4mc8dg/image/upload/v1744538681/nlpxvgayseipn0puvy9i.png','2025-04-13 10:04:39','2025-04-13 10:04:41',1,'ROLE_STUDENT');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-04-15 16:37:34
