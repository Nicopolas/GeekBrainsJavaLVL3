package com.company;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main<T> {

    public static void main(String[] args) {
        for (Integer element : (Integer[]) new Main().swapArraysElement(new Integer[]{1, 2, 3, 4}, 2, 4)) {
            System.out.println(element);
        }

        System.out.println();
        Box firstBox = new Box(new Apple(), new Apple(), new Orange());

        firstBox = new Box(new Apple(), new Apple(), new Apple());
        System.out.println("Вес первой коробки: " + firstBox.getWeight());
        Box secondBox = new Box(new Orange(), new Orange(), new Orange());
        System.out.println("Вес второй коробки: " + secondBox.getWeight());
        System.out.println("Вес первой коробки равен весу второй коробки: " + firstBox.compare(secondBox));

        firstBox.pourFruit(secondBox);

        Box thirdBox = new Box(new Apple());
        firstBox.pourFruit(thirdBox);
        System.out.println("Вес первой коробки: " + firstBox.getWeight());
        System.out.println("Вес третей коробки: " + thirdBox.getWeight());
    }

    /*
    1. Написать метод, который меняет два элемента массива местами.(массив может быть любого ссылочного типа);
     */
    public T[] swapArraysElement(T[] arr, T firsElement, T secondElement) {
        List<T> list = arrayToArrayList(arr);
        int i = list.indexOf(firsElement);
        int j = list.indexOf(secondElement);

        T element = arr[i];
        arr[i] = arr[j];
        arr[j] = element;
        return arr;
    }

    /*
    2. Написать метод, который преобразует массив в ArrayList;
     */
    public ArrayList arrayToArrayList(T[] arr) {
        return new ArrayList<>(Arrays.asList(arr));
    }

}
