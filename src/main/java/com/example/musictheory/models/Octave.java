package com.example.musictheory.models;

import java.util.Objects;

public class Octave {

    public int uuid; //generate a unique id for each note
    public String name;
    public String image;
    public String sound;
    public MusicNote[] notes;

    public Octave(int uuid, String name, String image, String sound, MusicNote[] notes) {
        this.uuid = uuid;
        this.name = name;
        this.image = image;
        this.sound = sound;
        this.notes = notes;
    }

    public int getUuid() {
        return uuid;
    }

    public void setUuid(int uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Octave octave = (Octave) o;
        return uuid == octave.uuid && Objects.equals(name, octave.name) && Objects.equals(image, octave.image) && Objects.equals(sound, octave.sound);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, name, image, sound);
    }

    @Override
    public String toString() {
        return "Octave{" +
                "uuid=" + uuid +
                ", name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", sound='" + sound + '\'' +
                '}';
    }

}
