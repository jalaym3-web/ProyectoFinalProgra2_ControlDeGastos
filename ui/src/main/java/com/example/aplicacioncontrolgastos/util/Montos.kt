package com.example.aplicacioncontrolgastos.util

fun String.aMontoOrNull(): Double? = trim().replace(',', '.').toDoubleOrNull()