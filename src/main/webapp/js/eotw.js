/**
 * App JS
 */

!function($) {

  var baseUrl = "/api";
  var TemplateEnum = { DEFAULT: "default", VOTE: "vote", SUCCESS: "success", ERROR: "error"};

  var model = new eotw.Model();

  google.load("visualization", "1", {packages:["corechart"]});

  // on document ready
  $(function() {
    $.jqlog.enabled(true);

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
    $.getJSON('http://jsonip.appspot.com/?callback=?',
      function(data){
        model.ip = data.ip;
      })
      .fail(function() {
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
    $.get(baseUrl + "/show/active/" + code)
     .done(function(data) {
        $.jqlog.info("Active? " + data.activationStatus);
        if (data.activationStatus == "ACTIVE") {
          model.showKey = data.key;
          goTo(TemplateEnum.VOTE);
          addVoteHandlers();
        } else {
          goTo(TemplateEnum.DEFAULT);
        }
      })
      .fail(errorFxn);
  }

  function vote() {
    var voteObj = { "showKey": model.showKey, "ip": model.ip, "vote": model.vote };
    $.ajax({
        url: baseUrl + '/vote',
        type: 'POST',
        contentType: 'application/json',
        dataType: 'json',
        data: JSON.stringify(voteObj)
    })
      .done(function(data, status) {
        goTo(TemplateEnum.SUCCESS);
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
    $.get(baseUrl + "/vote/forShow/" + model.showKey)
      .done(function(data) {
        $.jqlog.info("votes " + JSON.stringify(data));
        displayResults(data);
        $('.spinner').hide();
      })
      .fail(errorFxn);
  }

  function displayResults(showData) {
    var data = google.visualization.arrayToDataTable([
      ['Choice', 'Num Votes'],
      ['Save', showData.saveVotes],
      ['Destroy', showData.destroyVotes]
    ]);

    var options = {
      backgroundColor: '#140f0f',
      legend: {position: 'none'},
      hAxis: {titleTextStyle: {color: '#CDCDCD'}, textStyle: {color: '#CDCDCD'}},
      colors: ['#CDCDCD'],
      series: [{color: '#d43f3a'}] // #d43f3a
    };

    var chart = new google.visualization.ColumnChart(document.getElementById('results'));
    chart.draw(data, options);
  }

  function errorFxn(jqXHR, textStatus, errorThrown) {
    $('.spinner').hide();
    $.jqlog.error(textStatus + "..." + errorThrown);
    goTo(TemplateEnum.ERROR);
  }

}(window.jQuery);
