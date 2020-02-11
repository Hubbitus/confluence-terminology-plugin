$(function() {
    AJS.$('.glossary-term').tooltip({
        // gravity: 's'
        gravity: $.fn.tipsy.autoNS
        ,fade: true
        ,html: true
        ,title: function() {
            return '<div>' + this.getAttribute('original-title').replace(/\\"/g, '"') + '</div>';
        }
    });
});
