
import axios from "axios";

const API_URL = "http://localhost:8090";


const CLIENTES_URL = `${API_URL}/clientes`;

const obtenerClientes = () => axios.get(CLIENTES_URL);
const obtenerClientePorId = (id) => axios.get(`${CLIENTES_URL}/${id}`);
const crearCliente = (cliente) => axios.post(CLIENTES_URL, cliente);
const actualizarCliente = (id, cliente) => axios.put(`${CLIENTES_URL}/${id}`, cliente);
const eliminarCliente = (id) => axios.delete(`${CLIENTES_URL}/${id}`);

export { obtenerClientes, obtenerClientePorId, crearCliente, actualizarCliente, eliminarCliente };
