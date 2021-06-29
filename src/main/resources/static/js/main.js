// Reacting on pollee update server side events: either a new one added or an existing one updated

function populateRow(row, pollee) {
  row.cells[0].className = "number";
  row.cells[0].innerHTML = pollee.id;

  row.cells[1].className = "text";
  row.cells[1].innerHTML = pollee.name;

  row.cells[2].className = "text";
  row.cells[2].innerHTML = pollee.serviceUrl;

  row.cells[3].className = "text";
  row.cells[3].innerHTML = pollee.status;

  row.cells[4].className = "date";
  row.cells[4].innerHTML = pollee.createdAt;

  row.cells[5].className = "date";
  row.cells[5].innerHTML = pollee.lastCheckedAt;
}

function loadPollees() {
  this.source = null;

  this.start = function () {
    var polleesTable = document.getElementById("pollees");
    this.source = new EventSource("/services/updates");

    this.source.addEventListener("message", function (event) {
      console.log(event)

      var pollee = JSON.parse(event.data);
      var rows = polleesTable.getElementsByTagName("tbody")[0].rows;
      var row = null

      for (var i = 0; i < rows.length; i++) {
        if (rows[i].cells[0].innerHTML === pollee.id.toString()) {
          row = rows[i];
          break;
        }
      }

      if (row == null) {
        row = polleesTable.getElementsByTagName("tbody")[0].insertRow(0);
        for (i = 0; i < 6; i++) {
          row.insertCell(i);
        }
      }

      populateRow(row, pollee);
    });

    this.source.onerror = function () {
      this.close();
    };
  };

  this.stop = function () {
    this.source.close();
  }
}

pollee = new loadPollees();

window.onload = function () {
  pollee.start();
};

window.onbeforeunload = function () {
  pollee.stop();
}

