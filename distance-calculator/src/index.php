<?php
require_once('app/settings.php');
require_once('app/requests.php');

if (isset($_GET['origin']) && isset($_GET['destination'])) {
  $location1 = Requests::getLocation($_GET['origin']);
  $location2 = Requests::getLocation($_GET['destination']);

  if ($location1['error'] || $location2['error']) {
    $output['statusCode'] = 500;
    http_response_code(500);
  }
  else {
    $result = Requests::getDistance($location1['coordinates'], $location2['coordinates']);
    if ($result['error']) {
      http_response_code(500);
      $output['statusCode'] = 500;
    } else {
      $output['statusCode'] = 200;
      $output['distance'] = $result['distance'];
    }
  }

  header('Content-type:application/json;charset=utf-8');
  echo json_encode($output);
} else {
  include('app/views/main.php');
}
