import axios from "axios";

const API_URL = "http://localhost:8080";


const CLIENTES_URL = `${API_URL}/api/clientes`;

const obtenerClientes = () => axios.get(CLIENTES_URL);
const obtenerClientePorId = (id) => axios.get(`${CLIENTES_URL}/${id}`);
const crearCliente = async (cliente) => {
  const url = `${CLIENTES_URL}/?nombre=${encodeURIComponent(cliente.nombre)}&email=${encodeURIComponent(cliente.email)}`;
  const response = await fetch(url, {
    method: "POST",
    headers: {
      "Content-Type": "application/x-www-form-urlencoded"
    }
  });
  if (!response.ok) {
    const errorData = await response.text();
    throw new Error(errorData);
  }
  return response.json();
};
const actualizarCliente = (id, cliente) => axios.put(`${CLIENTES_URL}/${id}`, cliente);
const eliminarCliente = (id) => axios.delete(`${CLIENTES_URL}/${id}`);
const editarCorreoCliente = (nombre, emailNuevo) =>
  axios.put(`${CLIENTES_URL}/editar-correo?nombre=${encodeURIComponent(nombre)}&emailNuevo=${encodeURIComponent(emailNuevo)}`);

export { obtenerClientes, obtenerClientePorId, crearCliente, actualizarCliente, eliminarCliente, editarCorreoCliente };
