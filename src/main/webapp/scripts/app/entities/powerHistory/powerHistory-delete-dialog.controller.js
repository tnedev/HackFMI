'use strict';

angular.module('powerApp')
	.controller('PowerHistoryDeleteController', function($scope, $uibModalInstance, entity, PowerHistory) {

        $scope.powerHistory = entity;
        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.confirmDelete = function (id) {
            PowerHistory.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };

    });
