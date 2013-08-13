/**
 * App JS
 */

google.load("visualization", "1", {packages:["corechart"]});
//google.setOnLoadCallback(drawChart);
function drawChart() {
  var data = google.visualization.arrayToDataTable([
    ['Choice', 'Num Votes'],
    ['Save', 13],
    ['Destroy', 25]
  ]);

  var options = {
    backgroundColor: '#140f0f',
    legend: {position: 'none'},
    hAxis: {titleTextStyle: {color: '#CDCDCD'}, textStyle: {color: '#CDCDCD'}},
    colors: ['#CDCDCD'],
    series: [{color: '#d43f3a'}] // #d43f3a
  };

  var chart = new google.visualization.ColumnChart(document.getElementById('results'));
  chart.draw(data, options);
}
