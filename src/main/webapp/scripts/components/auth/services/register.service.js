'use strict';

angular.module('powerApp')
    .factory('Register', function ($resource) {
        return $resource('api/register', {}, {
        });
    });


