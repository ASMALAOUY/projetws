<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET');

include_once dirname(__DIR__) . '/service/EtudiantService.php';
$es = new EtudiantService();
echo json_encode($es->findAllApi());
?>