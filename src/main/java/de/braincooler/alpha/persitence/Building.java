package de.braincooler.alpha.persitence;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Building {
    public Integer id;
    public Integer size;
    public Sind underControl;
    public Sind ownerSind;
    public Sektor sektor;
    public String url;
    public String name;
    public String tur;
}
