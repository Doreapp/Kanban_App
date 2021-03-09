package com.mandin.antoine.kanbanapp.utils

/**
 * Object containing Project constants
 */
object Constants {
    /**
     * Name of the database
     */
    const val DATABASE_NAME = "db"

    /**
     * Constants about panels
     */
    object Panels {
        /**
         * List panel identifier
         */
        const val LIST = 0

        /**
         * To-Do panel identifier
         */
        const val TODO = 1

        /**
         * Doing panel identifier
         */
        const val DOING = 2

        /**
         * Done panel identifier
         */
        const val DONE = 3
        val EVERY_PANELS = arrayOf(LIST, TODO, DOING, DONE)
    }
}