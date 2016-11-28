$(function() {
    $('#detailsForm').submit(function(event) {
        var parameters = $(this).serializeArray();
        addCsrfKey(parameters);
        $.post("submitDetails", parameters, function(data) {
            $('#result-details').empty().append('Done!').fadeOut(1000);
        }).fail(function(xhr) {
            console.log(xhr['responseText']);
            openWindow(xhr['responseText']);
        });
        event.preventDefault();
    });

    $('#addNewForm').submit(function(event) {
        var parameters = $(this).serializeArray();
        addCsrfKey(parameters);
        $.post("processAddNew", parameters, function(data) {
            $('#result-details').empty().append('Done!').fadeOut(1000);
            window.location = "";
        }).fail(function(xhr) {
            console.log(xhr['responseText']);
            openWindow(xhr['responseText']);
        });
        event.preventDefault();
    });

    $('#cancelButton').click(function() {
        $('#result-details').fadeOut(500);
    });
});

function addCsrfKey(params) {
    var csrfKey = {
        name : 'CSRF_key',
        value : $('#csrf_key').text()
    };
    params.push(csrfKey);
}

function showDetails(itemId) {
    $.get("processDetails", {
        accounting_item_id : itemId
    }, function(data) {
        $('#result-details').empty().fadeIn(500).append(data + '<br>');
    }).fail(function(xhr) {
        console.log(xhr['responseText']);
        openWindow(xhr['responseText']);
    });
};

function showAddNew() {
    $.get("showAddNew", [], function(data) {
        $('#result-details').empty().fadeIn(500).append(data + '<br>');
    }).fail(function(xhr) {
        console.log(xhr['responseText']);
        openWindow(xhr['responseText']);
    });
};

function openWindow(html) {
    var w = window.open();
    w.document.write(html);
}
