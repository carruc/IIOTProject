package model.objects;

import java.util.HashMap;
import java.util.Map;

class Inventario {

    /**Classe usata per gestire l'inventario dei medicinali**/
    private Map<String, Medicinale> inventario;

    public Inventario() {
        this.inventario = new HashMap<>();
    }

    public void aggiungiMedicinale(String nome, int quantitaIniziale) {
        Medicinale medicinale = new Medicinale(nome, quantitaIniziale);
        inventario.put(nome, medicinale);
    }

    public int getQuantitaMedicinale(String nome) {
        if (inventario.containsKey(nome)) {
            return inventario.get(nome).getQuantita();
        }
        return 0;
    }

    public void aggiornaQuantitaMedicinale(String nome, int nuovaQuantita) {
        if (inventario.containsKey(nome)) {
            inventario.get(nome).setQuantita(nuovaQuantita);
        }
    }

    public void controllaDisponibilitaMedicinale(String nomePaziente, String nomeMedicinale) {
        int quantitaDisponibile = getQuantitaMedicinale(nomeMedicinale);
        if (quantitaDisponibile > 0) {
            System.out.println("Medicinale disponibile per il paziente " + nomePaziente);
        } else {
            System.out.println("Attenzione! Il medicinale per il paziente " + nomePaziente + " è esaurito.");

        }
    }
}
