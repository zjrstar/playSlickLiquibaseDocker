/**
 * User service, exposes user model to the rest of the app.
 */
define(['angular', 'common'], function (angular) {
  'use strict';

  var mod = angular.module('credit.services', ['yourprefix.common', 'ngCookies']);
  mod.factory('creditService', ['$http', '$q', 'playRoutes', '$cookies', '$log', function ($http, $q, playRoutes, $cookies, $log) {
    return {
      executeQuery: function (creditQuery) {
        var delay = $q.defer();
        playRoutes.controllers.Credits.checkIdCard().post(creditQuery).then(function (response) {
          // return promise so we can chain easily
          var creditResult = response.data[0];
          $log.info(creditResult);
          delay.resolve(creditResult);
        }, function(error){
          $log.info(error);
          delay.reject('目前服务不可用');
        });
        return delay.promise;
      },
      executeMobilePhoneQuery: function(mobilePhone) {
        return playRoutes.controllers.Credits.checkMobileCreditScore().post(mobilePhone).then(function (response) {
          var result = response.data[0];
          $log.info(result);
          return result;
        });
      },
      executeCreditUsageQuery: function() {
        return playRoutes.controllers.Credits.fetchUsage().get().then(function (response) {
          var result = response.data;
          $log.info(result);
          return result;
        });
      },
      executeCompoundCheckLocal: function(compoundCheckRequests) {
        return playRoutes.controllers.Credits.compoundCheckCredit(true).post(compoundCheckRequests).then(function (response) {
           var result = response.data[0];
           $log.info(result);
           return result;
        });
      },
      executeCompoundCheckRemote: function(compoundCheckRequests) {
         return playRoutes.controllers.Credits.compoundCheckCredit(false).post(compoundCheckRequests).then(function (response) {
           var result = response.data[0];
           $log.info(result);
           return result;
        });
      }
    };
  }]);

  var handleRouteError = function ($rootScope, $location) {
    $rootScope.$on('$routeChangeError', function (/*e, next, current*/) {
      $location.path('/');
    });
  };
  handleRouteError.$inject = ['$rootScope', '$location'];
  mod.run(handleRouteError);
  return mod;
});
