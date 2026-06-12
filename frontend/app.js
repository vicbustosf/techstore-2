const API_BASE = "http://localhost:8080";

function obtenerToken() {
    return localStorage.getItem("token");
}

async function login() {
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;
    const estadoLogin = document.getElementById("estadoLogin");

    try {
        const respuesta = await fetch(`${API_BASE}/api/auth/login`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                username: username,
                password: password
            })
        });

        if (!respuesta.ok) {
            estadoLogin.textContent = "Error al iniciar sesión.";
            estadoLogin.style.color = "red";
            return;
        }

        const datos = await respuesta.json();

        localStorage.setItem("token", datos.token);

        estadoLogin.textContent = `Sesión iniciada como ${datos.username}`;
        estadoLogin.style.color = "green";

        listarProductos();

    } catch (error) {
        estadoLogin.textContent = "No se pudo conectar con la API.";
        estadoLogin.style.color = "red";
        console.error(error);
    }
}

async function listarProductos() {
    const tabla = document.getElementById("tablaProductos");
    const token = obtenerToken();

    if (!token) {
        tabla.innerHTML = `
            <tr>
                <td colspan="7">Debes iniciar sesión para ver los productos.</td>
            </tr>
        `;
        return;
    }

    try {
        const respuesta = await fetch(`${API_BASE}/api/productos`, {
            method: "GET",
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });

        if (!respuesta.ok) {
            tabla.innerHTML = `
                <tr>
                    <td colspan="7">No autorizado o error al cargar productos.</td>
                </tr>
            `;
            return;
        }

        const productos = await respuesta.json();

        if (productos.length === 0) {
            tabla.innerHTML = `
                <tr>
                    <td colspan="7">No hay productos activos.</td>
                </tr>
            `;
            return;
        }

        tabla.innerHTML = "";

        productos.forEach(producto => {
            tabla.innerHTML += `
                <tr>
                    <td>${producto.id}</td>
                    <td>${producto.nombre}</td>
                    <td>${producto.descripcion}</td>
                    <td>$${producto.precio}</td>
                    <td>${producto.stock}</td>
                    <td>${producto.categoria}</td>
                    <td>
                        <div class="acciones">
                            <button class="editar" onclick="cargarProductoEnFormulario(${producto.id}, '${producto.nombre}', '${producto.descripcion}', ${producto.precio}, ${producto.stock}, '${producto.categoria}')">
                                Editar
                            </button>
                            <button class="eliminar" onclick="eliminarProducto(${producto.id})">
                                Eliminar
                            </button>
                        </div>
                    </td>
                </tr>
            `;
        });

    } catch (error) {
        tabla.innerHTML = `
            <tr>
                <td colspan="7">Error de conexión con la API.</td>
            </tr>
        `;
        console.error(error);
    }
}

async function guardarProducto() {
    const token = obtenerToken();

    if (!token) {
        alert("Debes iniciar sesión primero.");
        return;
    }

    const id = document.getElementById("productoId").value;

    const producto = {
        nombre: document.getElementById("nombre").value,
        descripcion: document.getElementById("descripcion").value,
        precio: Number(document.getElementById("precio").value),
        stock: Number(document.getElementById("stock").value),
        categoria: document.getElementById("categoria").value,
        activo: true
    };

    const url = id
        ? `${API_BASE}/api/productos/${id}`
        : `${API_BASE}/api/productos`;

    const metodo = id ? "PUT" : "POST";

    try {
        const respuesta = await fetch(url, {
            method: metodo,
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify(producto)
        });

        if (!respuesta.ok) {
            alert("No se pudo guardar el producto.");
            return;
        }

        limpiarFormulario();
        listarProductos();

        if (id) {
            alert("Producto actualizado correctamente.");
        } else {
            alert("Producto creado correctamente.");
        }

    } catch (error) {
        alert("Error de conexión con la API.");
        console.error(error);
    }
}

function cargarProductoEnFormulario(id, nombre, descripcion, precio, stock, categoria) {
    document.getElementById("productoId").value = id;
    document.getElementById("nombre").value = nombre;
    document.getElementById("descripcion").value = descripcion;
    document.getElementById("precio").value = precio;
    document.getElementById("stock").value = stock;
    document.getElementById("categoria").value = categoria;
}

async function eliminarProducto(id) {
    const token = obtenerToken();

    if (!token) {
        alert("Debes iniciar sesión primero.");
        return;
    }

    const confirmar = confirm("¿Seguro que deseas eliminar este producto?");

    if (!confirmar) {
        return;
    }

    try {
        const respuesta = await fetch(`${API_BASE}/api/productos/${id}`, {
            method: "DELETE",
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });

        if (!respuesta.ok) {
            alert("No se pudo eliminar el producto.");
            return;
        }

        listarProductos();
        alert("Producto eliminado correctamente.");

    } catch (error) {
        alert("Error de conexión con la API.");
        console.error(error);
    }
}

function limpiarFormulario() {
    document.getElementById("productoId").value = "";
    document.getElementById("nombre").value = "";
    document.getElementById("descripcion").value = "";
    document.getElementById("precio").value = "";
    document.getElementById("stock").value = "";
    document.getElementById("categoria").value = "";
}