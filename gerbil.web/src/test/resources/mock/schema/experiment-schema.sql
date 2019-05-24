CREATE TABLE IF NOT EXISTS ExperimentTasks (
id int(10) unsigned NOT NULL AUTO_INCREMENT PRIMARY KEY,
experimentType VARCHAR(10),
matching VARCHAR(50),
systemNAme VARCHAR(100),
datasetName VARCHAR(100),
state int(10),
lastChanged TIMESTAMP,
version VARCHAR(20)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS Experiments (
  id VARCHAR(300) unsigned NOT NULL,
  taskId int(10) unsigned NOT NULL,
   PRIMARY KEY (id, taskId)
  
) ENGINE=InnoDB;