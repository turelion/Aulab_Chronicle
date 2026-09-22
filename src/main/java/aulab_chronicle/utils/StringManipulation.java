package aulab_chronicle.utils;

//Classe di utilità per le operazioni di manipolazione delle stringhe.
//Isola la logica di estrazione dell'estensione dei file.

public class StringManipulation {

    //Estrae l'estensione di un file a partire dal suo nome originale. Es: "foto_vacanze.png" -> "png"
    public static String getFileExtension(String nameFile) {
        if (nameFile == null || !nameFile.contains(".")) {
            return "";
        }
        int dotIndex = nameFile.lastIndexOf('.');
        return nameFile.substring(dotIndex + 1);
    }
}
