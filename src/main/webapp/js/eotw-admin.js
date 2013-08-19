/**
 * Admin JS
 */

!function($) {

  // initialize some base variables
  var constants = eotw.Constants;
  var TemplateEnum = { MENU: "menu", ADD: "add", ACTIVATE: "activate", RESULTS: "results" };

  // initialize pubnub and google visualizations
  var pubnub = initPubnub();

  // on document ready
  $(function() {
    $.jqlog.enabled(constants.logEnabled);
    bindHistoryEvents();
    $.History.go('menu');
    $('.spinner').hide();
  });

  // load templates
  function goTo(templateEnum) {
    var template = '#' + templateEnum + '-template';
    var source   = $(template).html();
    var template = Handlebars.compile(source);
    $('.template').html(template);

    // subscribe to vote updates on the results page
    if (template == TemplateEnum.RESULTS) {
      pubnub.subscribe({
        channel : 'eotw-vote',
        message : function(m){
          $.jqlog.info('vote: ' + m);
        }
      });
    } else {
      pubnub.unsubscribe({ channel : 'chan8' })
    }
  }

  function initPubnub() {
    // subscribe only
    return PUBNUB.init({ subscribe_key : constants.subscribeKey });
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

  function errorFxn(jqXHR, textStatus, errorThrown) {
    $('.spinner').hide();
    $.jqlog.error(textStatus + "..." + errorThrown);
    goTo(TemplateEnum.ERROR);
  }

  /**
   * Add event handlers based on the state
   * @param state
   */
  function addHandlers(state) {
    switch (state) {
      default:
        // do nothing
        break;
    }
  }

}(window.jQuery);
