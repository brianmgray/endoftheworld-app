/**
 * Admin JS
 */

!function($) {

  // initialize some base variables
  var constants = new eotw.Constants();
  var TemplateEnum = { MENU: "menu", ADD_SHOW: "add_show", ACTIVATE: "activate", RESULTS: "results" };

  // initialize pubnub and google visualizations
  var pubnub = initPubnub();

  // on document ready
  $(function() {
    $.jqlog.enabled(constants.logEnabled);
  });

  // load templates
  function goTo(templateEnum) {
    var template = '#' + templateEnum + '-template';
    var source   = $(template).html();
    var template = Handlebars.compile(source);
    var html = template(model);
    $('.template').html(html);

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

  function errorFxn(jqXHR, textStatus, errorThrown) {
    $('.spinner').hide();
    $.jqlog.error(textStatus + "..." + errorThrown);
    goTo(TemplateEnum.ERROR);
  }

}(window.jQuery);
