TRUNCATE TABLE  Experiments;
TRUNCATE TABLE ExperimentTasks;


INSERT INTO ExperimentTasks (systemName, datasetName, experimentType, matching) VALUES ('derGuteBernd', 'test_dataset', 'QUATSCH', 'matsch');
INSERT INTO Experiments (id, taskId) VALUES ('id-123', '1');

