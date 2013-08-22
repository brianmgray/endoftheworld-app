/**
 * Admin JS
 */

!function($) {

  // initialize some base variables
  var constants = eotw.Constants;
  var model = new eotw.AdminModel();
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
    loadShows(function(shows) {
      $.History.go('menu'); // default to menu
    });
  });

  /**
   * (re)Load all shows
   * @param callback
   */
  function loadShows(callback) {
    // load the current shows
    $.get(constants.baseUrl + "/show/all")
      .done(function(data) {
        $.jqlog.info("shows " + JSON.stringify(data));
        model.shows = data.shows;
        callback(model.shows);
        $('.spinner').hide();
      })
      .fail(errorFxn);
  }

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
    $.History.bind(function(state){
      $.jqlog.info("Switching to: " + state);
    });

    $.History.bind('menu', function(state) {
      goTo(state);
      var dropdown = $('#selectShow');
      dropdown.change(function(e) {
        model.show = findSelectedShow($('#selectShow option:selected').val(), model.shows);
      });
      model.show = findSelectedShow($('#selectShow option:selected').val(), model.shows);
      pubnub.unsubscribe({ channel : 'eotw-vote' })
    });

    $.History.bind('results', function(state) {
      goTo(state);
      loadVotes(model.show.key);
      pubnub.subscribe({
        channel : 'eotw-vote',
        message : function(m){
          loadVotes(model.show.key);
        }
      });
    });

    $.History.bind('activate', function(state) {
      goTo(state);
      setupActivate();
      pubnub.unsubscribe({ channel : 'eotw-vote' });
    });

    $.History.bind('add', function(state) {
       // TODO
      goTo(state);
      pubnub.unsubscribe({ channel : 'eotw-vote' });
    });
  }

  function setupActivate() {
    // update the view
    var newStatus = model.show.activationStatus == 'ACTIVE' ? 'INACTIVE' : 'ACTIVE';
    $('#btn' + model.show.activationStatus).show();
    $('#btn' + newStatus).hide();

    // add the button handler
    $('#btn' + model.show.activationStatus).click(function(e) {
      $.ajax({
        url: constants.baseUrl + '/show/' + model.show.key + '/' + newStatus,
        type: 'PUT',
        contentType: 'application/json',
        dataType: 'json'
      })
      .done(function(data, status) {
          loadShows(function(newShows) {
            // the closure still has the old show
            model.shows = newShows;
            model.show = findSelectedShow(model.show.key, model.shows);
            setupActivate();
          });
      })
      .fail(errorFxn);
      return false;
    });
  }

  // TODO reduce duplication with eotw.js

  function loadVotes(showKey) {
    $('.spinner').show();
    $.get(constants.baseUrl + "/vote/forShow/" + showKey)
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

  function findSelectedShow(selectedKey, shows) {
    var selectedModels = $.grep(shows, function(element, index) {
       return element.key == selectedKey;
    });
    return selectedModels.length == 1 ? selectedModels[0] : null;
  }

  function errorFxn(jqXHR, textStatus, errorThrown) {
    $('.spinner').hide();
    $.jqlog.error(textStatus + "..." + errorThrown);
    goTo(TemplateEnum.ERROR);
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

}(window.jQuery);
