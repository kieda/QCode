package qcode.drivers;

import qcode.QCode;


/**
 * a basic main class, for when we want to have an independent main.
 * @author zkieda
 */
public class QMain{
    public static void main(String[] args) {
        new QCode().init();
    }
}
