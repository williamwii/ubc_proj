$(document).ready(function() {
	$(".collapse-btn").click(function() {
		$(".nav-collapse").collapse("hide");
	});
	$("#navbar-query").keypress(function(e) {
		if (String.fromCharCode(e.which)=="\r" ) {
			$(".nav-collapse").collapse("hide");
		}
	});

	// Temporary fix for bootstrap dropdown menu (breaks on mobile)
	$("body")
		.on('click.dropdown', '.dropdown form', function (e) { e.stopPropagation() })
		.on('touchstart.dropdown.data-api', '.dropdown', function (e) { e.stopPropagation() })
	$('.dropdown-menu').on('touchstart.dropdown.data-api', function(e) { e.stopPropagation() })

	window.scrollTo(0, 1);
});

function toggleHide() {
	if ($("#search-form").attr("style")!="display: none;") {
		$("#marketplace-search-btn").html("Show");
		$("#search-form").attr("style", "display: none;");
	}
	else {
		$("#marketplace-search-btn").html("Hide");
		$("#search-form").attr("style", "");
	}
}

function hideSearchForm() {
	$("#marketplace-search-btn").html("Show");
	$("#search-form").attr("style", "display: none;");
}
function showSearchForm() {
	$("#marketplace-search-btn").html("Hide");
	$("#search-form").attr("style", "");
}

function toggleSubscriptionHide() {
	if ($("#subscription-form").attr("style")!="display: none;") {
		$("#subscription-search-btn").html("Show");
		$("#subscription-form").attr("style", "display: none;");
	}
	else {
		$("#subscription-search-btn").html("Hide");
		$("#subscription-form").attr("style", "");
	}
}