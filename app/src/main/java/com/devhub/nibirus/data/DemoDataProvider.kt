package com.devhub.nibirus.data

data class DemoCity(val id: Int, val name: String, val department: String) {
    val label: String get() = "$name ($department)"
}

object DemoDataProvider {
    val cities = listOf(
        DemoCity(1, "Lima", "Lima"),
        DemoCity(2, "Arequipa", "Arequipa"),
        DemoCity(3, "Cusco", "Cusco"),
        DemoCity(4, "Trujillo", "La Libertad"),
        DemoCity(5, "Chiclayo", "Lambayeque"),
        DemoCity(6, "Piura", "Piura"),
        DemoCity(7, "Iquitos", "Loreto"),
        DemoCity(8, "Huancayo", "Junín"),
        DemoCity(9, "Huánuco", "Huánuco"),
        DemoCity(10, "Tarapoto", "San Martín"),
        DemoCity(11, "Puno", "Puno"),
        DemoCity(12, "Huaraz", "Áncash")
    )
}
