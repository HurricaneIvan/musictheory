package com.example.musictheory.models;

import java.util.Objects;
import java.util.Optional;

public class Symbol {

    public int uuid;
    public String name;
    public String symbol;
    public Optional<Integer> count;

    public Symbol(int uuid, String name, String symbol, Optional<Integer> count) {
        this.uuid = uuid;
        this.name = name;
        this.symbol = symbol;
        this.count = count;
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

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Optional<Integer> getCount() {
        return count;
    }

    public void setCount(Optional<Integer> count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Symbol symbol1 = (Symbol) o;
        return uuid == symbol1.uuid && Objects.equals(name, symbol1.name) && Objects.equals(symbol, symbol1.symbol) && Objects.equals(count, symbol1.count);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, name, symbol, count);
    }

    @Override
    public String toString() {
        return "Symbol{" +
                "uuid=" + uuid +
                ", name='" + name + '\'' +
                ", symbol='" + symbol + '\'' +
                ", count=" + count +
                '}';
    }
}
