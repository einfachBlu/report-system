package de.blu.reportsystem.repository;

import java.util.ArrayList;
import java.util.List;

public abstract class Repository<T> {

  private List<T> content = new ArrayList<>();

  public List<T> all() {
    return this.content;
  }

  public boolean contains(T element) {
    return this.content.contains(element);
  }

  public void remove(T element) {
    this.content.remove(element);
  }

  public void add(T element) {
    this.content.add(element);
  }

  public void clear() {
    this.content.clear();
  }

  public void addAll(List<T> elements) {
    this.content.addAll(elements);
  }
}
