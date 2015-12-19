'use strict';

angular.module('powerApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('powerHistory', {
                parent: 'entity',
                url: '/powerHistorys',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'PowerHistorys'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/powerHistory/powerHistorys.html',
                        controller: 'PowerHistoryController'
                    }
                },
                resolve: {
                }
            })
            .state('powerHistory.detail', {
                parent: 'entity',
                url: '/powerHistory/{id}',
                data: {
                    authorities: ['ROLE_USER'],
                    pageTitle: 'PowerHistory'
                },
                views: {
                    'content@': {
                        templateUrl: 'scripts/app/entities/powerHistory/powerHistory-detail.html',
                        controller: 'PowerHistoryDetailController'
                    }
                },
                resolve: {
                    entity: ['$stateParams', 'PowerHistory', function($stateParams, PowerHistory) {
                        return PowerHistory.get({id : $stateParams.id});
                    }]
                }
            })
            .state('powerHistory.new', {
                parent: 'powerHistory',
                url: '/new',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/powerHistory/powerHistory-dialog.html',
                        controller: 'PowerHistoryDialogController',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    timestamp: null,
                                    power: null,
                                    current: null,
                                    id: null
                                };
                            }
                        }
                    }).result.then(function(result) {
                        $state.go('powerHistory', null, { reload: true });
                    }, function() {
                        $state.go('powerHistory');
                    })
                }]
            })
            .state('powerHistory.edit', {
                parent: 'powerHistory',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/powerHistory/powerHistory-dialog.html',
                        controller: 'PowerHistoryDialogController',
                        size: 'lg',
                        resolve: {
                            entity: ['PowerHistory', function(PowerHistory) {
                                return PowerHistory.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('powerHistory', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            })
            .state('powerHistory.delete', {
                parent: 'powerHistory',
                url: '/{id}/delete',
                data: {
                    authorities: ['ROLE_USER'],
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'scripts/app/entities/powerHistory/powerHistory-delete-dialog.html',
                        controller: 'PowerHistoryDeleteController',
                        size: 'md',
                        resolve: {
                            entity: ['PowerHistory', function(PowerHistory) {
                                return PowerHistory.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function(result) {
                        $state.go('powerHistory', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    })
                }]
            });
    });
