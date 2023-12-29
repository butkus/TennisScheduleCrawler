
package com.butkus.tenniscrawler.rest.orders;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Result {

    @JsonProperty("iki")
    private String iki;

    @JsonProperty("kaina")
    private String kaina;

    @JsonProperty("kompl_preke")
    private Long komplPreke;

    @JsonProperty("nar_kiekgalioja")
    private String narKiekgalioja;

    @JsonProperty("pard_id")
    private Long pardId;

    @JsonProperty("pasl_pavadinimas")
    private String paslPavadinimas;

    @JsonProperty("prek_pavadinimas")
    private String prekPavadinimas;

    @JsonProperty("pv_pavadinimas")
    private String pvPavadinimas;

    @JsonProperty("sab_id")
    private Long sabId;

    @JsonProperty("sab_parduodamas")
    private Long sabParduodamas;

    @JsonProperty("sasi_galiojanuo")
    private String sasiGaliojanuo;

}
