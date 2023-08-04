package com.cjcrafter.mechanicseverywhere.command

import com.cjcrafter.mechanicseverywhere.MechanicsEverywhere

object Command {

    fun register() {
        command("mechanicseverywhere") {
            aliases("mechanics")

            subcommand("pool") {

            }

            subcommand("execute") {

            }
        }
    }
}