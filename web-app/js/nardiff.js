/**
 * Created by kkoning on 2/17/15.
 */

(function () {
    var app = angular.module('nardiff', []);
    app.controller('WorkflowController', ['$scope', '$interval','$http', function ($scope, $interval, $http) {
        $scope.stage = 1;
        $scope.demographics = {};
        $scope.timeRemaining = 120;

        this.request_id = null;
        this.parent_story_id = null;
        this.distractorAnswer = null;
        this.story = null;
        this.age = null;
        this.gender = null;
        this.education = null;
        this.turker_id = null;
        this.storyTime = 0;
        this.distractorTime = null;
        this.retellTime = null;
        this.tooSimple = null;

        var removeImage;

        removeImage = function () {
            var img_div = document.getElementById("img-div");
            var to_remove = document.getElementById("toremove");
            img_div.removeChild(to_remove);
        };

        this.submitDemographics = function () {
            $http({
                method: 'POST',
                url: '/nardiff-mt/workflow/demographics',
                data: $.param($scope.demographics),  // pass in data as strings
                headers: {'Content-Type': 'application/x-www-form-urlencoded'}  // set the headers so angular passing info as form data (not request payload)
            });

            $scope.stage = 3;
        };

        this.stopTimer = function () {
            $scope.timeRemaining = 0;
            $scope.stage = 5;
            removeImage();
        };

        this.startTimer = function () {
            deductTime = $interval(function () {
                $scope.timeRemaining--;
                if ($scope.timeRemaining === 0) {
                    stopTimer()
                }

            }, 1000);
        };

        this.dt = function (wf) {
            wf.distractorTime = $scope.timeRemaining;
        };

        this.rt = function (wf) {
            wf.retellTime = $scope.timeRemaining;
        };

        this.st = function (wf) {
            wf.storyTime = $scope.timeRemaining;
        };


    }]);


})();
