'use strict';

angular.module('powerApp').controller('PowerHistoryDialogController',
    ['$scope', '$stateParams', '$uibModalInstance', 'entity', 'PowerHistory',
        function($scope, $stateParams, $uibModalInstance, entity, PowerHistory) {

        $scope.powerHistory = entity;
        $scope.load = function(id) {
            PowerHistory.get({id : id}, function(result) {
                $scope.powerHistory = result;
            });
        };

        var onSaveSuccess = function (result) {
            $scope.$emit('powerApp:powerHistoryUpdate', result);
            $uibModalInstance.close(result);
            $scope.isSaving = false;
        };

        var onSaveError = function (result) {
            $scope.isSaving = false;
        };

        $scope.save = function () {
            $scope.isSaving = true;
            if ($scope.powerHistory.id != null) {
                PowerHistory.update($scope.powerHistory, onSaveSuccess, onSaveError);
            } else {
                PowerHistory.save($scope.powerHistory, onSaveSuccess, onSaveError);
            }
        };

        $scope.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        $scope.datePickerForTimestamp = {};

        $scope.datePickerForTimestamp.status = {
            opened: false
        };

        $scope.datePickerForTimestampOpen = function($event) {
            $scope.datePickerForTimestamp.status.opened = true;
        };
}]);
