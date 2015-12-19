'use strict';

angular.module('powerApp')
    .controller('PowerHistoryDetailController', function ($scope, $rootScope, $stateParams, entity, PowerHistory) {
        $scope.powerHistory = entity;
        $scope.load = function (id) {
            PowerHistory.get({id: id}, function(result) {
                $scope.powerHistory = result;
            });
        };
        var unsubscribe = $rootScope.$on('powerApp:powerHistoryUpdate', function(event, result) {
            $scope.powerHistory = result;
        });
        $scope.$on('$destroy', unsubscribe);

    });
