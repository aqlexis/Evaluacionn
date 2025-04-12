package com.example.tarea

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tarea.ui.theme.TareaTheme
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TareaTheme {
                AppTiendaProductos()
            }
        }
    }
}

// Modelo de datos
data class Usuario(val usuario: String, val password: String)
data class Producto(val id: Int, val nombre: String, val imagen: Int, val precio: Double, var cantidad: Int = 0)

// Lista de usuarios registrados (en una aplicación real, esto estaría en una base de datos)
val usuariosRegistrados = mutableStateListOf(
    Usuario("admin", "admin123"),
    Usuario("usuario", "123456")
)

// Lista de productos
val listaProductos = listOf(
    Producto(1, "Arroz", R.drawable.arroz, 2.50),
    Producto(2, "Azúcar", R.drawable.azucar, 1.80),
    Producto(3, "Tomates", R.drawable.tomates, 1.20),
    Producto(4, "Papas", R.drawable.papas, 2.30),
    Producto(5, "Aceite", R.drawable.aceite, 3.50),
    Producto(6, "Fideos", R.drawable.fideos, 1.10),
    Producto(7, "Leche", R.drawable.leche, 1.40),
    Producto(8, "Pan", R.drawable.pan, 0.90),
    Producto(9, "Huevos", R.drawable.huevos, 2.80),
    Producto(10, "Sal", R.drawable.sal, 0.75)
)

@Composable
fun AppTiendaProductos() {
    val navController = rememberNavController()

    // Se crea una única lista de productos que será compartida entre las pantallas
    // Usando mutableStateListOf para que los cambios desencadenen recomposiciones
    val productosConCantidad = remember {
        mutableStateListOf<Producto>().apply {
            addAll(listaProductos.map { it.copy() })
        }
    }

    // Función para actualizar la cantidad de manera más eficiente
    val actualizarCantidad = { productoId: Int, incremento: Int ->
        val indice = productosConCantidad.indexOfFirst { it.id == productoId }
        if (indice >= 0) {
            productosConCantidad[indice] = productosConCantidad[indice].copy(
                cantidad = (productosConCantidad[indice].cantidad + incremento).coerceAtLeast(0)
            )
        }
    }

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController)
        }
        composable("registro") {
            RegistroScreen(navController)
        }
        composable("productos") {
            ProductosScreen(
                navController = navController,
                productos = productosConCantidad,
                actualizarCantidad = actualizarCantidad
            )
        }
        composable("carrito") {
            CarritoScreen(
                navController = navController,
                productos = productosConCantidad,
                actualizarCantidad = actualizarCantidad
            )
        }
    }
}

