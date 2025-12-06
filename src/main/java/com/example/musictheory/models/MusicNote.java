package com.example.musictheory.models;

import java.util.Objects;

public class MusicNote {

    public int uuid; //generate a unique id for each note
    public String name;
    public String image;
    public float frequency;
    public String sound;
    public String tremble;
    public int position; // position as the number of the key or an image?

    public MusicNote(int uuid, String name, String image, float frequency, String sound, String tremble, int position) {
        this.uuid = uuid;
        this.name = name;
        this.image = image;
        this.frequency = frequency;
        this.sound = sound;
        this.tremble = tremble;
        this.position = position;
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

    public float getFrequency() {
        return frequency;
    }

    public void setFrequency(float frequency) {
        this.frequency = frequency;
    }

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

    public String getTremble() {
        return tremble;
    }

    public void setTremble(String tremble) {
        this.tremble = tremble;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MusicNote musicNote = (MusicNote) o;
        return uuid == musicNote.uuid && Float.compare(frequency, musicNote.frequency) == 0 && position == musicNote.position && Objects.equals(name, musicNote.name) && Objects.equals(image, musicNote.image) && Objects.equals(sound, musicNote.sound) && Objects.equals(tremble, musicNote.tremble);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, name, image, frequency, sound, tremble, position);
    }

    @Override
    public String toString() {
        return "MusicNote{" +
                "uuid=" + uuid +
                ", name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", frequency=" + frequency +
                ", sound='" + sound + '\'' +
                ", tremble='" + tremble + '\'' +
                ", position=" + position +
                '}';
    }
}
