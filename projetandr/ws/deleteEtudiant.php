<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: POST, DELETE');

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    include_once dirname(__DIR__) . '/service/EtudiantService.php';
    
    $id = $_POST['id'] ?? 0;
    
    $es = new EtudiantService();
    $etudiant = $es->findById($id);
    
    if($etudiant) {
        $es->delete($etudiant);
        echo json_encode(['success' => true, 'message' => 'Étudiant supprimé']);
    } else {
        echo json_encode(['success' => false, 'message' => 'Étudiant non trouvé']);
    }
} else {
    echo json_encode(['error' => 'Méthode non autorisée']);
}
?>