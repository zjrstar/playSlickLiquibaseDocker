/**
 * Admin dashboard
 * admin/main.js is the entry module which serves as an entry point so other modules only have
 * to include a single module.
 */
define(['angular', './routes', './services', './controllers'], function(angular, adminRoutes, adminService, adminController) {
  'use strict';

  var app = angular.module('yourprefix.admin', ['ngRoute', 'admin.routes', 'admin.services']);
  app.directive("edit",function($document){
    return {
      restrict: 'AE',
      require: 'ngModel',
      link: function(scope,element,attrs,ngModel){

         element.bind("click",function(){
            scope.$apply(function(){
                scope.master[ngModel.$modelValue.id] = {};
                angular.copy(ngModel.$modelValue,scope.master[ngModel.$modelValue.id]);
            });
            var editables = ["name", "role", "email"];
            for (var i = 0; i < editables.length; i ++) {
                var id = "txt_" + editables[i] + "_" +ngModel.$modelValue.id;
                var obj = $("#"+id);
                obj.removeClass("inactive");
                obj.addClass("active");
                obj.removeAttr("readOnly");
            }
            scope.$apply(function(){
               scope.showEdit = false;
            });
          });
         }
    };

  }).directive("cancel", function($document) {
    return {
      restrict: 'AE',
      require: 'ngModel',
      link: function(scope, element, attrs, ngModel) {
        element.bind("click", function(){
          scope.$apply(function(){
            angular.copy(scope.master[ngModel.$modelValue.id], ngModel.$modelValue);
          });
          var editables = ["name", "role", "email"];
          for (var i = 0; i < editables.length; i++) {
             var id = "txt_" + editables[i] + "_" + ngModel.$modelValue.id;
             var obj = $("#" + id);
             obj.removeClass("active");
             obj.addClass("inactive");
             obj.prop("readOnly",true);
          }
          scope.$apply(function(){
            scope.showEdit = true;
          });
        });
      }
    };
  }).directive("update", ['$document', 'adminService', function($document, adminService) {
    return {
      restrict: 'AE',
      require: 'ngModel',
      link: function(scope, element, attrs, ngModel) {
        element.bind("click", function(){
          adminService.updateUser(ngModel.$modelValue).then(function(result){
            if (result > 0) {
              scope.showEdit = true;
            } else {
              alert("更新失败");
              scope.$apply(function(){
                angular.copy(scope.master[ngModel.$modelValue.id], ngModel.$modelValue);
              });
              scope.$apply(function(){
                scope.showEdit = true;
              });
            }
          });

        });
      }
    };
  }]).directive("delete", ['$document', 'adminService', function($document, adminService) {
    return {
      restrict: 'AE',
      require: 'ngModel',
      link: function(scope, element, attrs, ngModel) {
        element.bind("click", function(){
          adminService.deleteUser(ngModel.$modelValue.id).then(function(result){
            if(result === true) {
              for (var i = 0; i < scope.users.length; i ++) {
                if (scope.users[i].id == ngModel.$modelValue.id) {
                  scope.users.splice(i, 1);
                }
              }
            } else {
              alert("删除失败");
            }
          });
        });
      }
    };
  }]);

  return app;
});
