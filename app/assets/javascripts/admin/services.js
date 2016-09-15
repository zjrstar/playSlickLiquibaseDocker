/**
 * User service, exposes user model to the rest of the app.
 */
define(['angular', 'common'], function (angular) {
  'use strict';

  var mod = angular.module('admin.services', ['yourprefix.common', 'ngCookies']);
  mod.factory('adminService', ['$http', '$q', 'playRoutes', '$cookies', '$log', function ($http, $q, playRoutes, $cookies, $log) {
    return {
      createUser: function (newUser) {
        return playRoutes.controllers.Users.createUser().post(newUser).then(function (response) {
          // return promise so we can chain easily
          var result = response.data;
          $log.info(result);
          return "创建成功";
        }).catch(function (error) {
          $log.error(error);
          return "创建失败";
        });
      },
      findUsers: function (queryEmail) {
        return playRoutes.controllers.Users.findUsers(queryEmail).get().then(function (response) {
          var result = response.data;
          $log.info(result);
          return result;
        });
      },
      deleteUser: function (userId) {
        return playRoutes.controllers.Users.deleteUser(userId).delete().then(function (response) {
                  var result = response.data;
                  $log.info(result);
                  return result;
                });
      },
      updateUser: function (user) {
        return playRoutes.controllers.Users.updateUser(user.id).put(user).then(function (response) {
          var result = response.data;
          $log.info(result);
          return result;
        }).catch(function (error) {
          $log.error(error);
          return 0;
        });
      },
      listQueryLogs: function() {
        return playRoutes.controllers.Credits.listQueryLogs().get().then(function (response) {
          return response.data;
        }).catch(function (error) {
          $log.error(error);
          return [];
        });
      },
      listQueryResultOfQueryLogs: function(offset, pageSize) {
              return playRoutes.controllers.Credits.listQueryResultOfQueryLogs(offset, pageSize).get().then(function (response) {
                $log.info(response.data);
                return response.data;
              }).catch(function (error) {
                $log.error(error);
                return [];
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
