package converter

import kotlin.system.exitProcess

enum class Type {
    LENGTH, WEIGHT, TEMPERATURE, ERROR
}

enum class AllUnits(val synonyms: List<String>, val type: Type, val conversion: Double?) {
    METERS(listOf("meters", "meter", "m"), Type.LENGTH, 1.0),
    KILOMETERS(listOf("kilometers", "kilometer", "km"), Type.LENGTH, 1000.0),
    CENTIMETERS(listOf("centimeters", "centimeter", "cm"), Type.LENGTH, 0.01),
    MILLIMETERS(listOf("millimeters", "millimeter", "mm"), Type.LENGTH, 0.001),
    MILES(listOf("miles", "mile", "mi"), Type.LENGTH, 1609.35),
    YARDS(listOf("yards", "yard", "yd"), Type.LENGTH, 0.9144),
    FEET(listOf("feet", "foot", "ft"), Type.LENGTH, 0.3048),
    INCHES(listOf("inches", "inch", "in"), Type.LENGTH, 0.0254),
    GRAMS(listOf("grams", "gram", "g"), Type.WEIGHT, 1.0),
    KILOGRAMS(listOf("kilograms", "kilogram", "kg"), Type.WEIGHT, 1000.0),
    MILLIGRAMS(listOf("milligrams", "milligram", "mg"), Type.WEIGHT, 0.001),
    POUNDS(listOf("pounds", "pound", "lb"), Type.WEIGHT, 453.592),
    OUNCES(listOf("ounces", "ounce", "oz"), Type.WEIGHT, 28.3495),
    CELSIUS(listOf("degrees Celsius", "degree Celsius", "c", "dc", "celsius"), Type.TEMPERATURE, null),
    FAHRENHEIT(listOf("degrees fahrenheit", "degree fahrenheit", "f", "df", "fahrenheit"), Type.TEMPERATURE, null),
    KELVINS(listOf("kelvins", "kelvin", "k"), Type.TEMPERATURE, null),
    UNKNOWN(listOf("???"), Type.ERROR, null)
}

fun main() {
    while (true) {
        println("Enter what you want to convert (or exit):")
        val input = readln().lowercase().replace("degrees", "").replace("degree", "")
        var inputList: MutableList<String> = input.split(" ").toMutableList()

        inputList.removeAll { it == "degree" }
        inputList.removeAll { it == "degrees" }
        inputList = inputList.filter { it.isNotBlank() }.toMutableList()

        if (input == "exit") break

        val amount = inputList[0].toDoubleOrNull()
        if (amount == null) {
            println("Parse error")
            continue
        }

        convertAndPrint(amount, getUnitName(inputList[1]), getUnitName(inputList.last()))
    }
    exitProcess(0)
}

fun getUnitName(input: String): AllUnits {
    for (i in AllUnits.values()) {
        if (i.synonyms.contains(input)) {
            return i
        }
    }
    return AllUnits.UNKNOWN
}

fun convertAndPrint(amount: Double, unit: AllUnits, toUnit: AllUnits) {
    if (unit.type == Type.ERROR || toUnit.type == Type.ERROR || unit.type != toUnit.type) {
        println("Conversion from ${unit.synonyms[0]} to ${toUnit.synonyms[0]} is impossible")
        return
    }

    if ((unit.type == Type.LENGTH || unit.type == Type.WEIGHT) && amount < 0.0) {
        println("${unit.type.toString().lowercase().replaceFirstChar(Char::titlecase)} shouldn't be negative")
        return
    }

    var converted = 0.0

    if (unit.type == Type.TEMPERATURE) {
        when (unit.synonyms[2]) {
            "c" -> {
                when (toUnit.synonyms[2]) {
                    "f" -> {
                        converted = amount * 9 / 5 + 32
                    }

                    "k" -> {
                        converted = amount + 273.15
                    }

                    "c" -> {
                        converted = amount
                    }
                }
            }

            "f" -> {
                when (toUnit.synonyms[2]) {
                    "c" -> {
                        converted = (amount - 32) * 5 / 9
                    }

                    "k" -> {
                        converted = (amount + 459.67) * 5 / 9
                    }

                    "f" -> {
                        converted = amount
                    }
                }

            }

            "k" -> {
                when (toUnit.synonyms[2]) {
                    "f" -> {
                        converted = amount * 9 / 5 - 459.67
                    }

                    "c" -> {
                        converted = amount - 273.15
                    }

                    "k" -> {
                        converted = amount
                    }
                }
            }
        }
    } else {
        converted = amount / toUnit.conversion!! * unit.conversion!!
    }

    println(
        "$amount ${if (amount != 1.0) unit.synonyms[0] else unit.synonyms[1]} is " +
                "$converted ${if (converted != 1.0) toUnit.synonyms[0] else toUnit.synonyms[1]}"
    )
    println()
}
