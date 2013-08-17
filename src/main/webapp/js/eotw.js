/**
 * App JS
 */

!function($) {

  // initialize some base variables
  var constants = new eotw.Constants();
  var TemplateEnum = { DEFAULT: "default", INACTIVE: "inactive", VOTE: "vote", SUCCESS: "success", ERROR: "error"};
  var model = new eotw.Model();

  // initialize pubnub and google visualizations
  var pubnub = initPubnub();
  google.load("visualization", "1", {packages:["corechart"]});

  // on document ready
  $(function() {
    $.jqlog.enabled(constants.logEnabled);

    getIp();
    var code = $.url().param('code');
    if (code) {
      $.jqlog.info("Code " + code);
      checkCode(code);
    } else {
      goTo(TemplateEnum.DEFAULT);
    }

    $('.spinner').hide();
  });

  function getIp() {
    // make a JSONP call to get the client ip
    $.getJSON('http://ip-api.com/json', function(data) {
        model.ip = data.query;
      }).fail(function(d) {
        model.ip = "default";
      });
  }

  // load templates
  function goTo(templateEnum) {
    var template = '#' + templateEnum + '-template';
    var source   = $(template).html();
    var template = Handlebars.compile(source);
    var html = template(model);
    $('.template').html(html);
  }


  /**
   * Check the code for an active show
   * @param code
   */
  function checkCode(code) {
    $.get(constants.baseUrl + "/show/active/" + code)
     .done(function(data) {
        $.jqlog.info("Active? " + data.activationStatus);
        if (data.activationStatus == "ACTIVE") {
          model.showKey = data.key;
          goTo(TemplateEnum.VOTE);
          addVoteHandlers();
        } else {
          loadInactive();
        }
      })
      .fail(errorFxn);
  }

  function vote() {
    var voteObj = { "showKey": model.showKey, "ip": model.ip, "vote": model.vote };
    $.ajax({
        url: constants.baseUrl + '/vote',
        type: 'POST',
        contentType: 'application/json',
        dataType: 'json',
        data: JSON.stringify(voteObj)
    })
      .done(function(data, status) {
        loadVotes();
      })
      .fail(errorFxn);
  }

  function addVoteHandlers() {
    $('#btnSave').click(function(e){
      model.vote = "SAVE";
      vote();
      return false;
    });
    $('#btnDestroy').click(function(e){
      model.vote = "DESTROY";
      vote();
      return false;
    });
  }

  function loadVotes() {
    $('.spinner').show();
    $.get(constants.baseUrl + "/vote/forShow/" + model.showKey)
      .done(function(data) {
        $.jqlog.info("votes " + JSON.stringify(data));
        model.showMercy = (data.saveVotes > data.destroyVotes);
        goTo(TemplateEnum.SUCCESS);
        displayResults(data);
        $('.spinner').hide();
      })
      .fail(errorFxn);
  }

  function displayResults(showData) {
    // set chart up as a series so color can be controlled
    var data = google.visualization.arrayToDataTable([
      ['Vote', 'Save',                  'Destroy'],
      ['Vote',  showData.saveVotes,      showData.destroyVotes]
    ]);

    var options = {
      backgroundColor: '#140f0f',
      legend: {position: 'right', textStyle: {color: '#CDCDCD'}},
      hAxis: {titleTextStyle: {color: '#CDCDCD'}, textStyle: {color: '#CDCDCD'}},
      colors: ['5cb85c', 'd43f3a']
    };

    var chart = new google.visualization.ColumnChart(document.getElementById('results'));
    chart.draw(data, options);
  }

  function initPubnub() {
    // subscribe only
    var pubnub = PUBNUB.init({ subscribe_key : constants.subscribeKey });
    pubnub.subscribe({
      channel : 'eotw-show',
      message : function(m){
        if (m.activationStatus == "ACTIVE") {
          goTo(TemplateEnum.VOTE);
        } else {
          loadInactive();
        }
      }
    });
    return pubnub;
  }

  function loadInactive() {
    goTo(TemplateEnum.INACTIVE);
    $(".vidEarth").get(0).play();
    $(".vidEarth").bind('ended', function(){
      this.play();
    });
  }

  function errorFxn(jqXHR, textStatus, errorThrown) {
    $('.spinner').hide();
    $.jqlog.error(textStatus + "..." + errorThrown);
    goTo(TemplateEnum.ERROR);
  }

}(window.jQuery);
