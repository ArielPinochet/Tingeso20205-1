
import axios from "axios";

const API_URL = "http://localhost:8080/clientes"; // URL de tu backend

const obtenerClientes = () => axios.get(API_URL);
const obtenerClientePorId = (id) => axios.get(`${API_URL}/${id}`);
const crearCliente = (cliente) => axios.post(API_URL, cliente);
const actualizarCliente = (id, cliente) => axios.put(`${API_URL}/${id}`, cliente);
const eliminarCliente = (id) => axios.delete(`${API_URL}/${id}`);

export { obtenerClientes, obtenerClientePorId, crearCliente, actualizarCliente, eliminarCliente };
