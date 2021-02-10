package com.mandin.antoine.kanbanapp.utils

object Constants {
    const val DATABASE_NAME = "db"

    object Panels {
        const val LIST = 0
        const val TODO = 1
        const val DOING = 2
        const val DONE = 3
        val EVERY_PANELS = arrayOf(LIST, TODO, DOING, DONE)
    }
}