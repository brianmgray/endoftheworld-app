/**
 * Shared Model
 */

!function($) {

  // use eotw namespace
  window.eotw = $.extend({}, window.eotw, {

    Model : function() {
      var showKey;
      var ip;
      var vote;
      var showMercy;
    },

    AdminModel : function() {
      var selectedShow;
      var shows;
    }

  });

}(window.jQuery);
