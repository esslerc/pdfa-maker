package com.github.esslerc.pdfamaker.ui;

import java.util.ResourceBundle;

abstract class AbstractI18nDialogController {

    protected ResourceBundle i18n;

    AbstractI18nDialogController(ResourceBundle i18n) {
        this.i18n = i18n;
    }
}