@Composable
fun LoginScreen(navController: NavController) {
    var usuario by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Iniciar Sesión",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = usuario,
            onValueChange = { usuario = it },
            label = { Text("Usuario") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Button(
            onClick = {
                val usuarioEncontrado = usuariosRegistrados.find {
                    it.usuario == usuario && it.password == password
                }

                if (usuarioEncontrado != null) {
                    navController.navigate("productos")
                } else {
                    errorMessage = "Usuario o contraseña incorrectos"
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Iniciar Sesión")
        }

        TextButton(
            onClick = { navController.navigate("registro") }
        ) {
            Text("¿No tienes una cuenta? Regístrate")
        }
    }
}

@Composable
fun RegistroScreen(navController: NavController) {
    var usuario by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmarPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Registro de Usuario",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = usuario,
            onValueChange = { usuario = it },
            label = { Text("Usuario") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = confirmarPassword,
            onValueChange = { confirmarPassword = it },
            label = { Text("Confirmar Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Button(
            onClick = {
                when {
                    usuario.isEmpty() || password.isEmpty() || confirmarPassword.isEmpty() -> {
                        errorMessage = "Todos los campos son obligatorios"
                    }
                    password != confirmarPassword -> {
                        errorMessage = "Las contraseñas no coinciden"
                    }
                    usuariosRegistrados.any { it.usuario == usuario } -> {
                        errorMessage = "El usuario ya existe, elija otro nombre"
                    }
                    else -> {
                        usuariosRegistrados.add(Usuario(usuario, password))
                        navController.navigate("login")
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Registrarse")
        }

        TextButton(
            onClick = { navController.navigate("login") }
        ) {
            Text("¿Ya tienes una cuenta? Inicia sesión")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductosScreen(
    navController: NavController,
    productos: List<Producto>,
    actualizarCantidad: (Int, Int) -> Unit
) {
    // Usar derivedStateOf para calcular el total solo cuando cambian las cantidades
    val totalProductos by remember {
        derivedStateOf { productos.sumOf { it.cantidad } }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Productos") },
                actions = {
                    Box {
                        IconButton(onClick = { navController.navigate("carrito") }) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Carrito"
                            )
                        }

                        // Mostrar la cantidad total de productos en el carrito
                        if (totalProductos > 0) {
                            Badge(
                                modifier = Modifier
                                    .offset(x = (-8).dp, y = 8.dp)
                                    .align(Alignment.TopEnd)
                            ) {
                                Text(
                                    text = if (totalProductos > 99) "99+" else totalProductos.toString(),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(
                start = 12.dp,
                top = paddingValues.calculateTopPadding() + 8.dp,
                end = 12.dp,
                bottom = 16.dp
            ),
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = productos,
                key = { it.id } // Usar ID como clave estable para evitar recomposiciones innecesarias
            ) { producto ->
                key(producto.id) { // Key composable para mejor rendimiento
                    ProductoItem(
                        producto = producto,
                        onAgregarClick = {
                            // Usamos la función de actualización en lugar de modificar directamente
                            actualizarCantidad(producto.id, 1)
                        },
                        onDisminuirClick = {
                            if (producto.cantidad > 0) {
                                actualizarCantidad(producto.id, -1)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ProductoItem(
    producto: Producto,
    onAgregarClick: () -> Unit,
    onDisminuirClick: () -> Unit
) {
    // Usar derivedStateOf para mejorar el rendimiento, evitando recomposiciones innecesarias
    val cantidad by remember(producto.cantidad) { derivedStateOf { producto.cantidad } }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Imagen del producto
            Image(
                painter = painterResource(id = producto.imagen),
                contentDescription = producto.nombre,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Nombre del producto
            Text(
                text = producto.nombre,
                fontWeight = FontWeight.Bold
            )

            // Precio
            Text(
                text = "$${String.format(Locale.US, "%.2f", producto.precio)}",
                color = MaterialTheme.colorScheme.primary
            )

            // Controles de cantidad
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                // Botón para disminuir
                Button(
                    onClick = onDisminuirClick,
                    modifier = Modifier.size(36.dp),
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(4.dp),
                    // Deshabilitar el botón si la cantidad es 0
                    enabled = cantidad > 0
                ) {
                    Text(
                        text = "-",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Mostrar la cantidad
                Text(
                    text = "$cantidad",
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                // Botón para aumentar
                Button(
                    onClick = onAgregarClick,
                    modifier = Modifier.size(36.dp),
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "+",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarritoScreen(
    navController: NavController,
    productos: List<Producto>,
    actualizarCantidad: (Int, Int) -> Unit
) {
    val productosEnCarrito = productos.filter { it.cantidad > 0 }
    val totalCarrito = productosEnCarrito.sumOf { it.precio * it.cantidad }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Carrito de Compras") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (productosEnCarrito.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay productos en el carrito",
                        fontSize = 18.sp
                    )
                }
            } else {
                // Lista de productos seleccionados con scroll
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    productosEnCarrito.forEach { producto ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = producto.imagen),
                                contentDescription = producto.nombre,
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                contentScale = ContentScale.Crop
                            )

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 16.dp)
                            ) {
                                Text(
                                    text = producto.nombre,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "$${String.format(Locale.US, "%.2f", producto.precio)}",
                                )
                            }

                            // Mostrar solo la cantidad sin botones de control
                            Text(
                                text = "Cantidad: ${producto.cantidad}",
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "$${String.format(Locale.US, "%.2f", producto.precio * producto.cantidad)}",
                                fontWeight = FontWeight.Bold
                            )
                        }

                        HorizontalDivider()
                    }
                }

                // Total
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                    ) {
                        // Total de productos
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Productos:",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            val cantidadTotal = productosEnCarrito.sumOf { it.cantidad }
                            Text(
                                text = "$cantidadTotal ${if (cantidadTotal == 1) "producto" else "productos"}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Línea divisoria
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        // Total monetario
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Total:",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "$" + String.format(Locale.US, "%.2f", totalCarrito),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Botones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Botón para vaciar el carrito
                    OutlinedButton(
                        onClick = {
                            productosEnCarrito.forEach { actualizarCantidad(it.id, -it.cantidad) }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Vaciar Carrito")
                    }

                    // Botón de finalizar compra
                    Button(
                        onClick = {
                            // Reiniciar las cantidades de todos los productos usando la función de actualización
                            productosEnCarrito.forEach { actualizarCantidad(it.id, -it.cantidad) }
                            navController.navigate("productos")
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Finalizar Compra")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    TareaTheme {
        LoginScreen(rememberNavController())
    }
}

@Preview(showBackground = true)
@Composable
fun ProductosScreenPreview() {
    TareaTheme {
        val productosPrueba = remember {
            mutableStateListOf<Producto>().apply {
                addAll(listaProductos.map { it.copy() })
            }
        }

        // Función simulada para el preview
        val actualizarCantidadSimulada: (Int, Int) -> Unit = { _, _ -> }

        ProductosScreen(
            navController = rememberNavController(),
            productos = productosPrueba,
            actualizarCantidad = actualizarCantidadSimulada
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CarritoScreenPreview() {
    TareaTheme {
        val productosPrueba = remember {
            mutableStateListOf<Producto>().apply {
                addAll(listaProductos.map { it.copy() })
                // Añadir algunas cantidades para el preview
                this[0] = this[0].copy(cantidad = 2)
                this[1] = this[1].copy(cantidad = 1)
            }
        }

        // Función simulada para el preview
        val actualizarCantidadSimulada: (Int, Int) -> Unit = { _, _ -> }

        CarritoScreen(
            navController = rememberNavController(),
            productos = productosPrueba,
            actualizarCantidad = actualizarCantidadSimulada
        )
    }
}