/**
 * Created by kkoning on 2/17/15.
 */

(function () {
    var app = angular.module('nardiff', []);
    app.controller('WorkflowController', ['$scope', '$interval', '$http', function ($scope, $interval, $http) {
        $scope.stage = 1;
        $scope.elapsedTime = 0;
        $scope.distractorAnswer = "";
        $scope.demographics = {};
        $scope.survey = {};
        $scope.story = "";
        $scope.storyTime = 0;
        $scope.distractorTime = 0;
        $scope.retellTime = 0;
        $scope.askForDemographics = false;
        $scope.workerId = "";
        $scope.assignmentId = "";

        this.removeImage = function () {
            var img_div = document.getElementById("img-div");
            var to_remove = document.getElementById("toremove");
            img_div.removeChild(to_remove);
        };

        $scope.steps = [
            'Step 1',
            'Step 2',
            'Step 3',
            'Step 4'
        ];
        $scope.selection = $scope.steps[0];

        $scope.getCurrentStepIndex = function(){
            // Get the index of the current step given selection
            return _.indexOf($scope.steps, $scope.selection);
        };

        $scope.hasNextStep = function(){
            var stepIndex = $scope.getCurrentStepIndex();
            var nextStep = stepIndex + 1;
            // Return true if there is a next step, false if not
            return !_.isUndefined($scope.steps[nextStep]);
        };

        $scope.hasPreviousStep = function(){
            var stepIndex = $scope.getCurrentStepIndex();
            var previousStep = stepIndex - 1;
            // Return true if there is a next step, false if not
            return !_.isUndefined($scope.steps[previousStep]);
        };

        $scope.incrementStep = function() {
            if ( $scope.hasNextStep() )
            {
                var stepIndex = $scope.getCurrentStepIndex();
                var nextStep = stepIndex + 1;
                $scope.selection = $scope.steps[nextStep];
            }
        };

        $scope.decrementStep = function() {
            if ( $scope.hasPreviousStep() )
            {
                var stepIndex = $scope.getCurrentStepIndex();
                var previousStep = stepIndex - 1;
                $scope.selection = $scope.steps[previousStep];
            }
        };


        this.submitStage = function () {

            var form = {
                "narrativeId":$scope.narrative,
                "stage":$scope.stage

            };

            $http({
                method: 'POST',
                url: '/nardiff-mt/narrative/submitStage',
                data: $.param(form),  // pass in data as strings
                headers: {'Content-Type': 'application/x-www-form-urlencoded'}  // set the headers so angular passing info as form data (not request payload)
            });
        };

        this.submitStory = function () {

            var form = {
                "narrativeId":$scope.narrative,
               "text":$scope.story,
                "distractorAnswer":$scope.distractorAnswer,
                "timeDistractor":$scope.distractorTime,
                "timeReading":$scope.storyTime,
                "timeWriting":$scope.retellTime
            };

            $http({
                method: 'POST',
                url: '/nardiff-mt/narrative/submitNarrative',
                data: $.param(form),  // pass in data as strings
                headers: {'Content-Type': 'application/x-www-form-urlencoded'}  // set the headers so angular passing info as form data (not request payload)
            });
        };

        this.submitDemographics = function () {

            $http({
                method: 'POST',
                url: '/nardiff-mt/narrative/demographics',
                data: $.param($scope.demographics),  // pass in data as strings
                headers: {'Content-Type': 'application/x-www-form-urlencoded'}  // set the headers so angular passing info as form data (not request payload)
            });
        };

        this.submitSurvey = function () {

            $http({
                method: 'POST',
                url: '/nardiff-mt/narrative/survey',
                data: $.param($scope.survey),  // pass in data as strings
                headers: {'Content-Type': 'application/x-www-form-urlencoded'}  // set the headers so angular passing info as form data (not request payload)
            });
        };


        this.resetTimer = function () {
            $scope.elapsedTime = 0;
        };

        this.startTimer = function () {

            $scope.elapsedTime = 0;
            var context = this;
            $interval(function () {
                $scope.elapsedTime++;
                if ($scope.stage == 4 && $scope.elapsedTime > 60) {
                    context.advance();
                }

            }, 1000);
        };



        this.advance = function () {
            switch ($scope.stage) {
                case 0:
                case 1:
                    var ask = $scope.askForDemographics;
                    if (ask) {
                        $scope.stage = 2;
                    } else {
                        $scope.stage = 3;
                    }
                    break;

                case 3:
                    this.startTimer();
                    $scope.stage = 4;
                    break;

                case 4:
                    $scope.storyTime = $scope.elapsedTime;
                    this.resetTimer();
                    this.removeImage();
                    $scope.stage = 5;
                    break;

                case 5:
                    this.resetTimer();
                    $scope.stage = 6;
                    break;


                case 6:
                    $scope.distractorTime = $scope.elapsedTime;
                    this.resetTimer();
                    $scope.stage = 7;
                    break;

                case 7:
                    $scope.retellTime = $scope.elapsedTime;
                    this.resetTimer();
                    $scope.stage = 8;
                    break;

                case 8:
                    break;

                default:
                    $scope.stage++;

            }
            this.submitStage();


        };



    }])
})();
