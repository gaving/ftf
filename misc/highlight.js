
function getColour(index) {
    var color = '#CCCCCC';

    switch (index) {
        case 0:
            color='#99FF99';
            break;
        case 1:
            color='yellow';
            break;
        case 2:
            color='#FFCCFF';
            break;
        case 3:
            color='#CC99FF';
            break;
        case 4:
            color='#99CCFF';
            break;
        case 5:
            color='#FFCC99';
            break;
        case 6:
            color='#CCCCFF';
            break;
        case 7:
            color='#66CCFF';
            break;
        default:
            break;
    }
    return color;
}

function highlightWord(bodyText, searchTerm, colour) {

    var highlightStartTag = "<font style='color: #000000; background-color: " + colour + ";'>";
    var highlightEndTag = "</font>";
    var newText = "";
    var i = -1;
    var lcSearchTerm = searchTerm.toLowerCase();
    var lcBodyText = bodyText.toLowerCase();

    while (bodyText.length > 0) {
        i = lcBodyText.indexOf(lcSearchTerm, i+1);
        if (i < 0) {
            newText += bodyText;
            bodyText = "";
        } else {
            if (bodyText.lastIndexOf(">", i) >= bodyText.lastIndexOf("<", i)) {
                if (lcBodyText.lastIndexOf("/script>", i) >= lcBodyText.lastIndexOf("<script", i)) {
                    newText += bodyText.substring(0, i) + highlightStartTag + bodyText.substr(i, searchTerm.length) + highlightEndTag;
                    bodyText = bodyText.substr(i + searchTerm.length);
                    lcBodyText = bodyText.toLowerCase();
                    i = -1;
                }
            }
        }
    }

    return newText;
}

function highlight(searchText, treatAsPhrase) {
    if (treatAsPhrase) {
        searchArray = [searchText];
    } else {
        searchArray = searchText.split(" ");
    }

    if (!document.body || typeof(document.body.innerHTML) == "undefined") {
        if (warnOnFailure) {
            alert("Sorry! For some reason the text of this page is unavailable. Searching will not work.");
        }
        return false;
    }

    var bodyText = document.body.innerHTML;
    for (var i = 0; i < searchArray.length; i++) {
        var highlightColour = getColour(i);
        bodyText = highlightWord(bodyText, searchArray[i], highlightColour);
    }

    document.body.innerHTML = bodyText;
}

function reset() {
    alert("TODO: This should now clear the highlighting!")
}
