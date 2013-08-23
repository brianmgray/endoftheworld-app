/**
 * App JS
 */

!function($) {

  // initialize some base variables
  var constants = eotw.Constants;
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
        addFacebookHandler();
        displayResults(data);
        $('.spinner').hide();
      })
      .fail(errorFxn);
  }

  function addFacebookHandler() {
    $('.facebook').click(function(e) {
      var vote = $(this).attr('vote');
      FB.ui({
        method: 'feed',
        link: 'http://on.fb.me/17kjf9o',
        picture: 'http://endoftheworldimprov.com/img/logo.jpg',
        name: 'I just voted to ' + vote + ' the humans at The End of the World Show!',
        description: 'You can too! FOUR PERFORMANCES ONLY: Fridays at 10pm through 9/13 at Arcade Comedy Theater. $10/$5. BYOB./n/n' +
          'THE END OF THE WORLD SHOW is a brand new take on the apocalypse. Prepare to be transformed (physically and mentally) into a creature from another planet, and view mankind from an outside eye./n/n' +
          'In the not too distant future, an alien race has taken over Earth and are hell-bent on its destruction. A motley band of humans win an audience with the Alien Counsel to attempt to prove the emotional life, meaningful relationships and humor of mankind is worth saving!/n/n' +
          'Will they succeed? Will man be spared? It’s up to you! You act the part of the alien counsel as the performers play improv games to win your sympathy toward mankind.'
      }, function(response){});
    })
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
    var pubnub = PUBNUB.init({
      origin        : 'pubsub.pubnub.com', // for same origin issue
      subscribe_key : constants.subscribeKey
    });
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
