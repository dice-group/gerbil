CREATE TABLE IF NOT EXISTS ExperimentTasks (
id int(10) unsigned NOT NULL AUTO_INCREMENT PRIMARY KEY,
experimentType VARCHAR(300),
matching VARCHAR(300),
annotatorName VARCHAR(300),
datasetName VARCHAR(300),
microF1 double,
microPrecision double,
microRecall double,
macroF1 double,
macroPrecision double,
macroRecall double,
state int(10),
created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ,

) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS Experiments (
  id VARCHAR(300) unsigned NOT NULL,
  taskId int(10) unsigned NOT NULL,
   PRIMARY KEY (id, taskId)
  
) ENGINE=InnoDB;