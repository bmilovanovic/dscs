package com.example.dscs.game;

/**
 * Class storing the two parts of the film title that need to be connected.
 */
class Pair {

    String first = "";
    String second = "";

    /**
     * Forms a pair from the film title.
     *
     * @param title Title with the multiple parts separated by a space character.
     */
    Pair(String title) {
        String[] array = title.split(" ");
        for (int i = 0; i < array.length; i++) {
            if (i < array.length / 2) {
                first += array[i] + " ";
            } else {
                second += array[i] + " ";
            }
        }
        // Subtract the last space character.
        first = first.substring(0, first.length() - 1);
        second = second.substring(0, second.length() - 1);
    }
}
