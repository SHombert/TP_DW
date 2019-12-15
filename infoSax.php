<?php header('Content-type: text/xml');
include_once('Sax4PHP.php');

class MySaxHandler extends DefaultHandler {

  public $flags = [
    'act' => false,
    'actUID' => false,
    'pren' => false,
    'nom' => false,
  ];

  public $uid;
  
  public $infos;
  public $acteurs;
  public $organes;

  function startElement($name, $att) {
    switch($nom){
      case 'acteur' :
        $flags['act'] = true;

      case 'uid' :
        if($flags['act']){
          $flags['actUID'] = true;
        }

      case 'prenom' : 
        $flags['pren'] = true;

      case 'nom' :
        $flags['nom'] =true;

      case 'scrutin' : 
        $flags ['sc'] = true;

    }
    
  }

  function endElement($name) {
    echo "<end name='$name'/>\n";
  } 
  
  function startDocument() {
          echo "<information>\n";
        } 
  function endDocument() {

          echo "</information>\n";
        }
  function characters($txt){

    if($flags['actUID']){
        $uid=$txt;
    }

    if($flags['prenom']){
      $act[$uid] ['prenom'] = $txt;
    }
    if($flags['nom']){
      $act[$uid] ['nom'] = $txt;
    }

    if($flags['dateSC']){
      $dateSC = $txt;
    }

    if($flags['codeSort']){
      $sort = $txt;
    }

    
  }
}

$xml = file_get_contents('assemblee1920.xml');
$sax = new SaxParser(new MySaxHandler());
try {
    $sax->parse($xml);
}catch(SAXException $e){  
    echo "\n",$e; }catch(Exception $e) {
    echo "Default exception >>", $e; }
    
    
    ?>
 