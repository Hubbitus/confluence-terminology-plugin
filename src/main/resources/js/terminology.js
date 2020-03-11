$(function() {
	/* Confluence have own bundled `tipsy` jQuery plugin (enabled just like AJS.$('.glossary-term').tooltip() without any deps - https://docs.atlassian.com/aui/5.4.1/docs/tooltips.html),
	* but it is very-very old...
	* Use modern `qtip2` - http://qtip2.com/
	* See https://jsfiddle.net/Hubbitus/5ygkrxu4/17/ as playground of options and
	* http://qtip2.com/options#position.viewport as documentation
	**/
	$('a.glossary-term').each(function() {
		$(this).qtip({
			content: {
				// @TODO should nested Terms also be opened in popup???
				text: this.getAttribute('data-tooltip')
					.replace(/([^⍞]|^)⍞(?!⍞)/g, '$1"') // Hack. Firefox does not support lookbehind: https://bugzilla.mozilla.org/show_bug.cgi?id=1225665 (< https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide/Regular_Expressions)
						.replace(/⍞⍞/g, '⍞'),
				button: true
			},
			position: {
				my: 'top left',
				at: 'bottom left',
				viewport: $(window)
			},
			show: {
				solo: true,
				effect: function(offset) {
					$(this).fadeIn(1500);
				}
			},
			hide: {
				event: 'unfocus',
				effect: function(offset) {
					$(this).fadeOut(500);
				}
			},
			style: {
				classes: 'qtip-' + this.getAttribute('data-tooltip-color') + ' qtip-shadow qtip-rounded'
			}
		});
	});
});
