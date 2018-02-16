/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.apertum.qsystem.server.controller;

/**
 *
 * @author ANDREWXU
 */
public class SlackException extends RuntimeException {

    public SlackException(Throwable cause) {
        super(cause);
    }
}
