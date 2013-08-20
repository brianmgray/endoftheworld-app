/**
 * Admin JS
 */

!function($) {

  // initialize some base variables
  var constants = eotw.Constants;
  var model = new eotw.Model();
  var TemplateEnum = { MENU: "menu", ADD: "add", ACTIVATE: "activate", RESULTS: "results" };

  // initialize pubnub and google visualizations
  var pubnub = initPubnub();
  google.load("visualization", "1", {packages:["corechart"]});

  // on document ready
  $(function() {
    $.jqlog.enabled(constants.logEnabled);
    $('.header').detach();
    bindHistoryEvents();
    registerHandlebarHelpers();

    // load the current shows
    $.get(constants.baseUrl + "/show/all")
      .done(function(data) {
        $.jqlog.info("shows " + JSON.stringify(data));
        model.shows = data.shows;
        $.History.go('menu');
        $('.spinner').hide();
      })
      .fail(errorFxn);
  });

  // load templates
  function goTo(templateEnum) {
    var template = '#' + templateEnum + '-template';
    var source   = $(template).html();
    var template = Handlebars.compile(source);
    var html = template(model);
    $('.template').html(html);
  }

  function initPubnub() {
    // subscribe only
    return PUBNUB.init({
      origin        : 'pubsub.pubnub.com', // for same origin issue
      subscribe_key : constants.subscribeKey
    });
  }

  function bindHistoryEvents() {
    // Bind a handler for ALL hash/state changes
    $.History.bind(function(state){
      // go to the appropriate template
      $.jqlog.info("Switching to: " + state);
      goTo(state);
      addHandlers(state);
    });
  }

  function registerHandlebarHelpers() {
    Handlebars.registerHelper("foreach",function(arr,options) {
      if(options.inverse && !arr.length)
        return options.inverse(this);

      return arr.map(function(item,index) {
        item.$index = index;
        item.$first = index === 0;
        item.$last  = index === arr.length-1;
        return options.fn(item);
      }).join('');
    });
  }

  /**
   * Add event handlers based on the state
   * @param state
   */
  function addHandlers(state) {
    switch (state) {
      case TemplateEnum.MENU:
        var dropdown = $('#selectShow');
        dropdown.change(function(e) {
          model.showKey = $('#selectShow option:selected').val();
        });
        model.showKey = $('#selectShow option:selected').val();
        break;
      case TemplateEnum.RESULTS:
        loadVotes();
        pubnub.subscribe({
          channel : 'eotw-vote',
          message : function(m){
            loadVotes();
          }
        });
        break;
      default:
        // do nothing
        break;
    }

    // not on the results page, unsubscribe pubnub
    if (state != TemplateEnum.RESULTS) {
      pubnub.unsubscribe({ channel : 'eotw-vote' })
    }
  }

  // TODO reduce duplication with eotw.js

  function loadVotes() {
    $('.spinner').show();
    $.get(constants.baseUrl + "/vote/forShow/" + model.showKey)
      .done(function(data) {
        $.jqlog.info("votes " + JSON.stringify(data));
        model.showMercy = (data.saveVotes > data.destroyVotes);
        goTo(TemplateEnum.RESULTS);
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

    $('#results').empty();
    var chart = new google.visualization.ColumnChart(document.getElementById('results'));
    chart.draw(data, options);
  }

  function errorFxn(jqXHR, textStatus, errorThrown) {
    $('.spinner').hide();
    $.jqlog.error(textStatus + "..." + errorThrown);
    goTo(TemplateEnum.ERROR);
  }

}(window.jQuery);
