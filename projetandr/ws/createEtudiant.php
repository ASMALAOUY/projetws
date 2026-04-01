<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST');
header('Access-Control-Allow-Headers: Content-Type');

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    include_once dirname(__DIR__) . '/service/EtudiantService.php';
    
    $nom = $_POST['nom'] ?? '';
    $prenom = $_POST['prenom'] ?? '';
    $ville = $_POST['ville'] ?? '';
    $sexe = $_POST['sexe'] ?? '';
    
    $es = new EtudiantService();
    $es->create(new Etudiant(0, $nom, $prenom, $ville, $sexe));
    
    echo json_encode($es->findAllApi());
} else {
    echo json_encode(['error' => 'Méthode non autorisée']);
}
?>