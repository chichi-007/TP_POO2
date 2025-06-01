package com.example.Model;



import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonTypeName; // Important pour Jackson

@JsonTypeName("concert") // Indi
public class Concert extends Evenement {
    private String artiste;
    private String genreMusical;


    public Concert() {
        // Constructeur par défaut (nécessaire pour Jackson)
        super(); // Appelle le constructeur par défaut de Evenement
    }
    public Concert(String id, String nom, LocalDateTime date, String lieu, int capaciteMax, String artiste, String genreMusical) {
        super(id, nom, date, lieu, capaciteMax);
        this.artiste = artiste;
        this.genreMusical = genreMusical;
    }

    public String getArtiste() {
        return artiste;
    }

    public void setArtiste(String artiste) {
        this.artiste = artiste;
    }

    public String getGenreMusical() {
        return genreMusical;
    }

    public void setGenreMusical(String genreMusical) {
        this.genreMusical = genreMusical;
    }

    @Override
    public void afficherDetails() {
        System.out.println("Concert de " + artiste + " - Genre : " + genreMusical);
    }
}
