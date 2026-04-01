<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST');

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    include_once dirname(__DIR__) . '/service/EtudiantService.php';
    
    $id = $_POST['id'] ?? 0;
    $nom = $_POST['nom'] ?? '';
    $prenom = $_POST['prenom'] ?? '';
    $ville = $_POST['ville'] ?? '';
    $sexe = $_POST['sexe'] ?? '';
    
    $es = new EtudiantService();
    $etudiant = new Etudiant($id, $nom, $prenom, $ville, $sexe);
    $es->update($etudiant);
    
    echo json_encode(['success' => true, 'message' => 'Étudiant modifié']);
} else {
    echo json_encode(['error' => 'Méthode non autorisée']);
}
?>