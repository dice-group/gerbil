'use strict';

var
controllers = angular.module('gerbil.controller.config', []);
controllers.controller('ConfigCtrl', function($scope, $window, $http, $upload, Alert) {

    $scope.title = "config";
    $scope.currentAnnotators = [];
    $scope.currentDatasets = [];
    $scope.currentMatch = [];
    $scope.newWebservices = [];
    $scope.newDatasets = [];
    $scope.percent = 0;
    $scope.overviewid = -1;
    $scope.experiment = {
        annotator: [],
        datasets: []
    };
    $scope.filedata = {
        name:''
    };

    // execute experiment
    $scope.run = function() {

        var newWebservices = [];
        angular.forEach($scope.newWebservices, function(value, key) {
            newWebservices.push('NIFWS_' + value.name + '(' + value.url + ')');
        });

        var newDatasets = [];
        angular.forEach($scope.newDatasets, function(value, key) {
            newDatasets.push('NIFDS_' + value.name + '(' + value.url + ')');
        });

        var postdata = {
            type: $scope.currentType,
            matching: $scope.currentMatch,
            annotator: _.union($scope.currentAnnotators, newWebservices),
            dataset: _.union($scope.currentDatasets,newDatasets)
        };
        $http.post('backend/execute', postdata)
            .success(function(data) {
                if (data.id) {
                    $scope.overviewid = data.id;
                    Alert.add({
                        type: 'success',
                        message: 'Experiment ' + data.id + ' running...'
                    });
                    $scope.runs = true;
                }
            })
            .error(function(data, status) {
                Alert.add({
                    type: 'error',
                    message: 'Could not execute experiment (' + status + ').'
                });
            });
    };

    // upload file
    //$files: an array of files selected, each file has name, size, and type
    $scope.onFileSelect = function($files) {
        if ($files && $scope.filedata.name){
            for (var i = 0; i < $files.length; i++) {
                $scope.upload = $upload.upload({
                    url: 'file/upload',
                    method: 'POST',
                    data: {
                        name: $scope.filedata.name
                    },
                    file: $files[i]
                }).progress(function(evt) {
                    $scope.percent = +(parseInt(100.0 * evt.loaded / evt.total));
                }).success(function(data, status, headers, config) {
                  
                    if(config && config.file && config.file.name){
                        var o = {
                            name: $scope.filedata.name,
                            url: config.file.name
                        };
                        $scope.newDatasets.push(o);
                    }
                    
                }).error(function(data, status) {
                    Alert.add({
                        type: 'error',
                        message: 'Could not upload data (' + status + ').'
                    });
                });
            }
        }
    };

    $scope.dragOverClass = function($event) {
        if(!$scope.filedata.name){
            Alert.add({
                type: 'error',
                message: 'Please set a name for the files.'
            });
            return "dragover-err";
        }else{
            var items = $event.dataTransfer.items;
            var hasFile = false;
            if (items != null) {
                for (var i = 0; i < items.length; i++) {
                    if (items[i].kind == 'file') {
                        hasFile = true;
                        break;
                    }
                }
            } else {
                hasFile = true;
            }
            return hasFile ? "dragover" : "dragover-err";
        }
    };


    // call backend for data
    $http.get('backend/exptypes')
        .success(function(data) {
            $scope.exptypes = data;
        })
        .error(function(data, status) {
            $scope.messages.push({
                type: 'error',
                text: 'Could not get data (' + status + ').'
            });
        });

    // type change
    $scope.setType = function(type) {
        $scope.currentType = type;
        // delete old selections
        $scope.currentMatch = [];
        $scope.currentDatasets = [];
        $scope.currentAnnotators = [];
        // set new experiment data
        $scope.experiment = $scope.exptypes[type];

    };

    $scope.setMatch = function(match) {
        $scope.currentMatch = match;
    };

    $scope.removeWebservice = function(ws) {
        $scope.newWebservices = _.without($scope.newWebservices, _.findWhere($scope.newWebservices, ws));
    };

    $scope.addWebservice = function() {
        if ($scope.newWebservice && $scope.newWebservice.name && $scope.newWebservice.url) {
            var add = 1;
            angular.forEach($scope.newWebservices, function(value, key) {
                if (value.url == $scope.newWebservice.url) {
                    Alert.add({
                        type: 'error',
                        message: 'No duplicates allowed!'
                    });
                    add = 0;
                }
            });
            if (add) {
                $scope.newWebservices.push($scope.newWebservice);
                $scope.newWebservice = {};
                Alert.reset();
            }
        } else {
            Alert.add({
                type: 'warn',
                message: 'Set a name and a URL!'
            });
        }
    };

    $scope.removeDataset = function(ds) {
        $scope.newDatasets = _.without($scope.newDatasets, _.findWhere($scope.newDatasets, ds));
    };
});
