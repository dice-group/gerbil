'use strict';

var
controllers = angular.module('gerbil.controller.overview', []);
controllers.controller('OverviewCtrl', function($scope, $window, $http, $location) {

    $scope.title = "overview";
    $scope.search = $location.search();

    if ($scope.search.id) {
        // <!-- Experiment via ID -->
        $http.get('backend/experiment?id=' + $scope.search.id)
            .success(function(data) {
                $scope.overviewdata = data;
            })
            .error(function(data, status) {
                Alert.add({
                    type: 'error',
                    message: 'Could not get data (' + status + ').'
                });
            });

    } else {
        // <!-- ALL Experiments-->
        // call backend for data
        $http.get('backend/exptypes')
            .success(function(data) {
                $scope.exptypes = data;
            })
            .error(function(data, status) {
                Alert.add({
                    type: 'error',
                    message: 'Could not get data (' + status + ').'
                });
            });

        $scope.currentType = '';
        $scope.currentMatch = '';
        // type change
        $scope.setType = function(type) {
            $scope.currentType = type;
            $scope.experiment = $scope.exptypes[type];
            // delete old selections
            $scope.currentMatch = [];
            $scope.overviewdata = '';
        };

        $scope.setMatch = function(match) {
            $scope.currentMatch = match;
            $http.get('backend/experimentoverview?experimentType=' + $scope.currentType + '&matching=' + $scope.currentMatch)
                .success(function(data) {
                    if (data) {
                        $scope.overviewdata = data;
                        //Alert.add({ type: 'success', message: 'Experiment '+data.id+' running...' });
                    }
                })
                .error(function(data, status) {
                    Alert.add({
                        type: 'error',
                        message: 'Could get overview data (' + status + ').'
                    });
                });
        };
    }
});
