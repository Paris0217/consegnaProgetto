<?php
require_once('curl_helper.php');

const LOCATION_URL = 'http://dev.virtualearth.net/REST/v1/Locations';
const DISTANCE_URL = 'https://dev.virtualearth.net/REST/v1/Routes/DistanceMatrix';

class Requests
{
  public static function getLocation(String $location)
  {
    $url = LOCATION_URL;
    $method = 'GET';
    $parameters = array('q' => $location, 'maxResults' => 1, 'key' => BING_KEY);

    $result = CurlHelper::perform_http_request($method, $url, $parameters);
    $result = json_decode($result);

    if (isset($result) && isset($result->statusCode) && $result->statusCode >= 200 && $result->statusCode < 300) {
      $coordinates = $result->resourceSets[0]->resources[0]->point->coordinates;

      return [ 'error' => false, 'coordinates' => $coordinates ];
    }

    return [ 'error' => true ];
  }

  public static function getDistance($coordinates1, $coordinates2)
  {
    $origins = $coordinates1[0] . ',' . $coordinates1[1];
    $destinations = $coordinates2[0] . ',' . $coordinates2[1];

    $url = DISTANCE_URL;
    $method = 'GET';
    $parameters = array('origins' => $origins, 'destinations' => $destinations, 'travelMode' => 'driving', 'key' => BING_KEY);

    $result = CurlHelper::perform_http_request($method, $url, $parameters);
    $result = json_decode($result);

    if (isset($result) && isset($result->statusCode) && $result->statusCode >= 200 && $result->statusCode < 300) {
      $distance = $result->resourceSets[0]->resources[0]->results[0]->travelDistance;

      return [ 'error' => false, 'distance' => $distance ];
    }

    return [ 'error' => true ];
  }
}
