package com.github.esslerc.pdfamaker.util;

import com.github.esslerc.pdfamaker.service.SettingsData;

public record SettingsSavedEvent(SettingsData newSettingsData) { }