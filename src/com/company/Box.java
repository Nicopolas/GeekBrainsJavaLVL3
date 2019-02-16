package com.company;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/*
3. Большая задача:
a. Есть классы Fruit -> Apple, Orange;(больше фруктов не надо)

b. Класс Box в который можно складывать фрукты, коробки условно сортируются по типу фрукта,
поэтому в одну коробку нельзя сложить и яблоки, и апельсины;

c. Для хранения фруктов внутри коробки можете использовать ArrayList;

d. Сделать метод getWeight() который высчитывает вес коробки, зная количество фруктов
и вес одного фрукта(вес яблока - 1.0f, апельсина - 1.5f, не важно в каких это единицах);

e. Внутри класса коробка сделать метод compare, который позволяет сравнить текущую коробку с той,
которую подадут в compare в качестве параметра, true - если их веса равны,
false в противном случае(коробки с яблоками мы можем сравнивать с коробками с апельсинами);

f. Написать метод, который позволяет пересыпать фрукты из текущей коробки в другую коробку
(помним про сортировку фруктов, нельзя яблоки высыпать в коробку с апельсинами),
соответственно в текущей коробке фруктов не остается, а в другую перекидываются объекты, которые были в этой коробке;

g. Не забываем про метод добавления фрукта в коробку.
 */

public class Box<F extends Fruit> {
    List<F> fruits = new ArrayList<>();
    String typeFruits;

    public Box(F... elements) {
        typeFruits = elements[0].getClass().getSimpleName();
        for (F element : elements) {
            try {
                addFruit(element);
            } catch (Exception e) {
                System.err.println(e.getMessage());
                continue;
            }
        }
    }

    public float getWeight() {
        if (fruits.isEmpty()) {
            return 0f;
        }
        return fruits.size() * fruits.get(0).getWeight();
    }

    public boolean compare(Box anotherBox) {
        return getWeight() == anotherBox.getWeight();
    }

    public void pourFruit(Box anotherBox) {
        if (fruits.isEmpty()) {
            return;
        }

        try {
            Iterator<F> iterator = fruits.iterator();
            while (iterator.hasNext()) {
                F fruit = iterator.next();
                anotherBox.addFruit(fruit);
                iterator.remove();
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return;
        }
    }

    public void addFruit(F fruit) throws Exception {
        if (fruits.isEmpty()) {
            typeFruits = fruit.getClass().getSimpleName();
            fruits.add(fruit);
            return;
        }
        if (checkTypeFruit(fruit)) {
            fruits.add(fruit);
        }
    }

    private boolean checkTypeFruit(F fruit) throws Exception {
        if (!fruits.isEmpty()) {
            if (!fruit.getClass().getSimpleName().equals(typeFruits)) {
                throw new Exception("Ошибка при добавлении елемента в коробку, в коробке может одновременно находится лишь один тип фруктов");
            }
        }
        return true;
    }
}
