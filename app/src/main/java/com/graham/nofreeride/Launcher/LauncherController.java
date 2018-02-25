package com.graham.nofreeride.Launcher;

/**
 * Created by grahamherceg on 2/3/18.
 */

public class LauncherController {
    LauncherContract.view view;

    public LauncherController(LauncherContract.view view) {
        this.view = view;
        // when created, check if shared preferences contain
    }
}
