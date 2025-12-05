/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.poo.Main;

import javax.swing.SwingUtilities;

import com.poo.view.MenuPrincipal;

/**
 *
 * @author cabra
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
        MenuPrincipal menu = new MenuPrincipal();
        menu.setVisible(true);
    });
    }
}
